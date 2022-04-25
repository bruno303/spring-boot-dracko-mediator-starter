rootProject.name = "spring-boot-dracko-mediator-starter"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("mockito-core", "org.mockito", "mockito-core").version("4.3.1")
            library("dracko-mediator-core", "com.github.bruno303", "dracko-mediator").version("1.1.0")
            library("junit", "org.junit.jupiter", "junit-jupiter").version("5.8.2")
        }
        create("springLibs") {
            library("starter", "org.springframework.boot", "spring-boot-starter").withoutVersion()
            library("test", "org.springframework.boot", "spring-boot-starter-test").withoutVersion()
        }
    }

    repositories {
        mavenLocal()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

