import java.net.URL
import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("maven-publish")
}

android {
    namespace = "com.radlance.uikit"
    compileSdk = 35

    defaultConfig {
        minSdk = 30

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {}
        debug {}
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}


publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "com.radlance.uikit"
            artifactId = "core"
            version = "1.0.0"

            afterEvaluate {
                from(components["release"])
            }
        }
    }

    val properties = Properties()
    properties.load(project.rootProject.file("local.properties").inputStream())

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/radlance/uikit")
            credentials {
                username = properties.getProperty("grp.user")
                password = properties.getProperty("grp.key")
            }
        }
    }

    publications {
        register<MavenPublication>("gpr")
    }
}

dependencies {
    implementation(files("${rootDir}/libs/downloaded-library.aar"))
    
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

tasks.register("downloadAar") {
    doLast {
        val aarUrl =
            "https://github.com/radlance/uikit/releases/download/1.0.2/app-release.aar"
        val targetDir = File("${project.rootDir}/libs")
        val aarFile = File(targetDir, "downloaded-library.aar")

        if (!aarFile.exists()) {
            targetDir.mkdirs()
            URL(aarUrl).openStream().use { input ->
                aarFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
    }
}

tasks.register("deleteAar") {
    doLast {
        val targetDir = File("${project.rootDir}/libs")
        val aarFile = File(targetDir, "downloaded-library.aar")

        if (aarFile.exists()) {
            aarFile.delete()
            println("AAR file deleted successfully")
        } else {
            println("AAR file not found")
        }
    }
}

