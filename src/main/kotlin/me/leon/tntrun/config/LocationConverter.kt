/*
 * © Copyright - Astride UG (haftungsbeschränkt) & Leon Krogul (Herr_Einhorn) - 2020.
 */

package me.leon.tntrun.config

import com.beust.klaxon.Converter
import com.beust.klaxon.JsonValue
import net.darkdevelopers.darkbedrock.darkness.spigot.functions.toBukkitWorld
import org.bukkit.Location

object LocationConverter : Converter {

    override fun canConvert(cls: Class<*>): Boolean = cls == Location::class.java

    override fun fromJson(jv: JsonValue): Location? {
        val obj = jv.obj ?: return null
        val world = obj["world"].toString().toBukkitWorld() ?: return null
        val x = obj["x"].toString().toDoubleOrNull() ?: return null
        val y = obj["y"].toString().toDoubleOrNull() ?: return null
        val z = obj["z"].toString().toDoubleOrNull() ?: return null
        val yaw = obj["yaw"].toString().toFloatOrNull() ?: return null
        val pitch = obj["pitch"].toString().toFloatOrNull() ?: return null
        return Location(world, x, y, z, yaw, pitch)
    }

    override fun toJson(value: Any): String {
        value as Location
        return """{"world": "${value.world?.name}", "x": ${value.x}, "y": ${value.y}, "z": ${value.z}, "yaw": ${value.yaw}, "pitch": ${value.pitch}}"""
    }

}