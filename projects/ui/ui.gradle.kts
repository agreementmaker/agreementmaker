plugins {
    `java-library`
}

dependencies {
    implementation(project(":core"))
    implementation(project(":matchers-common"))

    implementation("com.jidesoft:jide-oss:2.11.1")

    implementation("com.barchart.kitfox:kitfox-svg-core:1.0.0-build001")
    implementation("org.apache.logging.log4j:log4j-api:2.11.1")
}