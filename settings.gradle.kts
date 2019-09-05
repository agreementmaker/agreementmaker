rootProject.name = "agreementmaker"

include(
        "api",
        "wordnet"
)

for (project in rootProject.children) {
    project.apply {
        projectDir = file("core/$name")
        buildFileName = "$name.gradle.kts"
    }
}