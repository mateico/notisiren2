package com.notisiren.core.extensions

/**
 * True if this string contains [other], ignoring case.
 * Null-safe on the receiver: a null string never matches.
 */
fun String?.containsIgnoreCase(other: String): Boolean =
    this?.contains(other, ignoreCase = true) == true