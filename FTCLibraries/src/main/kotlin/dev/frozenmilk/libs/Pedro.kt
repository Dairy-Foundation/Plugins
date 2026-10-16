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

    val revhub by library("3.0.2")
    val tuning by library("1.0.1")

    private fun extension(version: String) = dependency { name ->
        EasyAutoDependency(
            group = "com.pedropathing.ivy",
            artifact = name,
            version = version,
        )
    }

    val pedro by extension("1.1.1")
}