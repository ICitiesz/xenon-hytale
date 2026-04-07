package com.islandstudio.xenon.shared.utils

object TextHelper {
    fun capitalizeFirstChar(text: String): String {
        return text.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
    }
}