package com.islandstudio.xenon.shared.di

import com.islandstudio.xenon.shared.di.PluginDependencyInjectionManager.getKoin
import org.koin.core.parameter.ParametersHolder

interface IComponentProvider

inline fun <reified T : Any> IComponentProvider.injectComponent(): Lazy<T> {
    return getKoin().inject<T>()
}

inline fun <reified T: Any> IComponentProvider.getComponent(vararg parameters: Any? = emptyArray()): T {
    return getKoin().get<T>(parameters = { ParametersHolder(parameters.toMutableList()) })
}
