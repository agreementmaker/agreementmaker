plugins {
    java
}

dependencies {
    implementation(project(":core"))
    implementation(project(":ui"))
    implementation(project(":batch-mode"))
    implementation(project(":matcher-registry"))
    implementation(project(":matcher-linked-open-data"))
    implementation(project(":matcher-imei2013"))
    implementation(project(":user-feedback"))
}