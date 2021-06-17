// DONT_TARGET_EXACT_BACKEND: JS
// SKIP_MINIFICATION
// SKIP_OLD_MODULE_SYSTEMS
// FILE: api.kt

@JsExport
class A() {
    fun ping() = "ping"
}

@JsExport
class B() {
    @JsName("pong")
    fun ping() = "pong"
}

// we need his class to make sure that there's more than one ping method in existence - due to peculiarities of current namer otherwise test can pass but JsExport won't be actually respected
class C() {
    fun ping() = "pong"
}


// FILE: main.kt
external interface JsResult {
    val res: String
}

@JsModule("./jsExportInClass.mjs")
external fun jsBox(): JsResult

fun box(): String {
    val res = jsBox().res
    if (res != "pingpong") {
        return "Fail: ${res}"
    }

    return "OK"
}