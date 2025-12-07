import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import java.util.Properties
import kotlin.apply

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.build.config)
    alias(libs.plugins.kotlinSerialization)
}

buildConfig {
    val authToken = loadFromLocalProperties("OPENAI_API_KEY")
    buildConfigField("OPENAI_API_KEY", authToken)

    val supabaseUrl = loadFromLocalProperties("SUPABASE_URL")
    buildConfigField("SUPABASE_URL", supabaseUrl)

    val supabaseKey = loadFromLocalProperties("SUPABASE_API_KEY")
    buildConfigField("SUPABASE_API_KEY", supabaseKey)
}

kotlin {
    js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(project.dependencies.platform(libs.supabase.bom))
            implementation(libs.supabase.realtime)
            implementation(libs.supabase.postgrest)

            implementation(libs.koog)

            implementation(libs.kotlinx.serialization.json)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

private fun loadFromLocalProperties(key: String): String {
    return rootProject.file("local.properties")
        .inputStream()
        .use { input ->
            Properties().apply {
                load(input)
            }.getProperty(key)
        }
}


