package dev.frozenmilk.easyautolibraries.util

class NullableType<T> private constructor(val cls: Class<T>?, val nullable: Boolean) {
	/**
	 * returns true if a value with [typeOf] [other] can be used where a value with [typeOf] [this] could be
	 */
	fun isAssignableFrom(other: Any?): Boolean {
		return when (other) {
			is NullableType<*> -> cls?.run { if (other.cls != null) isAssignableFrom(other.cls) else nullable } ?: other.nullable
			is Class<*> -> cls?.isAssignableFrom(other) == true
			else -> false
		}
	}
	companion object {
		fun <T: Any> Class<T>.typeOf(nullable: Boolean = false) = NullableType(this, nullable)
		fun <T: Any> fromClass(cls: Class<T>, nullable: Boolean) = NullableType(cls, nullable)
		fun <T: Any?> typeOf(t: T) = t?.run { NullableType(t.javaClass, false) } ?: NullableType(null, true)
		fun <T: Any> nullableTypeOf(t: T) = t.run { NullableType(t.javaClass, true) }

		fun Collection<NullableType<*>>.isAssignableFrom(vararg other: NullableType<*>): Boolean {
			if (size != other.size) return false
			forEachIndexed { i, item ->
				if (!item.isAssignableFrom(other[i])) return false
			}
			return true
		}
		fun Collection<NullableType<*>>.isAssignableFrom(other: Collection<NullableType<*>>): Boolean {
			if (size != other.size) return false
			val thisIter = iterator()
			val otherIter = iterator()
			while (thisIter.hasNext() && otherIter.hasNext()) {
				if (!thisIter.next().isAssignableFrom(otherIter.next())) return false
			}
			return true
		}
		fun Array<NullableType<*>>.isAssignableFrom(vararg other: NullableType<*>): Boolean {
			if (size != other.size) return false
			for (i in 0 until size) {
				if (!this[i].isAssignableFrom(other[i])) return false
			}
			return true
		}
		fun Array<NullableType<*>>.isAssignableFrom(other: Collection<NullableType<*>>): Boolean {
			if (size != other.size) return false
			other.forEachIndexed { i, item ->
				if (!this[i].isAssignableFrom(item)) return false
			}
			return true
		}
		fun Collection<NullableType<*>>.isAssignableFromClasses(vararg other: Class<*>): Boolean {
			if (size != other.size) return false
			forEachIndexed { i, item ->
				if (!item.isAssignableFrom(other[i])) return false
			}
			return true
		}
		fun Collection<NullableType<*>>.isAssignableFromClasses(other: Collection<Class<*>>): Boolean {
			if (size != other.size) return false
			val thisIter = iterator()
			val otherIter = iterator()
			while (thisIter.hasNext() && otherIter.hasNext()) {
				if (!thisIter.next().isAssignableFrom(otherIter.next())) return false
			}
			return true
		}
		fun Array<NullableType<*>>.isAssignableFromClasses(vararg other: Class<*>): Boolean {
			if (size != other.size) return false
			for (i in 0 until size) {
				if (!this[i].isAssignableFrom(other[i])) return false
			}
			return true
		}
		fun Array<NullableType<*>>.isAssignableFromClasses(other: Collection<Class<*>>): Boolean {
			if (size != other.size) return false
			other.forEachIndexed { i, item ->
				if (!this[i].isAssignableFrom(item)) return false
			}
			return true
		}
	}
}

