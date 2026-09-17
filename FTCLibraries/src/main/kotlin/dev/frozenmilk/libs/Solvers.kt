package dev.frozenmilk.libs

import dev.frozenmilk.FTC
import dev.frozenmilk.easyautolibraries.EasyAutoDependency
import dev.frozenmilk.easyautolibraries.EasyAutoScope

@Suppress("unused")
class Solvers(ftc: FTC) : EasyAutoScope<Solvers>(ftc) {
    private fun dependency(version: String) = dependency { name ->
        EasyAutoDependency(
            group = "org.solverslib",
            artifact = name,
            version = version
        )
    }

    val core by dependency("0.3.6")
    val pedroPathing by dependency("0.3.6")
    val photon by dependency("0.3.6")
}
