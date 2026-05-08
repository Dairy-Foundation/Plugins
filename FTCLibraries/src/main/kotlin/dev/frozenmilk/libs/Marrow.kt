package dev.frozenmilk.libs

import dev.frozenmilk.FTC
import dev.frozenmilk.easyautolibraries.EasyAutoDependency
import dev.frozenmilk.easyautolibraries.EasyAutoScope

@Suppress("unused")
class Marrow(ftc: FTC) : EasyAutoScope<Marrow>(ftc){
    var version = "1.1.0"

    private val Marrow = dependency { name ->
        EasyAutoDependency(
            group = "com.skeletonarmyftc.marrow",
            artifact = name,
            defaultVersion = { version }
        )
    }

    val core by Marrow
}