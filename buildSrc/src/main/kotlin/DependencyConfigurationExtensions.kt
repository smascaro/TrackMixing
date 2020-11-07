import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.project

fun DependencyHandler.implementation(dependency: Any) {
    add("implementation", dependency)
}

fun DependencyHandler.testImplementation(dependency: Any) {
    add("testImplementation", dependency)
}

fun DependencyHandler.androidTestImplementation(dependency: Any) {
    add("androidTestImplementation", dependency)
}

fun DependencyHandler.api(dependency: Any) {
    add("api", dependency)
}

fun DependencyHandler.annotationProcessor(dependency: Any) {
    add("annotationProcessor", dependency)
}

fun DependencyHandler.kapt(dependency: Any) {
    add("kapt", dependency)
}

fun DependencyHandler.androidModule(module: String) {
    implementation(project(module))
}