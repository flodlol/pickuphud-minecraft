package kotleni.pickupnotif.client

import net.minecraft.item.ItemStack

data class PickupMessage(
    val itemName: String,
    var itemCount: Int,
    var itemsTotal: Int,
    var createTime: Long,
    val stack: ItemStack
)