package com.islandstudio.xenon.shared.di

import org.koin.core.parameter.ParametersHolder

interface IComponentProvider {
    fun getKoin(): org.koin.core.Koin {
        return PluginDependencyInjectionManager.getKoin()
    }
}

inline fun <reified T : Any> IComponentProvider.injectComponent(): Lazy<T> {
    return getKoin().inject<T>()
}

inline fun <reified T: Any> IComponentProvider.getComponent(vararg parameters: Any? = emptyArray()): T {
    return getKoin().get<T>(parameters = { ParametersHolder(parameters.toMutableList()) })
}
