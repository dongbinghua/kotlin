/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.compiler.plugin

import com.intellij.openapi.project.Project
import org.jetbrains.annotations.TestOnly
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.extensions.ProjectExtensionDescriptor

abstract class K2PluginRegistrar {
    companion object {
        val K2_PLUGIN_REGISTRARS: CompilerConfigurationKey<MutableList<K2PluginRegistrar>> =
            CompilerConfigurationKey.create("K2 plugin registrars")
    }

    abstract fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration)

    class ExtensionStorage {
        private val _registeredExtensions = mutableMapOf<ProjectExtensionDescriptor<*>, MutableList<Any>>()
        val registeredExtensions: Map<ProjectExtensionDescriptor<*>, List<Any>>
            get() = _registeredExtensions

        fun <T : Any> ProjectExtensionDescriptor<T>.registerExtension(extension: T) {
            _registeredExtensions.getOrPut(this, ::mutableListOf).add(extension)
        }
    }

    abstract val supportsK2: Boolean
}

fun K2PluginRegistrar.ExtensionStorage.registerInProject(
    project: Project,
    errorMessage: (Any) -> String = { "Error while registering ${it.javaClass.name} "}
) {
    for ((extensionPoint, extensions) in registeredExtensions) {
        for (extension in extensions) {
            @Suppress("UNCHECKED_CAST")
            try {
                (extensionPoint as ProjectExtensionDescriptor<Any>).registerExtensionUnsafe(project, extension)
            } catch (e: AbstractMethodError) {
                throw IllegalStateException(errorMessage(extension), e)
            }
        }
    }
}

private fun ProjectExtensionDescriptor<Any>.registerExtensionUnsafe(project: Project, extension: Any) {
    this.registerExtension(project, extension)
}

@TestOnly
fun registerExtensionsForTest(
    project: Project,
    configuration: CompilerConfiguration,
    register: K2PluginRegistrar.ExtensionStorage.(CompilerConfiguration) -> Unit
) {
    val extensionStorage = K2PluginRegistrar.ExtensionStorage().apply {
        register(configuration)
    }
    extensionStorage.registerInProject(project)
}
