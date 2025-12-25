import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.build.config)
    alias(libs.plugins.kotlinSerialization)
}

buildConfig {
    val openAiApiKey = project.findProperty("OPENAI_API_KEY") as String
    buildConfigField("OPENAI_API_KEY", openAiApiKey)

    val supabaseUrl = project.findProperty("SUPABASE_URL") as String
    buildConfigField("SUPABASE_URL", supabaseUrl)

    val supabaseApiKey = project.findProperty("SUPABASE_API_KEY") as String
    buildConfigField("SUPABASE_API_KEY", supabaseApiKey)
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

            implementation(libs.coil.compose)
            implementation(libs.coil.compose.core)
            implementation(libs.coil.ktor)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
