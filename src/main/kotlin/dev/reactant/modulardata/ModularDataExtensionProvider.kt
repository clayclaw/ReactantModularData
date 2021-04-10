package dev.reactant.modulardata

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataHolder

fun ItemStack.getOrCreateItemMeta(): ItemMeta {
    if (!this.hasItemMeta()) {
        if (this.type === Material.AIR) throw UnsupportedOperationException("Air do not have item meta")
        this.itemMeta = Bukkit.getItemFactory().getItemMeta(this.type)
    }
    return this.itemMeta!!
}

/**
 * The changes of the itemMeta after this accessor created will be lost once the module changes commited
 * Base on the above rule:
 *
 *      - you should not save the this accessor for further use;
 *
 *      - you should not do any itemMeta changes inside .mutate{ }, but you can do the following:
 *          val exampleModule = itemStack.modules.getModule<Example>();
 *          /** some ItemMeta changes of the itemStack **/
 *          itemStack.modules.upsert(exampleModule)
 *
 */
val ItemStack.modules: DataModulesAccessor
    get() {
        val itemMeta = this.getOrCreateItemMeta()
        return DataModulesAccessor(
            { itemMeta.persistentDataContainer }, { this.setItemMeta(itemMeta) }
        )
    }

val PersistentDataHolder.modules get() = DataModulesAccessor({ this.persistentDataContainer }, {})
