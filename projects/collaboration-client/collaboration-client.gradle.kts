plugins {
    `java-library`
}

dependencies {
    implementation(project(":core"))
    implementation(project(":matcher-oaei"))
    implementation(project(":batch-mode"))

    implementation("org.apache.logging.log4j:log4j-api:2.11.1")

    implementation("javax.ws.rs:jsr311-api:1.1.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.9.9.2")
}