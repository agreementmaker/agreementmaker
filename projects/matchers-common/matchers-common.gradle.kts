plugins {
    `java-library`
}

dependencies {
    implementation(project(":core"))
    implementation(project(":similarity-metrics"))
    implementation(project(":wordnet"))

    implementation("net.sf.jwordnet:jwnl:1.4_rc3")
    api("com.github.agreementmaker:simpack:0.91")
    implementation("org.apache.logging.log4j:log4j-api:2.11.1")

    testImplementation("junit:junit:4.11")
    testImplementation("org.hamcrest:hamcrest-all:1.3")
    testImplementation("org.mockito:mockito-core:3.0.0")
}