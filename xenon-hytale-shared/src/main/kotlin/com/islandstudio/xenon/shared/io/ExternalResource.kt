package com.islandstudio.xenon.shared.io

import java.io.File

enum class ExternalResource(val resourceFolder: File, val resourceName: String) {
    TestConfigFile(DataDirectory.getRootDataDirectory(), "test.toml")
}