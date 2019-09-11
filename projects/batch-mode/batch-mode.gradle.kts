plugins {
    `java-library`
}

dependencies {
    implementation(project(":core"))
    implementation(project(":matchers-common"))
    implementation(project(":matcher-oaei"))
    implementation(project(":matcher-advanced-similarity"))
    implementation(project(":matcher-pra"))

    implementation("javax.xml.bind:jaxb-api:2.3.1")
    runtimeOnly("org.glassfish.jaxb:jaxb-runtime:2.3.1")

    testImplementation("junit:junit:4.11")
    testImplementation("org.hamcrest:hamcrest-all:1.3")
    testImplementation("com.google.guava:guava:18.0")
}