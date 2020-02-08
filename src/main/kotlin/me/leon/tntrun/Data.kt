/*
 * © Copyright - Astride UG (haftungsbeschränkt) & Leon Krogul (Herr_Einhorn) - 2020.
 */

package me.leon.tntrun

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.beust.klaxon.Parser.Companion.default
import me.leon.tntrun.config.LocationConverter
import me.leon.tntrun.config.UnderscoreCamelFieldRenamer
import net.darkdevelopers.darkbedrock.darkness.general.configs.ConfigData
import net.darkdevelopers.darkbedrock.darkness.general.configs.ConfigData.Companion.createFoldersIfNotExists
import net.darkdevelopers.darkbedrock.darkness.general.functions.toConfigData
import java.io.File
import java.nio.file.Files

val klaxon = Klaxon()
    .fieldRenamer(UnderscoreCamelFieldRenamer)
    .converter(LocationConverter)
val configs: MutableSet<Any> = mutableSetOf()

fun Any.registerAsConfig(folder: File) {
    configs += this
    writeConfig(folder)
}

inline fun <reified O : Any> Any.readConfig(folder: File): O? {
    val configData = configData(folder)
    val content = configData.file.readText()
    return klaxon.parse<O>(content)
}

fun Any.writeConfig(folder: File) {
    val configData = configData(folder)

    val output = Klaxon()
        .fieldRenamer(UnderscoreCamelFieldRenamer)
        .converter(LocationConverter)
        .toJsonString(this)

    val path = configData.file.toPath()
    Files.delete(path)
    Files.write(path, output.toPretty().toByteArray())
}

fun Any.configData(folder: File): ConfigData {
    val name = javaClass.name.split("$")
    val parent = name.first().split(".").last()

    val directory = folder.resolve(parent)
    createFoldersIfNotExists(directory)
    return directory.toConfigData(name.last().toLowerCase())
}

fun String.toPretty() = (default().parse(StringBuilder(this)) as JsonObject).toJsonString(true)
