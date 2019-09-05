rootProject.name = "agreementmaker"

include(
        "api",
        "wordnet",
        "alignment-repair",
        "common",
        "similarity-metrics"
)

for (project in rootProject.children) {
    project.apply {
        if (File("${rootProject.projectDir.absolutePath}/core/$name").exists()) {
            projectDir = file("core/$name")
        }

        buildFileName = "$name.gradle.kts"
    }
}