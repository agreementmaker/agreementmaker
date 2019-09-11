plugins {
    `java-library`
}

dependencies {
    implementation(project(":core"))
    api(project(":similarity-metrics"))
    implementation(project(":matchers-common"))
    implementation(project(":matcher-base-similarity"))
}