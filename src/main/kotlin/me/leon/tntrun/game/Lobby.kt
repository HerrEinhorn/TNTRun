/*
 * © Copyright - Astride UG (haftungsbeschränkt) & Leon Krogul (Herr_Einhorn) - 2020.
 */

package me.leon.tntrun.game

import net.darkdevelopers.darkbedrock.darkness.spigot.countdowns.LobbyCountdown
import net.darkdevelopers.darkbedrock.darkness.spigot.events.PlayerDisconnectEvent
import net.darkdevelopers.darkbedrock.darkness.spigot.functions.events.cancelEntityExplode
import net.darkdevelopers.darkbedrock.darkness.spigot.functions.events.listen
import net.darkdevelopers.darkbedrock.darkness.spigot.functions.sendLobbyScoreBoard
import net.darkdevelopers.darkbedrock.darkness.spigot.manager.game.EventsTemplate
import net.darkdevelopers.darkbedrock.darkness.spigot.manager.game.LobbyEventsTemplate
import net.darkdevelopers.darkbedrock.darkness.spigot.utils.Items
import org.bukkit.ChatColor.AQUA
import org.bukkit.ChatColor.BOLD
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.Plugin

class Lobby(
    private val plugin: Plugin,
    private val spawn: Location,
    gameName: String,
    minPlayers: Int,
    private val gameWorldName: String
) : EventsTemplate(), StartStop {

    private var countdown: LobbyCountdown = LobbyCountdown(gameName = gameName, minPlayers = minPlayers, seconds = 25)

    override fun start() {
        LobbyEventsTemplate.setup(plugin, spawn)
        cancelEntityExplode = true
        listen<PlayerJoinEvent>(plugin) {
            val player = it.player
            player.gameMode = GameMode.SURVIVAL
            player.sendLobbyScoreBoard("$AQUA${BOLD}$gameWorldName", countdown.gameName)
            player.inventory.setItem(8, Items.LEAVE.itemStack)
        }.add()
        listen<PlayerDisconnectEvent>(plugin) {
            if (countdown.players.size - 1 > countdown.minPlayers) return@listen
            countdown.stop()
            countdown.idle()
        }
        countdown.idle()
    }

    override fun stop() {
        countdown.players.forEach { it.inventory.clear() }
        LobbyEventsTemplate.reset()
        cancelEntityExplode = false
        reset()
        countdown.stop()
    }


}