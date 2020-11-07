object Libs {
    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk7"

    object Androidx {
        const val appCompat = "androidx.appcompat:appcompat"
        const val core = "androidx.core:core-ktx"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout"
        const val recyclerView = "androidx.recyclerview:recyclerview"
        const val palette = "androidx.palette:palette"
        const val preference = "androidx.preference:preference"

        object Navigation {
            const val ui = "androidx.navigation:navigation-ui"
            const val uiKtx = "androidx.navigation:navigation-ui-ktx"
            const val fragment = "androidx.navigation:navigation-fragment"
            const val fragmentKtx = "androidx.navigation:navigation-fragment-ktx"
        }

        object Test {
            const val runner = "androidx.test:runner"
            const val core = "androidx.test:core"
            const val junitKtx = "androidx.test.ext:junit-ktx"
            const val espresso = "androidx.test.espresso:espresso-core"
        }
    }

    const val timber = "com.jakewharton.timber:timber"

    object Dagger {
        const val core = "com.google.dagger:dagger"
        const val compilerKapt = "com.google.dagger:dagger-compiler"
        const val annotations = "com.google.dagger:dagger-compiler"
    }

    const val shimmer = "com.facebook.shimmer:shimmer"
    const val junit = "junit:junit"

    object Mockito {
        const val core = "org.mockito:mockito-core"
        const val inline = "org.mockito:mockito-inline"
        const val mockitoKotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin"
    }

    object CoroutinesTest {
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test"
    }

    object Room {
        const val runtime = "androidx.room:room-runtime"
        const val ktx = "androidx.room:room-ktx"
        const val annotationsCompiler = "androidx.room:room-compiler"
        const val kaptCompiler = "androidx.room:room-compiler"
    }

    object Retrofit {
        const val retrofit = "com.squareup.retrofit2:retrofit"
        const val converterGson = "com.squareup.retrofit2:converter-gson"
    }

    object Glide {
        const val glide = "com.github.bumptech.glide:glide"
        const val compiler = "com.github.bumptech.glide:compiler"
    }

    const val eventBus = "org.greenrobot:eventbus"
    const val material = "com.google.android.material:material"
    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core"
    const val gson = "com.google.code.gson:gson"
    const val exoplayer = "com.google.android.exoplayer:exoplayer"

    object Plugins {
        const val buildGradle = "com.android.tools.build:gradle"
        const val kotlinGradle = "org.jetbrains.kotlin:kotlin-gradle-plugin"
        const val safeargs = "androidx.navigation:navigation-safe-args-gradle-plugin"
        const val checkDependencyUpdates = "name.remal.check-dependency-updates"
    }
}

