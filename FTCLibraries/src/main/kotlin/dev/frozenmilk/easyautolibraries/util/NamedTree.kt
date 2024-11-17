package dev.frozenmilk.easyautolibraries.util

interface NamedTree<PARENT: NamedTree<PARENT>> {
	val name: String
	val parent: PARENT?
	val nameTree: String
		get() = parent?.let { "${it.nameTree}.$name" } ?: name
}