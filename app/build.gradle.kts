plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}
android {
    namespace = "de.benzo.scorium"
    compileSdk = 35
    buildFeatures { buildConfig = true; compose = true }
    val adminBuild = (providers.gradleProperty("scoriumAdmin").orNull?.toBooleanStrictOrNull() ?: (System.getenv("GITHUB_HEAD_REF") == "admin" || System.getenv("GITHUB_REF_NAME") == "admin"))
    defaultConfig {
        applicationId = if (adminBuild) "de.benzo.scorium.admin" else "de.benzo.scorium"
        buildConfigField("boolean", "SCORIUM_ADMIN", adminBuild.toString())
        manifestPlaceholders["scoriumAppLabel"] = if (adminBuild) "SCORIUM DEV" else "SCORIUM"
        minSdk = 26
        targetSdk = 35
        versionCode = (System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull() ?: 75).coerceAtLeast(75)
        versionName = "0.3." + versionCode
    }
    signingConfigs {
        create("distribution") {
            val store = System.getenv("SCORIUM_KEYSTORE_PATH")
            if (!store.isNullOrBlank()) {
                storeFile = file(store)
                storePassword = System.getenv("SCORIUM_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("SCORIUM_KEY_ALIAS")
                keyPassword = System.getenv("SCORIUM_KEY_PASSWORD")
            }
        }
    }
    buildTypes {
        getByName("debug") {
            if (!System.getenv("SCORIUM_KEYSTORE_PATH").isNullOrBlank()) {
                signingConfig = signingConfigs.getByName("distribution")
            }
        }
    }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    kotlinOptions { jvmTarget = "17" }
}
dependencies {
    val bom = platform("androidx.compose:compose-bom:2025.02.00")
    implementation(bom)
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
}
