plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id(Libs.Plugins.checkDependencyUpdates).version (Versions.Plugins.checkDependencyUpdates)
}
android {
    compileSdkVersion(Config.Sdk.compile)
    buildToolsVersion = Config.buildToolsVersion

    defaultConfig {
        minSdkVersion(Config.Sdk.min)
        targetSdkVersion(Config.Sdk.target)
        versionCode = Config.Version.code
        versionName = Config.Version.name

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles.add(File("consumer-rules.pro"))
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    searchModule()
}