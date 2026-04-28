package com.islandstudio.xenon.shared.config

import kotlin.reflect.full.declaredMemberProperties

abstract class AbstractConfigSection(open val sectionKey: String = ROOT_NODE_KEY): IConfigDescriptor {
    companion object {
        const val ROOT_NODE_KEY = "rootNode"
    }
    fun getAllConfigEntry(): List<AbstractConfigEntry<*>> {
        return this::class.declaredMemberProperties
            .filter { it.returnType.classifier == AbstractConfigEntry::class }
            .map { it.getter.call(this) as AbstractConfigEntry<*> }
            .toList()
    }
}