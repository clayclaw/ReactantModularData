package dev.reactant.modulardata

import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook
import dev.reactant.reactant.core.dependency.layers.SystemLevel
import dev.reactant.reactant.extra.parser.GsonJsonParserService

@Component
internal class ModularItemGsonGetter(
    private val gsonJsonParserService: GsonJsonParserService
) : LifeCycleHook, SystemLevel {
    override fun onEnable() {
        _gsonParser = gsonJsonParserService
    }

    companion object {
        private var _gsonParser: GsonJsonParserService? = null
        val gsonParser
            get() = _gsonParser
                ?: throw IllegalStateException("Gson parse only available after System Level initialized")
    }

}
