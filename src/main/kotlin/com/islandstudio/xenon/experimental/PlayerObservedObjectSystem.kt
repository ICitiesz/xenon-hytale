package com.islandstudio.xenon.experimental

import com.hypixel.hytale.component.ComponentAccessor
import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.protocol.Direction
import com.hypixel.hytale.protocol.Position
import com.hypixel.hytale.protocol.packets.player.ClientMovement
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent
import com.hypixel.hytale.server.core.universe.world.ParticleUtil
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import com.hypixel.hytale.server.core.util.TargetUtil
import com.islandstudio.xenon.shared.di.IComponentProvider
import com.islandstudio.xenon.shared.di.getComponent
import com.islandstudio.xenon.shared.experimental.protocol.PacketFilterRegistry
import com.islandstudio.xenon.shared.init.context.IPluginContext
import org.joml.Vector3d
import org.koin.core.annotation.Single
import kotlin.math.abs

@Single
class PlayerObservedObjectSystem: IComponentProvider {
    private val pluginContext = getComponent<IPluginContext>()
    private var lastAbsolutePosition: Position? = null
    private var lastLookOrientation: Direction? = null

    @Suppress("PrivatePropertyName")
    private val MOVEMENT_THRESHOLD = 0.1f

    fun run() {
        pluginContext.packetFilterRegistry.registerPlayerPacketFilter(
            this.javaClass,
            PacketFilterRegistry.PlayerPacketFilterHolder(
                { playerRefObj, packet ->
                    val clientMovementPacket = packet as? ClientMovement ?: return@PlayerPacketFilterHolder false

                    clientMovementPacket.absolutePosition?.let {
                        if (lastAbsolutePosition == null) {
                            lastAbsolutePosition = it
                            return@let
                        }

                        val xDelta = abs(it.x - lastAbsolutePosition!!.x)
                        val yDelta = abs(it.y - lastAbsolutePosition!!.y)
                        val zDelta = abs(it.z - lastAbsolutePosition!!.z)

                        if (xDelta > MOVEMENT_THRESHOLD || yDelta > MOVEMENT_THRESHOLD || zDelta > MOVEMENT_THRESHOLD) {
                            playerRefObj.sendMessage(Message.raw("Debug: Movement detected"))
                            lastAbsolutePosition = it

                            playerRefObj.reference?.let { ref ->
                                val store = ref.store

                                val world = store.externalData.world

                                world.execute {
                                    val targetBlockVector3i = TargetUtil.getTargetBlock(ref, 8.0, store) ?: return@execute
                                    val playerTransform = store.getComponent(ref, TransformComponent.getComponentType()) ?: return@execute
                                    val modelComponent = store.getComponent(ref, ModelComponent.getComponentType())
                                    val eyeHeight = modelComponent?.model?.getEyeHeight(ref, store) ?: 0.0f

                                    val playerViewPosition = Vector3d(
                                        playerTransform.position.x,
                                        playerTransform.position.y + eyeHeight,
                                        playerTransform.position.z
                                    )
                                    val targetBlockVector3d = Vector3d(targetBlockVector3i).add(0.5, 0.5, 0.5)

                                    spawnLaserBeam(playerViewPosition, targetBlockVector3d, "DebugOrb2", listOf(ref), store)
                                }
                            }

                            return@PlayerPacketFilterHolder false
                        }
                    }

                    clientMovementPacket.lookOrientation?.let {
                        if (lastLookOrientation == null) {
                            lastLookOrientation = it
                            return@let
                        }

                        val yawDelta = abs(it.yaw - lastLookOrientation!!.yaw)
                        val pitchDelta = abs(it.pitch - lastLookOrientation!!.pitch)
                        val rollDelta = abs(it.roll - lastLookOrientation!!.roll)

                        if (yawDelta > MOVEMENT_THRESHOLD || pitchDelta > MOVEMENT_THRESHOLD || rollDelta > MOVEMENT_THRESHOLD) {
                            playerRefObj.sendMessage(Message.raw("Debug: Look detected"))
                            lastLookOrientation = it

                            playerRefObj.reference?.let { ref ->
                                val store = ref.store

                                val world = store.externalData.world

                                world.execute {
                                    val targetBlockVector3i = TargetUtil.getTargetBlock(ref, 8.0, store) ?: return@execute
                                    val playerTransform = store.getComponent(ref, TransformComponent.getComponentType()) ?: return@execute
                                    val modelComponent = store.getComponent(ref, ModelComponent.getComponentType())
                                    val eyeHeight = modelComponent?.model?.getEyeHeight(ref, store) ?: 0.0f

                                    val playerViewPosition = Vector3d(playerTransform.position.x, playerTransform.position.y + eyeHeight, playerTransform.position.z)
                                    val targetBlockVector3d = Vector3d(targetBlockVector3i).add(0.5, 0.5, 0.5)

                                    spawnLaserBeam(playerViewPosition, targetBlockVector3d, "DebugOrb2", listOf(ref), store)
                                }
                            }

                            return@PlayerPacketFilterHolder false
                        }
                    }

                    return@PlayerPacketFilterHolder false
                },
                null
            )
        )
    }

    fun spawnLaserBeam(
        startPos: Vector3d,
        endPos: Vector3d,
        particleName: String,
        playerRefs: List<Ref<EntityStore>>,
        componentAccessor: ComponentAccessor<EntityStore>,
        spacing: Double = 0.5
    ) {
        val direction = Vector3d(endPos).sub(startPos)
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
                particleName,
                position,
                playerRefs,
                componentAccessor
            )

            currentDistance += spacing
        }
    }
}