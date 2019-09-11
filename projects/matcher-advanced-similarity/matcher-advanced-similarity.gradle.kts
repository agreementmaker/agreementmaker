plugins {
    `java-library`
}

dependencies {
    implementation(project(":core"))
    implementation(project(":similarity-metrics"))
    implementation(project(":matchers-common"))
    implementation(project(":matcher-base-similarity"))
}