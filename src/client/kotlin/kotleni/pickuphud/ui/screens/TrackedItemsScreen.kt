package kotleni.pickuphud.ui.screens

import kotleni.pickuphud.ModConfig
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class TrackedItemsScreen(
    private val parent: Screen?,
    private val config: ModConfig,
) : Screen(Text.literal("Tracked Items")) {
    private val allItemIds: List<Identifier> = Registries.ITEM.ids.toList().sortedBy { it.toString() }
    private val filteredResults = mutableListOf<Identifier>()
    private val trackedIds = linkedSetOf<Identifier>()

    private var selectedId: Identifier? = null
    private var searchQuery: String = ""
    private var scrollOffset = 0

    private val listX: Int get() = width / 2 - 205
    private val listY: Int get() = 66
    private val listWidth: Int get() = 410
    private val rowHeight: Int get() = 24
    private val listBottom: Int get() = height - 84
    private val rowsPerPage: Int get() = maxOf(1, (listBottom - listY) / rowHeight)

    override fun init() {
        if (trackedIds.isEmpty()) {
            config.trackedItemIdsCsv
                .split(',')
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .forEach { raw ->
                    val id = Identifier.tryParse(raw) ?: return@forEach
                    if (Registries.ITEM.containsId(id)) trackedIds.add(id)
                }
        }

        refreshFiltered()
        rebuildWidgets()
    }

    private fun refreshFiltered() {
        filteredResults.clear()
        if (searchQuery.isBlank()) {
            filteredResults.addAll(allItemIds)
        } else {
            val query = searchQuery.lowercase()
            filteredResults.addAll(allItemIds.filter { id ->
                val itemName = Registries.ITEM.get(id).name.string.lowercase()
                id.toString().contains(query) || itemName.contains(query)
            })
        }

        filteredResults.sortWith(
            compareByDescending<Identifier> { trackedIds.contains(it) }
                .thenBy { Registries.ITEM.get(it).name.string.lowercase() }
                .thenBy { it.toString() }
        )

        val maxOffset = maxOf(0, filteredResults.size - rowsPerPage)
        scrollOffset = scrollOffset.coerceIn(0, maxOffset)
    }

    private fun syncToConfig() {
        config.trackedItemIdsCsv = trackedIds.joinToString(",") { it.toString() }
    }

    private fun toggleTracked(id: Identifier) {
        if (!trackedIds.remove(id)) trackedIds.add(id)
        syncToConfig()
        refreshFiltered()
    }

    private fun ensureTracked(id: Identifier) {
        if (trackedIds.add(id)) {
            syncToConfig()
            refreshFiltered()
        }
    }

    private fun rebuildWidgets() {
        clearChildren()

        val searchField = TextFieldWidget(textRenderer, listX, 40, listWidth, 20, Text.literal("Search items"))
        searchField.setMaxLength(128)
        searchField.text = searchQuery
        searchField.setChangedListener { value ->
            searchQuery = value
            refreshFiltered()
            rebuildWidgets()
        }
        addDrawableChild(searchField)

        val pageRows = filteredResults.drop(scrollOffset).take(rowsPerPage)
        pageRows.forEachIndexed { idx, id ->
            addDrawableChild(
                ButtonWidget.Builder(Text.empty()) {
                    selectedId = id
                    ensureTracked(id)
                    rebuildWidgets()
                }
                    .dimensions(listX, listY + idx * rowHeight, listWidth, rowHeight)
                    .build()
            )
        }

        addDrawableChild(
            ButtonWidget.Builder(Text.literal("Toggle Selected")) {
                selectedId?.let {
                    toggleTracked(it)
                    rebuildWidgets()
                }
            }
                .dimensions(width / 2 - 205, height - 52, 200, 20)
                .build()
        )
        addDrawableChild(
            ButtonWidget.Builder(Text.literal("Clear Tracked")) {
                trackedIds.clear()
                syncToConfig()
                rebuildWidgets()
            }
                .dimensions(width / 2 + 5, height - 52, 200, 20)
                .build()
        )
        addDrawableChild(
            ButtonWidget.Builder(Text.literal("Back")) { close() }
                .dimensions(width / 2 - 205, height - 28, 410, 20)
                .build()
        )
    }

    override fun close() {
        if (parent == null) {
            ModConfig.save()
        }
        client?.setScreen(parent)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, horizontalAmount: Double, verticalAmount: Double): Boolean {
        if (mouseX < listX || mouseX > listX + listWidth || mouseY < listY || mouseY > listBottom) {
            return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
        }

        val maxOffset = maxOf(0, filteredResults.size - rowsPerPage)
        if (verticalAmount < 0 && scrollOffset < maxOffset) {
            scrollOffset++
            rebuildWidgets()
            return true
        }
        if (verticalAmount > 0 && scrollOffset > 0) {
            scrollOffset--
            rebuildWidgets()
            return true
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, deltaTicks: Float) {
        super.render(context, mouseX, mouseY, deltaTicks)

        val pageRows = filteredResults.drop(scrollOffset).take(rowsPerPage)
        pageRows.forEachIndexed { idx, id ->
            val y = listY + idx * rowHeight
            val item = Registries.ITEM.get(id)
            val isTracked = trackedIds.contains(id)
            val isSelected = selectedId == id
            val bg = when {
                isSelected -> 0xAA2979FF.toInt()
                isTracked -> 0xAA235C2E.toInt()
                else -> 0x88303030.toInt()
            }

            context.fill(listX, y, listX + listWidth, y + rowHeight - 1, bg)
            context.drawItem(ItemStack(item), listX + 4, y + 2)

            context.drawTextWithShadow(textRenderer, item.name, listX + 24, y + 4, 0xFFFFFFFF.toInt())
            context.drawTextWithShadow(textRenderer, Text.literal(id.toString()), listX + 24, y + 14, 0xFFA8A8A8.toInt())
            context.drawText(
                textRenderer,
                Text.literal(if (isTracked) "TOGGLED" else "UNTOGGLED"),
                listX + listWidth - 88,
                y + 8,
                if (isTracked) 0xFFA7FFA7.toInt() else 0xFFFFB7B7.toInt(),
                false,
            )
        }

        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Tracked Items"), width / 2, 16, 0xFFFFFFFF.toInt())
        context.drawCenteredTextWithShadow(
            textRenderer,
            Text.literal("Tracked Items: ${trackedIds.size}"),
            width / 2,
            height - 74,
            0xFFB0F0B0.toInt(),
        )
        context.drawCenteredTextWithShadow(
            textRenderer,
            Text.literal("Click any item row to track item"),
            width / 2,
            height - 62,
            0xFFCFCFCF.toInt(),
        )
    }
}
