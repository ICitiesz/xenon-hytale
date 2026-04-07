package com.islandstudio.xenon.shared.config

import com.akuleshov7.ktoml.tree.nodes.TomlKeyValuePrimitive
import com.akuleshov7.ktoml.tree.nodes.TomlNode
import com.akuleshov7.ktoml.tree.nodes.pairs.values.TomlBasicString
import com.akuleshov7.ktoml.tree.nodes.pairs.values.TomlBoolean
import com.akuleshov7.ktoml.tree.nodes.pairs.values.TomlDouble
import com.akuleshov7.ktoml.tree.nodes.pairs.values.TomlLiteralString
import com.akuleshov7.ktoml.tree.nodes.pairs.values.TomlLong
import com.akuleshov7.ktoml.tree.nodes.pairs.values.TomlValue
import kotlinx.serialization.descriptors.PrimitiveKind

data class ConfigNode(
    val parentTomlNode: TomlNode?,
    val key: String,
    val value: Any,
    val dataType: PrimitiveKind,
    val comments: List<String>,
    val inlineComment: String
) {
    companion object {
        fun mapFromTomlKeyValue(tomlKeyValue: TomlKeyValuePrimitive): ConfigNode {
            return ConfigNode(
                parentTomlNode = tomlKeyValue.parent,
                key = tomlKeyValue.name,
                value = tomlKeyValue.value.content,
                dataType = getDataType(tomlKeyValue.value),
                comments = tomlKeyValue.comments,
                inlineComment = tomlKeyValue.inlineComment
            )
        }

        private fun getDataType(tomlValue: TomlValue): PrimitiveKind {
            return when(tomlValue) {
                is TomlBasicString, is TomlLiteralString -> PrimitiveKind.STRING

                is TomlBoolean -> PrimitiveKind.BOOLEAN

                is TomlLong -> PrimitiveKind.LONG

                is TomlDouble -> PrimitiveKind.DOUBLE

                else -> PrimitiveKind.STRING
            }
        }
    }
}
