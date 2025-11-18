plugins {
    // Define a versão do plugin do Android
    id("com.android.application") version "8.4.1" apply false

    // Define a versão do Kotlin
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false

    // Define o plugin do Compose Compiler
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false

    // Define o plugin do Kapt
    id("org.jetbrains.kotlin.kapt") version "2.0.0" apply false

    // Define o plugin do Google Services
    id("com.google.gms.google-services") version "4.4.4" apply false
}