/*
 * © Copyright - Astride UG (haftungsbeschränkt) & Leon Krogul (Herr_Einhorn) - 2020.
 */

package me.leon.tntrun.game

import kotlinx.coroutines.*
import net.darkdevelopers.darkbedrock.darkness.spigot.events.PlayerDisconnectEvent
import net.darkdevelopers.darkbedrock.darkness.spigot.functions.events.*
import net.darkdevelopers.darkbedrock.darkness.spigot.functions.schedule
import net.darkdevelopers.darkbedrock.darkness.spigot.functions.sendSubTitle
import net.darkdevelopers.darkbedrock.darkness.spigot.functions.sendTimings
import net.darkdevelopers.darkbedrock.darkness.spigot.functions.sendTitle
import net.darkdevelopers.darkbedrock.darkness.spigot.manager.game.EventsTemplate
import org.apache.commons.lang.time.StopWatch
import org.bukkit.ChatColor.*
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.plugin.Plugin
import java.util.concurrent.TimeUnit
import net.darkdevelopers.darkbedrock.darkness.spigot.utils.Utils.players as allPlayers

class InGame(
    private val plugin: Plugin,
    private val minY: Int,
    private val win: (Player) -> Unit
) : EventsTemplate(), StartStop {

    private var scope = CoroutineScope(Dispatchers.Default)
    private val players: Collection<Player> get() = allPlayers
    private var stopWatch = StopWatch()
    private var isRunning = false

    override fun start() {
        stopWatch.start()
        isRunning = true
        block(true)
        @Suppress("EXPERIMENTAL_API_USAGE")
        scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            val blocks = mutableSetOf<Block>()
            val tnts = mutableSetOf<Entity>()
            while (isRunning) {
                val time = 250L
                delay(time)
                scope.launch {
                    delay(time * 175 / 100)
                    plugin.schedule {
                        tnts.forEach { it.remove() }
                        tnts.clear()
                        blocks.forEach {
                            @Suppress("DEPRECATION")
                            tnts += it.world.spawnFallingBlock(it.location.add(0.5, -0.5, 0.5), it.type, it.data)
                            it.setType(Material.AIR, false)
                        }
                        blocks.clear()
                    }
                }
                players.forEach {
                    if (it.gameMode != GameMode.SURVIVAL) return@forEach
                    val block = it.location.block.getRelative(BlockFace.DOWN).run {
                        if (type == Material.AIR) getRelative(BlockFace.DOWN) else this
                    }
                    blocks += block
                }
            }
            isRunning = false
        }
        autoRespawn = true
        listen<PlayerMoveEvent>(plugin) {
            val player: Player = it.player
            if (player.location.y >= minY) return@listen
            player.damage(player.maxHealth)
        }.add()
        listen<PlayerDeathEvent>(plugin) {
            it.entity.dead()
            checkWin()
        }.add()
        listen<PlayerDisconnectEvent>(plugin) { checkWin() }
        listen<PlayerRespawnEvent>(plugin) {
            it.player.teleport(players.random())
        }.add()
        //Disable Fall Damage
        listen<EntityDamageEvent>(plugin) {
            if (it.cause != EntityDamageEvent.DamageCause.FALL) return@listen
            it.cancel()
        }
    }

    private fun checkWin() {
        val winner = players.singleOrNull { it.gameMode != GameMode.SPECTATOR && it.isOnline } ?: return
        winner.sendTitle("${GREEN}You won${WHITE}!")
        players.forEach {
            it.sendPlayTime()
            if (it == winner) return@forEach
            it.sendTitle("${GREEN}${winner.displayName} won")
        }
        stop()
        win(winner)
    }

    override fun stop() {
        isRunning = false
        reset()
        stopWatch.reset()
        block(false)
        scope.cancel()
    }

    private fun Player.dead() {
        gameMode = GameMode.SPECTATOR
        sendTitle("${DARK_RED}Dead")
        sendPlayTime()
    }

    private fun Player.sendPlayTime() {
        sendSubTitle("${GRAY}after $DARK_GRAY${TimeUnit.MILLISECONDS.toSeconds(stopWatch.time)}${GRAY}s")
        sendTimings(20, 140, 40)
    }

    private fun block(value: Boolean) {
        cancelBlockBreak = value
        cancelBlockPlace = value
    }

}
