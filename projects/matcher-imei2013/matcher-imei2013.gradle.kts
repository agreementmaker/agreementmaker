plugins {
    `java-library`
}

dependencies {
    implementation(project(":core"))
    implementation(project(":matchers-common"))
    implementation(project(":matcher-base-similarity"))
    implementation(project(":matcher-advanced-similarity"))
    implementation(project(":matcher-pra"))
    implementation(project(":matcher-oaei"))
    implementation(project(":matcher-linked-open-data"))

    implementation("org.apache.logging.log4j:log4j-api:2.11.1")
}