plugins {
    `java-library`
}

dependencies {
    implementation(project(":wordnet"))
    implementation("org.apache.logging.log4j:log4j-api:2.11.1")
    implementation("com.github.agreementmaker:simmetrics:1.6.2")

    testImplementation("junit:junit:4.11")
}