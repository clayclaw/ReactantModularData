package dev.reactant.modulardata

import dev.reactant.reactant.extra.parser.GsonJsonParserService
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation


fun getModuleKey(clazz: KClass<*>) = NamespacedKey(
    ModularDataLoader.getINSTANCE(),
    clazz.findAnnotation<PersistentDataKey>()?.key ?: clazz.qualifiedName!!
)


class DataModulesAccessor(
    val dataContainerGetter: () -> PersistentDataContainer,
    val commitChanges: () -> Any
) {
    protected val parser get() = ModularItemGsonGetter.gsonParser

    /**
     * Check if a module exist
     */
    inline fun <reified T: Any> has(): Boolean {
        return this.dataContainerGetter().has(getModuleKey(T::class), PersistentDataType.STRING)
    }

    /**
     * Get a module, null if not exist
     */
    inline fun <reified T : Any> get(): T? {
        return this.dataContainerGetter().get(getModuleKey(T::class), PersistentDataType.STRING)
            ?.let { `access$parser`.decode(it, T::class).blockingGet() }
    }

    inline fun <reified T : Any> createIfNotExist(moduleFactory: () -> T): DataModulesAccessor {
        val key = getModuleKey(T::class)
        if (!this.dataContainerGetter().has(key, PersistentDataType.STRING)) {
            this.upsertModule(moduleFactory())
            this.commitChanges();
        }
        return this
    }

    /**
     * Get a module, create and insert if not exist
     */
    inline fun <reified T : Any> getOrPut(moduleFactory: () -> T): T {
        this.createIfNotExist(moduleFactory)
        return this.get<T>()!!
    }

    /**
     * Get a module, return default if not exist
     */
    inline fun <reified T : Any> getOrDefault(moduleFactory: () -> T): T {
        val key = getModuleKey(T::class)
        return if (this.dataContainerGetter().has(key, PersistentDataType.STRING)) moduleFactory() else this.get<T>()!!
    }

    /**
     * Update or insert a module in the holder
     */
    inline fun <reified T : Any> upsertModule(module: T): DataModulesAccessor {
        this.dataContainerGetter().set(
            getModuleKey(T::class),
            PersistentDataType.STRING,
            `access$parser`.encode(module, false).blockingGet()
        )
        this.commitChanges();
        return this
    }

    /**
     * Mutate a module in the holder if module exist
     *
     * Keep the mutation function atomic, nested upsert/mutation on same module will cause overwrite issues
     * Remind that you should not do any async changes inside the mutation, those changes will not be committed
     */
    inline fun <reified T : Any> mutateModule(mutation: (T) -> Any): DataModulesAccessor {
        this.get<T>()?.also {
            mutation(it)
            this.upsertModule(it)
        }
        this.commitChanges();
        return this
    }

    inline fun <reified T : Any> removeModule(): DataModulesAccessor {
        this.dataContainerGetter().remove(getModuleKey(T::class))
        this.commitChanges();
        return this;
    }

    @PublishedApi
    internal val `access$parser`: GsonJsonParserService
        get() = parser
}
