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
}