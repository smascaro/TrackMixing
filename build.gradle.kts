// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    val kotlin_version by extra("1.4.10")
    repositories {
        google()
        jcenter()

    }
    dependencies {
        classpath(Dependencies.Plugins.buildGradle)
        classpath(Dependencies.Plugins.kotlinGradle)

        classpath(Dependencies.Plugins.safeargs)
        "classpath"("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}


allprojects {
    repositories {
        google()
        jcenter()
    }
}

tasks.register("clean").configure {
    delete("build")
}