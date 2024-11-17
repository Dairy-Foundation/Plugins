package dev.frozenmilk.easyautolibraries

import dev.frozenmilk.easyautolibraries.util.EasyAutoLibraryDynamicObject
import dev.frozenmilk.easyautolibraries.util.SubPropertyAccess
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.internal.DynamicObjectAware
import org.gradle.api.plugins.Convention
import org.gradle.internal.instantiation.InstantiatorFactory
import org.gradle.internal.metaobject.DynamicInvokeResult
import org.gradle.internal.metaobject.MethodMixIn
import org.gradle.internal.metaobject.PropertyMixIn
import java.net.URI
import kotlin.reflect.KProperty

abstract class AbstractEasyAutoLibrary (
	final override val name: String,
	final override val parent: AbstractEasyAutoLibrary?,
	instantiatorFactory: InstantiatorFactory
) : EasyAutoLibraryMarker(), DynamicObjectAware, MethodMixIn, PropertyMixIn, SubPropertyAccess {
	private val dynamicAccess = EasyAutoLibraryDynamicObject(this, instantiatorFactory)
	val subLibraries
		get() = dynamicAccess.values.filterIsInstance<AbstractEasyAutoLibrary>()
	val dependencies
		get() = dynamicAccess.values.filterIsInstance<EasyAutoLibraryDependency>()
	val repositories
		get() = dynamicAccess.values.filterIsInstance<EasyAutoLibraryRepository>()

	override fun getAsDynamicObject() = dynamicAccess
	override fun getAdditionalMethods() = dynamicAccess
	override fun getAdditionalProperties() = dynamicAccess
	private val dynamicThis = DynamicInvokeResult.found(this)
	override fun tryGetProperty() = dynamicThis

	private var _configurationName: String? = null

	/**
	 * the gradle dependencies configuration name, automatically resolves to find the most local one
	 */
	var configurationName: String
		get() =
			// trys the configuration name of the parent
			try {
				_configurationName ?: parent?.configurationName
			}
			// if it crashes, we know there is a parent
			catch (_: IllegalStateException) {
				error("unable to find configuration for $name, try setting the `configurationName` property for $name or a parent")
			}
			// otherwise, there isn't and we don't recommend interacting with it
				?: error("unable to find configuration for $name, set the `configurationName` property")
		set(value) {
			_configurationName = value
		}

	open val project: Project
		get() = parent?.project
			?: error("incorrectly set up FTCLibrary tree, this either needs a parent with a concrete project, or its own")

	/**
	 * registers [cls] as a SubLibrary but does not [access] it, you can then use [action] to configure and [access] it
	 */
	@JvmOverloads
	fun <LIB : AbstractEasyAutoLibrary> registerSubLibrary(
		cls: Class<LIB>,
		action: Action<LIB> = Action {}
	) = dynamicAccess.registerSubLibrary(cls).also { action.execute(it) }

	/**
	 * registers but does not [access] repository, you can use [action] to then configure and [access] it,
	 *
	 * or you can pass it to dependencies
	 */
	@JvmOverloads
	fun registerRepository(
		name: String,
		configureRepositoryHandler: RepositoryHandler.() -> Unit,
		action: Action<EasyAutoLibraryRepository> = Action {}
	) = dynamicAccess.registerRepository(name, configureRepositoryHandler).also { action.execute(it) }

	/**
	 * registers but does not [access] repository, you can use [action] to then configure and [access] it,
	 *
	 * or you can pass it to dependencies
	 */
	@JvmOverloads
	fun registerRepository(
		name: String,
		uri: String,
		action: Action<EasyAutoLibraryRepository> = Action {}
	) = dynamicAccess.registerRepository(name, uri).also { action.execute(it) }

	/**
	 * registers but does not [access] repository, you can use [action] to then configure and [access] it,
	 *
	 * or you can pass it to dependencies
	 */
	@JvmOverloads
	fun registerRepository(
		name: String,
		uri: URI,
		action: Action<EasyAutoLibraryRepository> = Action {}
	) = dynamicAccess.registerRepository(name, uri).also { action.execute(it) }

	/**
	 * registers but does not [apply] dependency, you can use [action] to then configure and [apply] it
	 */
	@JvmOverloads
	fun registerDependency(
		name: String,
		notation: (version: String) -> String,
		repository: EasyAutoLibraryRepository,
		action: Action<EasyAutoLibraryDependency> = Action {}
	) = dynamicAccess.registerDependency(name, notation, repository).also { action.execute(it) }

	/**
	 * registers but does not [apply] dependency, you can use [action] to then configure and [apply] it
	 */
	@JvmOverloads
	fun registerDependency(
		name: String,
		notation: (version: String) -> String,
		repositoryName: String,
		action: Action<EasyAutoLibraryDependency> = Action {}
	) = dynamicAccess.registerDependency(name, notation, repositoryName).also { action.execute(it) }

	@JvmOverloads
	operator fun get(name: String, action: Action<EasyAutoLibraryMarker> = Action {}) =
		dynamicAccess[name]?.also { action.execute(it); it.access() }

	fun getDependency(name: String, action: Action<EasyAutoLibraryDependency> = Action {}) =
		dynamicAccess[name].run { this as? EasyAutoLibraryDependency }
			?.also { action.execute(it); it.access() }

	fun getRepository(name: String, action: Action<EasyAutoLibraryRepository> = Action {}) =
		dynamicAccess[name].run { this as? EasyAutoLibraryRepository }
			?.also { action.execute(it); it.access() }

	fun getSubLibrary(name: String, action: Action<AbstractEasyAutoLibrary> = Action {}) =
		dynamicAccess[name].run { this as? AbstractEasyAutoLibrary }
			?.also { action.execute(it); it.access() }

	companion object {
		//fun <LIB: AbstractFTCLibraryContainer> LIB.applyConfiguration(action: Action<LIB>) = run {
		//	action.execute(this)
		//	if (!configured) init()
		//	configured = true
		//	this
		//}

		@Suppress("UNCHECKED_CAST")
		operator fun <MARKER : EasyAutoLibraryMarker> String.getValue(
			thisRef: AbstractEasyAutoLibrary,
			property: KProperty<*>
		) = thisRef[this] as MARKER
	}

	fun getConvention(): Convention = dynamicAccess
}