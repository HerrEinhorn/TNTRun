/*
 * © Copyright - Astride UG (haftungsbeschränkt) & Leon Krogul (Herr_Einhorn) - 2020.
 */

package me.leon.tntrun

import me.leon.tntrun.game.GameStateManager
import net.darkdevelopers.darkbedrock.darkness.spigot.plugin.DarkPlugin

class TNTRun : DarkPlugin() {

    private val gameStateManager by lazy { GameStateManager(this) }

    override fun onEnable() = onEnable {
        gameStateManager.init()
    }

    override fun onDisable() = onDisable {
        gameStateManager.shutdown()
    }

}

