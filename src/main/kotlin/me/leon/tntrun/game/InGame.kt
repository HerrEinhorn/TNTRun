/*
 * © Copyright - Astride UG (haftungsbeschränkt) & Leon Krogul (Herr_Einhorn) - 2020.
 */

package me.leon.tntrun.game

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.darkdevelopers.darkbedrock.darkness.spigot.functions.events.autoRespawn
import net.darkdevelopers.darkbedrock.darkness.spigot.functions.events.listen
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
import org.bukkit.entity.Player
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

    private val players: Collection<Player> get() = allPlayers
    private var stopWatch = StopWatch().apply { start() }
    private var isRunning = false

    override fun start() {
        isRunning = true
        GlobalScope.launch {
            val blocks = mutableSetOf<Block>()
            while (isRunning) {
                val time = TimeUnit.SECONDS.toMillis(1)
                GlobalScope.launch {
                    delay(time)
                    plugin.schedule {
                        blocks.forEach { it.setType(Material.AIR, false) }
                    }
                }
                players.forEach {
                    val block = it.location.block.getRelative(BlockFace.DOWN)
                    blocks += block
                }
                delay(time)
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
        listen<PlayerRespawnEvent>(plugin) {
            it.player.teleport(players.random())
        }.add()
    }

    private fun checkWin() {
        val winner = players.singleOrNull { it.gameMode != GameMode.SPECTATOR && it.isOnline } ?: return
        stop()
        win(winner)
        winner.sendTitle("${AQUA}You$BLUE won!")
        players.forEach {
            it.sendPlayTime()
            if (it == winner) return@forEach
            it.sendTitle("${AQUA}${winner.displayName}$BLUE won")
        }
    }

    override fun stop() {
        isRunning = false
        reset()
    }

    private fun Player.dead() {
        gameMode = GameMode.SPECTATOR
        sendTitle("${DARK_RED}Dead")
        sendPlayTime()
    }

    fun Player.sendPlayTime() {
        sendSubTitle("${GRAY}after $DARK_GRAY${TimeUnit.MILLISECONDS.toSeconds(stopWatch.time)}${GRAY}s")
        sendTimings(20, 140, 40)
    }

}