plugins {
    `java-library`
}

dependencies {
    implementation(project(":common"))
    implementation(project(":similarity-metrics"))
    implementation(project(":matchers-common"))
    implementation(project(":matcher-base-similarity"))
}