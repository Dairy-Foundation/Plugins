package dev.frozenmilk.easyautolibraries.util

import dev.frozenmilk.easyautolibraries.AbstractEasyAutoLibrary
import dev.frozenmilk.easyautolibraries.EasyAutoLibraryDependency
import dev.frozenmilk.easyautolibraries.EasyAutoLibraryMarker
import dev.frozenmilk.easyautolibraries.EasyAutoLibraryRepository
import groovy.lang.MissingMethodException
import groovy.lang.MissingPropertyException
import org.gradle.api.Action
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.internal.metaobject.DynamicInvokeResult
import org.gradle.internal.metaobject.DynamicObject
import java.net.URI

class EasyAutoLibraryDynamicObject(private val container: AbstractEasyAutoLibrary<*>) : DynamicObject {
	private val namedContents = mutableMapOf<String, EasyAutoLibraryMarker<*>>()
	private operator fun get(name: String): EasyAutoLibraryMarker<*>? = namedContents[name]
	val values
		get() = namedContents.values as Collection<EasyAutoLibraryMarker<*>>

	private fun <MARKER: EasyAutoLibraryMarker<*>> register(marker: MARKER) = run {
		require(!namedContents.contains(marker.name)) { "${container.nameTree} already contains an object named ${marker.name}" }
		marker.also {
			namedContents[it.name] = it
		}
	}

	//
	// SubLibraries
	//

	fun <LIB: AbstractEasyAutoLibrary<LIB>> registerSubLibrary(lib: LIB) = register(lib)
	fun getSubLibrary(name: String) = this[name] as? AbstractEasyAutoLibrary<*>

	//
	// Repositories
	//

	private fun registerRepository(easyAutoLibraryRepository: EasyAutoLibraryRepository): EasyAutoLibraryRepository = container.parent?.asDynamicObject?.registerRepository(easyAutoLibraryRepository) ?: register(easyAutoLibraryRepository)
	fun getOrRegisterRepository(name: String, configureRepositoryHandler: RepositoryHandler.() -> Unit) = getRepository(name) ?: registerRepository(EasyAutoLibraryRepository(container, name, configureRepositoryHandler))
	fun getOrRegisterRepository(name: String, uri: String) = getRepository(name) ?: registerRepository(EasyAutoLibraryRepository(container, name, uri))
	fun getOrRegisterRepository(name: String, uri: URI) = getRepository(name) ?: registerRepository(EasyAutoLibraryRepository(container, name, uri))
	fun getRepository(name: String): EasyAutoLibraryRepository? = this[name] as? EasyAutoLibraryRepository ?: container.parent?.asDynamicObject?.getRepository(name)
	fun tryGetRepository(name: String): EasyAutoLibraryRepository = requireNotNull(getRepository(name)) { "unable to find repository with name $name for ${container.nameTree}" }

	//
	// Dependencies
	//

	fun registerDependency(name: String, notation: (version: String) -> String, repository: () -> EasyAutoLibraryRepository) = register(EasyAutoLibraryDependency(container, name, notation, repository))
	fun registerDependency(name: String, notation: (version: String) -> String, repositoryName: String) = register(EasyAutoLibraryDependency(container, name, notation) { tryGetRepository(repositoryName) })
	fun getDependency(name: String) = this[name] as? EasyAutoLibraryDependency

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
				(arguments[0] as Action<EasyAutoLibraryMarker<*>>).execute(this)
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

