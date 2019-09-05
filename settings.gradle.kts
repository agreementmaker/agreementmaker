rootProject.name = "agreementmaker"

include(
        "api"
)

for (project in rootProject.children) {
    project.apply {
        buildFileName = "$name.gradle.kts"
    }
}