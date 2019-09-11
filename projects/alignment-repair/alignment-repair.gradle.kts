plugins {
    `java-library`
}

dependencies {
    implementation("org.apache.logging.log4j:log4j-api:2.11.1")
    implementation(project(":core"))
    implementation("com.hermit-reasoner:org.semanticweb.hermit:1.3.8.4")
    implementation("dom4j:dom4j:1.6.1")
    implementation("xml-apis:xml-apis:1.4.01")
}