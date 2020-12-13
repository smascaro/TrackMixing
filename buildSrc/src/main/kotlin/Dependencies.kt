import org.gradle.api.artifacts.dsl.DependencyHandler

// region Dependencies definitions
object Dependencies {
    const val kotlin = "${Libs.kotlin}:${Config.kotlinVersion}"
    const val appcompat = "${Libs.Androidx.appCompat}:${Versions.Androidx.appcompat}"
    const val coreKtx = "${Libs.Androidx.core}:${Versions.Androidx.core}"
    const val constraintLayout =
        "${Libs.Androidx.constraintLayout}:${Versions.Androidx.constraintLayout}"
    const val recyclerView = "${Libs.Androidx.recyclerView}:${Versions.Androidx.recyclerView}"
    const val timber = "${Libs.timber}:${Versions.timber}"
    const val navigationUi = "${Libs.Androidx.Navigation.ui}:${Versions.Androidx.navigation}"
    const val navigationUiKtx = "${Libs.Androidx.Navigation.uiKtx}:${Versions.Androidx.navigation}"
    const val navigationFragment =
        "${Libs.Androidx.Navigation.fragment}:${Versions.Androidx.navigation}"
    const val navigationFragmentKtx =
        "${Libs.Androidx.Navigation.fragmentKtx}:${Versions.Androidx.navigation}"
    const val daggerCore = "${Libs.Dagger.core}:${Versions.dagger}"
    const val daggerAnnotations = "${Libs.Dagger.annotations}:${Versions.dagger}"
    const val daggerKapt = "${Libs.Dagger.compilerKapt}:${Versions.dagger}"
    const val shimmer = "${Libs.shimmer}:${Versions.shimmer}"
    const val junit = "${Libs.junit}:${Versions.junit}"
    const val mockitoCore = "${Libs.Mockito.core}:${Versions.mockito}"
    const val mockitoInline = "${Libs.Mockito.inline}:${Versions.mockito}"
    const val mockitoKotlin = "${Libs.Mockito.mockitoKotlin}:${Versions.mockitoKotlin}"
    const val coroutinesTest = "${Libs.CoroutinesTest.test}:${Versions.coroutinesTest}"
    const val espresso = "${Libs.Androidx.Test.espresso}:${Versions.espresso}"
    const val testRunner = "${Libs.Androidx.Test.runner}:${Versions.Androidx.test}"
    const val testCore = "${Libs.Androidx.Test.core}:${Versions.Androidx.test}"
    const val testJunitKtx = "${Libs.Androidx.Test.junitKtx}:${Versions.Androidx.testJunit}"

    object Room {
        const val runtime = "${Libs.Room.runtime}:${Versions.room}"
        const val ktx = "${Libs.Room.ktx}:${Versions.room}"
        const val annotationsCompiler = "${Libs.Room.annotationsCompiler}:${Versions.room}"
        const val kapt = "${Libs.Room.kaptCompiler}:${Versions.room}"
    }

    object Retrofit {
        const val retrofit = "${Libs.Retrofit.retrofit}:${Versions.retrofit}"
        const val converterGson = "${Libs.Retrofit.converterGson}:${Versions.retrofit}"
    }

    object Glide {
        const val glide = "${Libs.Glide.glide}:${Versions.glide}"
        const val compiler = "${Libs.Glide.compiler}:${Versions.glide}"
    }

    const val eventBus = "${Libs.eventBus}:${Versions.eventBus}"
    const val palette = "${Libs.Androidx.palette}:${Versions.Androidx.palette}"

    const val preference = "${Libs.Androidx.preference}:${Versions.Androidx.preference}"
    const val material = "${Libs.material}:${Versions.material}"
    const val coroutines = "${Libs.coroutines}:${Versions.coroutines}"
    const val gson = "${Libs.gson}:${Versions.gson}"
    const val exoplayer = "${Libs.exoplayer}:${Versions.exoplayer}"

    object Plugins {
        const val checkDependencyUpdates =
            "${Libs.Plugins.checkDependencyUpdates}:${Versions.Plugins.checkDependencyUpdates}"
        const val buildGradle = "${Libs.Plugins.buildGradle}:${Versions.Plugins.gradle}"
        const val kotlinGradle = "${Libs.Plugins.kotlinGradle}:${Config.kotlinVersion}"
        const val safeargs = "${Libs.Plugins.safeargs}:${Versions.Plugins.safeargs}"
    }
}
// endregion

// region Dependencies per library
fun DependencyHandler.timber() {
    implementation(Dependencies.timber)
}

fun DependencyHandler.kotlin() {
    implementation(Dependencies.kotlin)
}

fun DependencyHandler.appcompat() {
    implementation(Dependencies.appcompat)
}

fun DependencyHandler.coreKtx() {
    implementation(Dependencies.coreKtx)
}

fun DependencyHandler.constraintLayout() {
    implementation(Dependencies.constraintLayout)
}

fun DependencyHandler.recyclerView() {
    implementation(Dependencies.recyclerView)
}

fun DependencyHandler.navigationComponent() {
    implementation(Dependencies.navigationUi)
    implementation(Dependencies.navigationUiKtx)
    implementation(Dependencies.navigationFragment)
    implementation(Dependencies.navigationFragmentKtx)
}

fun DependencyHandler.dagger() {
    implementation(Dependencies.daggerCore)
    annotationProcessor(Dependencies.daggerAnnotations)
    kapt(Dependencies.daggerKapt)
}

fun DependencyHandler.shimmer() {
    implementation(Dependencies.shimmer)
}

fun DependencyHandler.junit() {
    testImplementation(Dependencies.junit)
}

fun DependencyHandler.mockito() {
    testImplementation(Dependencies.mockitoCore)
    testImplementation(Dependencies.mockitoInline)
    testImplementation(Dependencies.mockitoKotlin)
}

fun DependencyHandler.coroutinesTest() {
    testImplementation(Dependencies.coroutinesTest)
}

fun DependencyHandler.espresso() {
    androidTestImplementation(Dependencies.espresso)
}

fun DependencyHandler.androidxTest() {
    androidTestImplementation(Dependencies.testRunner)
    androidTestImplementation(Dependencies.testCore)
    androidTestImplementation(Dependencies.testJunitKtx)
}

fun DependencyHandler.room() {
    implementation(Dependencies.Room.runtime)
    implementation(Dependencies.Room.ktx)
    annotationProcessor(Dependencies.Room.annotationsCompiler)
    kapt(Dependencies.Room.kapt)
}

fun DependencyHandler.retrofit() {
    api(Dependencies.Retrofit.retrofit)
    implementation(Dependencies.Retrofit.converterGson)
}

fun DependencyHandler.glide() {
    api(Dependencies.Glide.glide)
    annotationProcessor(Dependencies.Glide.compiler)
}

fun DependencyHandler.eventBus() {
    api(Dependencies.eventBus)
}

fun DependencyHandler.palette() {
    implementation(Dependencies.palette)
}

fun DependencyHandler.androidxPreference() {
    implementation(Dependencies.preference)
}

fun DependencyHandler.material() {
    implementation(Dependencies.material)
}

fun DependencyHandler.coroutines() {
    implementation(Dependencies.coroutines)
}

fun DependencyHandler.gson() {
    implementation(Dependencies.gson)
}

fun DependencyHandler.exoplayer() {
    implementation(Dependencies.exoplayer)
}
// endregion

// region Modules
//region :app
fun DependencyHandler.appModule() {
    androidModule(":base")
    androidModule(":playback")
    androidModule(":player")
    androidModule(":search")
    androidModule(":settings")
    kotlin()
    appcompat()
    coreKtx()
    constraintLayout()
    recyclerView()
    timber()
    navigationComponent()
    dagger()
    shimmer()
    junit()
    mockito()
    coroutinesTest()
    espresso()
    androidxTest()
}
// endregion

// region :base
fun DependencyHandler.baseModule() {
    kotlin()
    coreKtx()
    appcompat()
    constraintLayout()
    navigationComponent()
    timber()
    room()
    dagger()
    retrofit()
    glide()
    eventBus()
    palette()
    junit()
    androidxTest()
    espresso()
}
// endregion

// region :settings
fun DependencyHandler.settingsModule() {
    androidModule(":base")
    kotlin()
    coreKtx()
    material()
    navigationComponent()
    androidxPreference()
    dagger()
    timber()
    shimmer()
    junit()
    espresso()
    androidxTest()
}
// endregion

// region :playback
fun DependencyHandler.playbackModule() {
    androidModule(":base")
    kotlin()
    coreKtx()
    coroutines()
    gson()
    exoplayer()
    timber()
    dagger()
    junit()
    androidxTest()
    espresso()
}
// endregion

// region :search
fun DependencyHandler.searchModule() {
    androidModule(":base")
    kotlin()
    coreKtx()
    material()
    navigationComponent()
    dagger()
    timber()
    shimmer()
    junit()
    androidxTest()
    espresso()
}
// endregion

// region :player
fun DependencyHandler.playerModule() {
    androidModule(":base")
    androidModule(":playback")
    kotlin()
    coreKtx()
    coroutines()
    material()
    dagger()
    timber()
}
// endregion
// endregion