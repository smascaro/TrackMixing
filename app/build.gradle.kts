plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("androidx.navigation.safeargs.kotlin")
    id(Libs.Plugins.checkDependencyUpdates).version (Versions.Plugins.checkDependencyUpdates)
}

android {
    compileSdkVersion(Config.Sdk.compile)
    buildToolsVersion = Config.buildToolsVersion

    defaultConfig {
        applicationId = "com.smascaro.trackmixing"
        minSdkVersion(Config.Sdk.min)
        targetSdkVersion(Config.Sdk.target)
        versionCode = Config.Version.code
        versionName = Config.Version.name

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }


        packagingOptions {
            exclude("META-INF/atomicfu.kotlin_module")
        }
    }

// To inline the bytecode built with JVM target 1.8 inato
// bytecode that is being built with JVM target 1.6. (e.g. navArgs)

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    appModule()
}
