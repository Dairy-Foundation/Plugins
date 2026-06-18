package dev.frozenmilk.libs

import dev.frozenmilk.FTC
import dev.frozenmilk.easyautolibraries.EasyAutoDependency
import dev.frozenmilk.easyautolibraries.EasyAutoScope

@Suppress("unused")
class Pedro(ftc: FTC) : EasyAutoScope<Pedro>(ftc) {
    private fun library(version: String) = dependency { name ->
        EasyAutoDependency(
            group = "com.pedropathing",
            artifact = name,
            version = version,
        )
    }

    val core by library("2.1.2")
    val ftc by library("2.1.2")
    val telemetry by library("1.0.0")
}