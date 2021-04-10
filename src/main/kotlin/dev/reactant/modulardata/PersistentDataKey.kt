package dev.reactant.modulardata

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class PersistentDataKey(val key: String)
