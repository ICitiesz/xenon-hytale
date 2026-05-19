package com.islandstudio.xenon.experimental

import com.hypixel.hytale.component.ArchetypeChunk
import com.hypixel.hytale.component.CommandBuffer
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.component.query.Query
import com.hypixel.hytale.component.system.tick.EntityTickingSystem
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore

class PlayerLookSystem: EntityTickingSystem<EntityStore>() {
    companion object {
        val QUERY = Query.and(Player.getComponentType(), TransformComponent.getComponentType())
    }

    override fun tick(
        dt: Float,
        index: Int,
        archetypeChunk: ArchetypeChunk<EntityStore?>,
        store: Store<EntityStore?>,
        commandBuffer: CommandBuffer<EntityStore?>
    ) {
        val transformComponents = archetypeChunk.getComponent(index, TransformComponent.getComponentType())
    }

    override fun getQuery(): Query<EntityStore?> = QUERY
}