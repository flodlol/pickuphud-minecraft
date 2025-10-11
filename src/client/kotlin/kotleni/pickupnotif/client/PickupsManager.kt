package kotleni.pickupnotif.client

import net.minecraft.item.ItemStack

class PickupsManager {
    private var pickupMessages: ArrayList<PickupMessage> = arrayListOf();

    val allPickups: List<PickupMessage> get() = pickupMessages

    private fun cleanup() {
        pickupMessages.removeIf { System.currentTimeMillis() - it.createTime >= 2000 }
    }

    fun addPickup(stack: ItemStack, totalItemsOfThisType: Int) {
        cleanup()

        val prevMessage = pickupMessages.find { it.itemName == stack.itemName.string }
        if(prevMessage != null) {
            prevMessage.itemCount += stack.count;
            prevMessage.createTime = System.currentTimeMillis();
        } else {
            pickupMessages.add(PickupMessage(
                stack.name.string,
                stack.count,
                totalItemsOfThisType,
                System.currentTimeMillis(),
                stack
            ))
        }
    }
}