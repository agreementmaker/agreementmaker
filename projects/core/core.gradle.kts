plugins {
    `java-library`
}

dependencies {
    implementation("org.apache.logging.log4j:log4j-api:2.11.1")
    implementation(project(":api"))

    api(project(":wordnet"))

    implementation("net.sf.jwordnet:jwnl:1.4_rc3")

    implementation("dom4j:dom4j:1.6.1")
    implementation("xml-apis:xml-apis:1.4.01")

    api("commons-lang:commons-lang:2.6")
    implementation("commons-io:commons-io:2.3")
    implementation("commons-collections:commons-collections:3.2.2")
    api("org.apache.commons:commons-compress:1.18")

    api("org.apache.jena:jena-core:2.13.0")
    api("org.apache.jena:jena-tdb:1.1.2")
    api("org.apache.jena:jena-sdb:1.5.2")

    implementation("org.openrdf.sesame:sesame-repository-sail:2.6.10")
    implementation("org.openrdf.sesame:sesame-repository-http:2.6.10")
    implementation("org.openrdf.sesame:sesame-sail-memory:2.6.10")
    implementation("org.openrdf.sesame:sesame-rio-api:2.6.10")
    implementation("org.openrdf.sesame:sesame-rio-ntriples:2.6.10")
    implementation("org.openrdf.sesame:sesame-rio-n3:2.6.10")
    implementation("org.openrdf.sesame:sesame-rio-trig:2.6.10")
    implementation("org.openrdf.sesame:sesame-rio-rdfxml:2.6.10")
    implementation("org.openrdf.sesame:sesame-sail-rdbms:2.6.10")

    implementation("colt:colt:1.2.0")

    api("nz.ac.waikato.cms.weka:weka-stable:3.6.7")

    api("com.github.agreementmaker:cluster-gvm:1.1")

    api("com.github.agreementmaker:secondstring:20120620")

    implementation("net.sf.jopt-simple:jopt-simple:4.3")

    testImplementation("junit:junit:4.11")
    testImplementation("org.hamcrest:hamcrest-all:1.3")
    testImplementation("org.mockito:mockito-core:3.0.0")
}