pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Thêm Jitpack để đảm bảo tải được các thư viện cộng đồng (nếu cần)
        maven { url = uri("https://www.jitpack.io") }
    }
}

rootProject.name = "MusicPlayer"
include(":app")