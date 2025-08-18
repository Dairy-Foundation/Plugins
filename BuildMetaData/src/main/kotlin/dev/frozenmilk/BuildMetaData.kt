package dev.frozenmilk

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.AbstractCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.internal.ensureParentDirsCreated

@Suppress("unused")
class BuildMetaData : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val extension = extensions.create("meta", BuildMetaDataExtension::class.java)
        val outDir = layout.buildDirectory.dir("generated/sources/generatedBuildMetaData/kotlin")

        afterEvaluate {
            val kotlinExtension = extensions.getByType(KotlinProjectExtension::class.java)
            val generateBuildMetadata = tasks.create("generateBuildMetaData") { task ->
                task.group = "build"

                val f = file(
                    "${outDir.get().asFile}/${
                        extension.packagePath.replace(
                            '.', '/'
                        )
                    }/${extension.name}BuildMetaData.kt"
                )

                task.doLast {
                    outDir.get().asFile.deleteRecursively()
                    f.ensureParentDirsCreated()
                    f.createNewFile()
                    val out = f.bufferedWriter()
                    extension.write(out)
                    out.flush()
                }
            }

            kotlinExtension.sourceSets.getByName("main") { sourceSet ->
                sourceSet.kotlin.srcDir(outDir)
            }

            tasks.withType(AbstractCompile::class.java).all {
                it.dependsOn(generateBuildMetadata)
            }
        }
    }
}