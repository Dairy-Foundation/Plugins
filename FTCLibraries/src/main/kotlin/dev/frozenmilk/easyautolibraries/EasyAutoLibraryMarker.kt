package dev.frozenmilk.easyautolibraries

import dev.frozenmilk.easyautolibraries.util.NamedTree
import org.gradle.api.Action

/**
 * sealed marker class for FTCLibrary DSL Trees
 */
sealed class EasyAutoLibraryMarker<SELF: EasyAutoLibraryMarker<SELF>>() : NamedTree<AbstractEasyAutoLibrary<*>> {
	@Suppress("UNCHECKED_CAST")
	operator fun invoke(configure: Action<SELF>) {
		configure.execute(this as SELF)
		this.access()
	}

	var accessed = false
		private set
	/**
	 * callback that is run after the first access and configuration of this instance
	 */
	fun access() {
		if (!accessed) onAccess()
		accessed = true
	}
	protected abstract fun onAccess()
}