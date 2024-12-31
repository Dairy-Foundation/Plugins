package dev.frozenmilk.publishing

class UncleanWorkingTree : Throwable("Cannot publish an unclean working tree.\nCheck `git status` for more information.")