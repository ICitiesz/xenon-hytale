package com.islandstudio.xenon

import com.hypixel.hytale.component.*
import com.hypixel.hytale.component.query.Query
import com.hypixel.hytale.component.system.EntityEventSystem
import com.hypixel.hytale.math.vector.Vector3d
import com.hypixel.hytale.protocol.Color
import com.hypixel.hytale.protocol.Direction
import com.hypixel.hytale.protocol.Vector3f
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.asset.type.particle.config.WorldParticle
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent
import com.hypixel.hytale.server.core.event.events.player.PlayerMouseMotionEvent
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent
import com.hypixel.hytale.server.core.plugin.JavaPlugin
import com.hypixel.hytale.server.core.plugin.JavaPluginInit
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.ParticleUtil
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import com.islandstudio.xenon.experimental.PlayerObservedObjectSystem
import com.islandstudio.xenon.shared.di.PluginDependencyInjectionManager
import com.islandstudio.xenon.shared.init.context.PluginContext


class Xenon(init: JavaPluginInit) : JavaPlugin(init) {
    private val pluginContext = PluginContext(this)

    protected override fun setup() {
        PluginDependencyInjectionManager.startAsPluginScoped(pluginContext)

        this.entityStoreRegistry.registerSystem(BlockBreakEventSystem())
        PlayerObservedObjectSystem.run()
    }

    override fun shutdown() {
        pluginContext.packetFilterRegistry.deregisterAllPlayerPacketFilter()
        PluginDependencyInjectionManager.dispose()
    }

    class BlockBreakEventSystem: EntityEventSystem<EntityStore, BreakBlockEvent>(BreakBlockEvent::class.java) {
        override fun handle(
            index: Int,
            achetypeChunk: ArchetypeChunk<EntityStore?>,
            store: Store<EntityStore?>,
            commandBuffer: CommandBuffer<EntityStore?>,
            event: BreakBlockEvent
        ) {
            val entityStoreRef = achetypeChunk.getReferenceTo(index)
            val player = store.getComponent(entityStoreRef, Player.getComponentType())
            val playerRef = player?.reference



            playerRef?.store?.let {
                val playerTransform = it.getComponent(playerRef, TransformComponent.getComponentType())
                    ?: return@let

                val modelComponent = it.getComponent(playerRef, ModelComponent.getComponentType()) as ModelComponent?
                val eyeHeight = modelComponent?.model?.getEyeHeight(playerRef, it) ?: 0.0f

                val worldParticle = WorldParticle("Ball2", Color(255.toByte(), 0, 0), 1.0F, Vector3f(0F, 0F, 0F),
                    Direction(0F, 0F, 0F))

                spawnLaserBeam(
                    Vector3d(playerTransform.position.x, playerTransform.position.y + eyeHeight, playerTransform.position.z),
                    Vector3d(event.targetBlock.x + 0.5, event.targetBlock.y + 0.5, event.targetBlock.z + 0.5),
                    worldParticle,
                    listOf(playerRef),
                    it)
            }
        }

        override fun getQuery(): Query<EntityStore?> {
            return PlayerRef.getComponentType()
        }

        private fun spawnLaserBeam(
            startPos: Vector3d,
            endPos: Vector3d,
            worldParticle: WorldParticle,
            playerRefs: List<Ref<EntityStore>>,
            componentAccessor: ComponentAccessor<EntityStore>,
            spacing: Double = 0.5
        ) {
            val direction = Vector3d(endPos).subtract(startPos)
            val distance = direction.length()

            if (distance == 0.0) return

            direction.normalize()

            var currentDistance = 0.0
            while (currentDistance <= distance) {
                val position = Vector3d(startPos).add(
                    direction.x * currentDistance,
                    direction.y * currentDistance,
                    direction.z * currentDistance
                )

                ParticleUtil.spawnParticleEffect(
                    worldParticle,
                    position,
                    playerRefs,
                    componentAccessor
                )

                currentDistance += spacing
            }
        }
    }


    private fun onPlayerMouseMotionEvent(e: PlayerMouseMotionEvent) {
        val world = e.player.world ?: return
        val chunkStore = world.chunkStore
        val playerRef = e.playerRef

        val targetBlockVector = e.targetBlock
        val blockType = world.getBlockType(targetBlockVector)

        blockType?.item?.let {
            e.player.sendMessage(Message.raw("Debug: ${Message.translation(it.translationKey)}"))
        }
    }
}