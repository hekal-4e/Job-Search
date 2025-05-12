plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.jobsearchapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.jobsearchapp"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }


    // Fix for the Bouncycastle conflict
    packaging {
        resources {
            excludes += listOf(
                "/META-INF/{AL2.0,LGPL2.1}",
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/license.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/notice.txt",
                "META-INF/ASL2.0",
                "META-INF/*.kotlin_module",
                "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
            )
        }
    }
}

dependencies {
    implementation(libs.cronet.embedded)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.identity.jvm)
    implementation(libs.firebase.functions.ktx)
    implementation(libs.play.services.auth)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.room.common.jvm)
    implementation(libs.androidx.storage)
    coreLibraryDesugaring(libs.desugar.jdk.libs)


    // -- Compose BOM (manages all Compose UI versions) --
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.00"))

    // -- Firebase BOM and libs --
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.core)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.firebase.ads)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.database)

    // -- Play Services --
    implementation(libs.play.services.base)
    implementation(libs.play.services.tasks)
    implementation(libs.play.services.basement)
    implementation(libs.play.services.ads)

    // -- AndroidX Core & Lifecycle --
    implementation(libs.androidx.core.ktx.v1120)
    implementation(libs.androidx.lifecycle.runtime.ktx.v270)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // -- Navigation --
    implementation(libs.androidx.navigation.compose)

    // -- Coil (image loading) --
    implementation(libs.coil.compose)

    // -- UI / Material3 --
    implementation(libs.material3)
    implementation(libs.androidx.compose.material3.material3)
    implementation("androidx.compose.material:material-icons-extended:1.6.3")
    implementation("androidx.compose.material:material:1.8.0-rc03")

    // -- Compose UI & Tooling --
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // -- Animation & Lottie --
    implementation("androidx.compose.animation:animation")
    implementation("com.airbnb.android:lottie-compose:6.6.6")

    // -- Testing --
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // -- Coroutines --
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.0")
}
