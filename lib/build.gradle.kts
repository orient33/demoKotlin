//plugins {
//    id "org.jetbrains.kotlin.jvm" version "1.3.11"
//}
plugins {
    id("java-library")
    id("kotlin")
}

dependencies {
//    implementation(fileTree(dir: "libs", include: ["*.jar"])
//    implementation("io.reactivex.rxjava2:rxjava:2.2.20")
    implementation("com.google.code.gson:gson:2.10.1")
//    implementation("org.simpleframework:simple-xml:2.7.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$1.8.20")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
//sourceCompatibility = "8"
//targetCompatibility = "8"
//sourceSets {
//    main.kotlin.srcDirs += "src/main/java"
//    main.java.srcDirs += "src/main/java"
//}

//repositories {
//    mavenCentral()
//}
//compileKotlin {
//    kotlinOptions {
//        jvmTarget = "1.8"
//    }
//}
//compileTestKotlin {
//    kotlinOptions {
//        jvmTarget = "1.8"
//    }
//}
