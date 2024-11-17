package dev.frozenmilk.easyautolibraries

import dev.frozenmilk.easyautolibraries.util.NamedTree

/**
 * sealed marker class for FTCLibrary DSL Trees
 */
sealed class EasyAutoLibraryMarker () : NamedTree<AbstractEasyAutoLibrary> {
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