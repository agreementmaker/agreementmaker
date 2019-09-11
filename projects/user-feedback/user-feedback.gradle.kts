plugins {
    `java-library`
}

dependencies {
    implementation(project(":core"))
    implementation(project(":ui"))
    implementation(project(":matchers-common"))
    implementation(project(":matcher-base-similarity"))
    implementation(project(":matcher-advanced-similarity"))
    implementation(project(":collaboration-client"))
    implementation(project(":batch-mode"))

    implementation("com.github.agreementmaker:javaplot:0.4.0")
    implementation("org.codehaus.jackson:jackson-mapper-lgpl:1.9.13")
    implementation("org.codehaus.jackson:jackson-core-lgpl:1.9.13")
    implementation("com.thoughtworks.xstream:xstream:1.4.6")

    testImplementation("junit:junit:4.11")
    testImplementation("org.hamcrest:hamcrest-all:1.3")
    testImplementation("org.mockito:mockito-core:3.0.0")
}