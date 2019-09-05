import java.net.URI

allprojects{
    repositories {
        mavenCentral()
        maven {
            url = URI("https://www.onawh.im/maven")
        }
    }
}

subprojects {
    apply(plugin = "java")
}