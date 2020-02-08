/*
 * © Copyright - Astride UG (haftungsbeschränkt) & Leon Krogul (Herr_Einhorn) - 2020.
 */

package me.leon.tntrun.game

import net.darkdevelopers.darkbedrock.darkness.spigot.countdowns.LobbyCountdown
import net.darkdevelopers.darkbedrock.darkness.spigot.functions.events.cancelEntityExplode
import net.darkdevelopers.darkbedrock.darkness.spigot.functions.events.listen
import net.darkdevelopers.darkbedrock.darkness.spigot.functions.sendLobbyScoreBoard
import net.darkdevelopers.darkbedrock.darkness.spigot.manager.game.EventsTemplate
import net.darkdevelopers.darkbedrock.darkness.spigot.manager.game.LobbyEventsTemplate
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

    private var countdown: LobbyCountdown = LobbyCountdown(gameName = gameName, minPlayers = minPlayers)

    override fun start() {
        LobbyEventsTemplate.setup(plugin, spawn)
        cancelEntityExplode = true
        listen<PlayerJoinEvent>(plugin) {
            val player = it.player
            player.gameMode = GameMode.SURVIVAL
            player.sendLobbyScoreBoard("$AQUA${BOLD}$gameWorldName", countdown.gameName)
        }.add()
        countdown.idle()
    }

    override fun stop() {
        LobbyEventsTemplate.reset()
        cancelEntityExplode = false
        reset()
        countdown.stop()
    }


}