package dev.frozenmilk

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.gradle.internal.ensureParentDirsCreated
import kotlin.collections.component1
import kotlin.collections.component2

abstract class GenerateBuildMetaData : DefaultTask() {
    @get:OutputDirectory
    abstract val outDir: DirectoryProperty

    @get:Input
    abstract val packagePath: Property<String>

    @get:Input
    abstract val classNamePrefix: Property<String>

    @get:Input
    abstract val fields: MapProperty<String, Pair<String, String>>

    init {
        group = "build"
        outputs.dir(outDir)
    }

    @TaskAction
    fun generate() {
        project.run {
            val f = file(
                "${outDir.get().asFile}/${
                    packagePath.get().replace(
                        '.', '/'
                    )
                }/${classNamePrefix.get()}BuildMetaData.kt"
            )

            outDir.get().asFile.deleteRecursively()
            f.ensureParentDirsCreated()
            f.createNewFile()
            val out = f.bufferedWriter()
            write(out)
            out.flush()
        }
    }

    private fun write(out: Appendable) {
        out.appendLine("package ${packagePath.get()}")
        out.appendLine("object ${classNamePrefix.get()}BuildMetaData {")
        fields.get().entries.forEach { (name, tv) ->
            val (type, value) = tv
            out.appendLine("\t@JvmStatic val `$name`: $type = ${value};")
        }
        out.appendLine("}")
    }
}