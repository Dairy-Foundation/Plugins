package dev.frozenmilk.easyautolibraries.util

import dev.frozenmilk.easyautolibraries.AbstractEasyAutoLibrary
import dev.frozenmilk.easyautolibraries.EasyAutoLibraryDependency
import dev.frozenmilk.easyautolibraries.EasyAutoLibraryMarker
import dev.frozenmilk.easyautolibraries.EasyAutoLibraryRepository
import groovy.lang.MissingMethodException
import groovy.lang.MissingPropertyException
import org.gradle.api.Action
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.plugins.Convention
import org.gradle.internal.extensibility.DefaultConvention
import org.gradle.internal.instantiation.InstantiatorFactory
import org.gradle.internal.metaobject.DynamicInvokeResult
import org.gradle.internal.metaobject.DynamicObject
import java.net.URI

class EasyAutoLibraryDynamicObject(private val container: AbstractEasyAutoLibrary, instantiatorFactory: InstantiatorFactory) : Convention by DefaultConvention(instantiatorFactory.decorateLenient()), DynamicObject {
	private val namedContents = mutableMapOf<String, EasyAutoLibraryMarker>()
	operator fun get(name: String): EasyAutoLibraryMarker? = namedContents[name]
	val values
		get() = namedContents.values as Collection<EasyAutoLibraryMarker>

	private fun <MARKER: EasyAutoLibraryMarker> register(marker: MARKER) = run {
		require(!namedContents.contains(marker.name)) { "${container.nameTree} already contains an object named ${marker.name}" }
		marker.also {
			(this as Convention).add(it.name, it)
			namedContents[it.name] = it
		}
	}

	//
	// SubLibraries
	//

	fun <LIB: AbstractEasyAutoLibrary> registerSubLibrary(cls: Class<LIB>) = register(container.project.objects.newInstance(cls, container))

	//
	// Repositories
	//

	fun registerRepository(easyAutoLibraryRepository: EasyAutoLibraryRepository): EasyAutoLibraryRepository = register(easyAutoLibraryRepository).also {
		container.parent?.asDynamicObject?.registerRepository(it)
	}
	fun registerRepository(name: String, configureRepositoryHandler: RepositoryHandler.() -> Unit) = registerRepository(EasyAutoLibraryRepository(container, name, configureRepositoryHandler))
	fun registerRepository(name: String, uri: String) = registerRepository(EasyAutoLibraryRepository(container, name, uri))
	fun registerRepository(name: String, uri: URI) = registerRepository(EasyAutoLibraryRepository(container, name, uri))
	private fun getRepositorySilent(name: String): EasyAutoLibraryRepository = try {
		requireNotNull(this[name] as? EasyAutoLibraryRepository) { "unable to find repository with name $name for ${container.name}" }
	}
	catch (_: IllegalArgumentException) {
		requireNotNull(container.parent?.asDynamicObject?.getRepositorySilent(name)) { "unable to find repository with name $name for ${container.nameTree}" }
	}

	//
	// Dependencies
	//

	fun registerDependency(name: String, notation: (version: String) -> String, repository: EasyAutoLibraryRepository) = register(EasyAutoLibraryDependency(container, name, notation, repository))
	fun registerDependency(name: String, notation: (version: String) -> String, repositoryName: String) = register(EasyAutoLibraryDependency(container, name, notation, getRepositorySilent(repositoryName)))

	//
	// Methods
	//

	override fun hasMethod(name: String, vararg arguments: Any?) =
		namedContents[name]?.run {
			println("testing hasMethod $name, found $this")
			if (
				arguments.size == 1
				&& arguments[0] != null
				&& Action::class.java.isAssignableFrom(arguments[0]!!.javaClass)
			) {
				access()
				return@run true
			}

			when (this) {
				is SubMethodAccess -> {
					access()
					hasMethod(arguments)
				}
				else -> {
					access()
					false
				}
			}
		} == true // otherwise false

	override fun tryInvokeMethod(
		name: String,
		vararg arguments: Any?
	) =
		namedContents[name]?.run {
			println("testing tryInvokeMethod $name, found $this")
			if (
				arguments.size == 1
				&& arguments[0] != null
				&& Action::class.java.isAssignableFrom(arguments[0]!!.javaClass)
			) {
				@Suppress("UNCHECKED_CAST")
				(arguments[0] as Action<EasyAutoLibraryMarker>).execute(this)
				access()
				return@run DynamicInvokeResult.found()
			}

			when (this) {
				is SubMethodAccess -> {
					access()
					tryInvokeMethod(arguments)
				}
				else -> DynamicInvokeResult.notFound()
			}
		} ?: DynamicInvokeResult.notFound()

	override fun getExtensionsAsDynamicObject() = this
	//
	// Properties
	//

	override fun hasProperty(name: String) =
		namedContents[name]?.run {
			when (this) {
				is SubPropertyAccess -> {
					access()
					true
				}
				else -> {
					access()
					false
				}
			}
		} == true // false otherwise

	override fun tryGetProperty(name: String) =
		namedContents[name]?.run {
			println("testing property $name, found $this")
			when (this) {
				is SubPropertyAccess -> {
					access()
					tryGetProperty()
				}
				else -> {
					access()
					DynamicInvokeResult.notFound()
				}
			}
		} ?: DynamicInvokeResult.notFound()

	override fun trySetProperty(
		name: String,
		value: Any?
	) = DynamicInvokeResult.notFound()

	override fun getProperties() = namedContents
		.filterValues { it is SubPropertyAccess }
		.mapValues { (_, v) -> (v as SubPropertyAccess).tryGetProperty().run {
			if (this.isFound) this.value
			else null
		}
	}

	override fun getMissingProperty(name: String) = MissingPropertyException("unable to find property $name on ${this.container.nameTree}", container.javaClass)

	override fun setMissingProperty(name: String) = MissingPropertyException("unable to find property $name on ${this.container.nameTree}", container.javaClass)

	override fun methodMissingException(
		name: String,
		vararg params: Any?
	) = MissingMethodException("unable to find property $name on ${this.container.nameTree}", container.javaClass, params)

	override fun getProperty(name: String): Any? {
		TODO("Do not use")
	}

	override fun setProperty(name: String, value: Any?) {
		TODO("Do not use")
	}

	override fun invokeMethod(name: String, vararg arguments: Any?): Any? {
		TODO("Do not use")
	}
}

