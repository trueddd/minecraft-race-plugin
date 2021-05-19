package com.github.trueddd

import org.bukkit.configuration.file.FileConfiguration

class PluginConfig {

    var worldName: String = "world"
        private set

    private val _races = mutableListOf<String>()
    val races: List<String> = _races

    private val _attributes = mutableMapOf<String, Map<String, NbtData>>()
    val attributes: Map<String, Map<String, NbtData>> = _attributes

    private fun getNbtData(type: String, value: String): NbtData? {
        return when (type) {
            "byte" -> value.toByteOrNull()?.let { NbtData.Byte(it) }
            "short" -> value.toShortOrNull()?.let { NbtData.Short(it) }
            "int" -> value.toIntOrNull()?.let { NbtData.Int(it) }
            "long" -> value.toLongOrNull()?.let { NbtData.Long(it) }
            "float" -> value.toFloatOrNull()?.let { NbtData.Float(it) }
            "double" -> value.toDoubleOrNull()?.let { NbtData.Double(it) }
            "string" -> NbtData.String(value)
            else -> null
        }
    }

    fun buildFromConfig(config: FileConfiguration) {
        worldName = config.getString("world") ?: "world"
        _races.clear()
        _races.addAll(
            config.getConfigurationSection("races")
                .getKeys(false)
        )
        _attributes.clear()
        races.forEach { raceName ->
            val raceAttributes = config.getConfigurationSection("races.$raceName")
                .getKeys(false)
                .associateWith { attributeName ->
                    val type = config.getString("races.${raceName}.${attributeName}.type")
                    val value = config.getString("races.${raceName}.${attributeName}.value")
                    getNbtData(type, value) ?: throw IllegalStateException("Unrecognized data type \"${type}\" for attribute $attributeName of race $raceName")
                }
            _attributes[raceName] = raceAttributes
        }
    }
}