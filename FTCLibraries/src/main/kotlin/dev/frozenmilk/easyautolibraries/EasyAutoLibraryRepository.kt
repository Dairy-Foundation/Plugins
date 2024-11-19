package dev.frozenmilk.easyautolibraries

import org.gradle.api.artifacts.dsl.RepositoryHandler
import java.net.URI

class EasyAutoLibraryRepository (
	override val parent: AbstractEasyAutoLibrary<*>,
	override val name: String,
	val configureRepositoryHandler: RepositoryHandler.() -> Unit
) : EasyAutoLibraryMarker<EasyAutoLibraryRepository>() {
	constructor(parent: AbstractEasyAutoLibrary<*>, name: String, uri: String):
			this(parent, name, {
				maven { url = parent.project.uri(uri) }
			})
	constructor(parent: AbstractEasyAutoLibrary<*>, name: String, uri: URI):
			this(parent, name, {
				maven { url = uri }
			})

	override fun onAccess() {
		parent.project.repositories.configureRepositoryHandler()
	}
}