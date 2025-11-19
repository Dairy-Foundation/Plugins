package dev.frozenmilk

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

@Suppress("unused")
class BuildMetaData : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val extension = extensions.create("meta", BuildMetaDataExtension::class.java)
        val outDir = layout.buildDirectory.dir("generated/sources/generatedBuildMetaData/kotlin")

        val generateBuildMetadata =
            tasks.register("generateBuildMetaData", GenerateBuildMetaData::class.java) { task ->
                task.outDir.set(outDir)
                task.packagePath.set(extension.packagePathProperty)
                task.classNamePrefix.set(extension.nameProperty)
                task.fields.set(extension.fieldsProperty.map {
                    it.mapValues { (_, tv) ->
                        val (type, value) = tv
                        type to value()
                    }
                })
            }

        afterEvaluate {
            val kotlinExtension = extensions.getByType(KotlinProjectExtension::class.java)
            kotlinExtension.sourceSets.getByName("main") { sourceSet ->
                sourceSet.kotlin.srcDir(generateBuildMetadata.get().outDir)
            }
        }
    }
}