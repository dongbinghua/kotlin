// WITH_STDLIB
enum class A { X1, X2 }

fun box(): String {
    when {}
    when (A.X1) { else -> {} }
    return "OK"
}
