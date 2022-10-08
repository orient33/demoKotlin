plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
//    compileSdkVersion(33)
    compileSdk = 33
    defaultConfig {
        applicationId = "com.example.kotlindemo"
        minSdk = 21
        targetSdk = 28
        versionCode = 1
        versionName = "1.0"
//        minSdkVersion(21)
//        targetSdkVersion(28)
    }
    //https://blog.jetbrains.com/kotlin/2021/02/the-jvm-backend-is-in-beta-let-s-make-it-stable-together/
//    kotlinOptions.useIR = true
    //https://developer.android.com/topic/libraries/view-binding
    buildFeatures {
        viewBinding = true
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }
    namespace = "com.example.kotlindemo"
}

dependencies {
//    implementation(fileTree(include:['*.jar'], dir: 'libs'))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.10")
    implementation("androidx.window:window:1.0.0")
    implementation("androidx.core:core-splashscreen:1.0.0")
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("androidx.fragment:fragment-ktx:1.5.3")
    implementation("androidx.preference:preference-ktx:1.2.0")
    implementation("androidx.palette:palette-ktx:1.0.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.6.1")
    implementation("androidx.annotation:annotation:1.5.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")

    implementation("androidx.room:room-runtime:2.4.3")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
//    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    kapt("androidx.room:room-compiler:2.4.3")
    // ViewModel and LiveData
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
//    kapt("androidx.lifecycle:lifecycle-compiler:2.2.0")
    // alternately - if using Java8, use the following instead of compiler
    implementation("androidx.lifecycle:lifecycle-common-java8:2.5.1")

    implementation("androidx.lifecycle:lifecycle-livedata-core-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("com.google.crypto.tink:tink-android:1.2.2")
    implementation("com.github.bumptech.glide:glide:4.14.0")
    kapt("com.github.bumptech.glide:compiler:4.14.0")
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.7")
    api("com.squareup.retrofit2:retrofit:2.9.0")
    api("com.squareup.retrofit2:converter-gson:2.9.0")
    api("com.squareup.okhttp3:okhttp:4.10.0")
    api("com.squareup.okhttp3:logging-interceptor:4.10.0")
    api("com.google.android.exoplayer:exoplayer-core:2.18.1")

    implementation("com.airbnb.android:lottie:4.0.0") //support包用2.7.0
    implementation("pub.devrel:easypermissions:3.0.0")//非androidX时候使用版本 2.0.1
    //方便调试,查看数据库的.可以在PC上直接浏览db,,不需要每次pull出来文件.
//    debugImplementation ("com.amitshekhar.android:debug-db:1.0.6")
}
