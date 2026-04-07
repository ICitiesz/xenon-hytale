package com.islandstudio.xenon.shared.io

import com.islandstudio.xenon.shared.di.IComponentProvider
import com.islandstudio.xenon.shared.di.getComponent
import com.islandstudio.xenon.shared.init.context.IPluginContext
import java.io.File

sealed class DataDirectory(folder: File): File(folder.toPath().toString()) {
    companion object: IComponentProvider {
        private val pluginContext = getComponent<IPluginContext>()

        fun createOrGetFile(folder: File, fileName: String): File {
            if (!folder.exists()) folder.mkdirs()

            val file = File(folder, fileName)

            if (!file.exists()) file.createNewFile()

            return file
        }

        fun getRootDataDirectory(): File {
            val dataDirectoryPath = pluginContext.mainPluginInstance.dataDirectory.toString();

            return File(dataDirectoryPath).also {
                if (it.exists()) return@also

                it.mkdirs()
            }
        }
    }
}