plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}
dependencies {
    // Потрібно для анотації @Inject
    implementation(libs.javax.inject)

    // Потрібно для suspend-функцій та Flow
    // (Припускаю, що ця залежність є у вашому libs.versions.toml)
    //implementation(libs.androidx.lifecycle.runtime.ktx)
    // Якщо `runtime.ktx` не спрацює,
    // спробуйте `kotlinx.coroutines.core`
    implementation(libs.kotlinx.coroutines.core)
}