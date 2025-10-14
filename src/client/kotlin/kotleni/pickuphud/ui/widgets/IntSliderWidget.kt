package kotleni.pickuphud.ui.widgets

import net.minecraft.client.gui.widget.SliderWidget
import net.minecraft.text.Text

class IntSliderWidget(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    private val text: Text,
    private val initialValue: Int,
    private val min: Int,
    private val max: Int,
    private val onChangeValue: (value: Int) -> Unit = { }
) : SliderWidget(x, y, width, height, text, (initialValue - min).toDouble() / (max - min)) {
    val actualValue: Int get() = (value * (max - min)).toInt() + min

    init {
        updateMessage()
    }

    override fun updateMessage() {
        this.message = Text.literal("Value: $actualValue")
    }

    override fun applyValue() {
        onChangeValue(actualValue)
    }
}