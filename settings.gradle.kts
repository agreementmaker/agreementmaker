rootProject.name = "agreementmaker"

include(
        "api",
        "wordnet",
        "alignment-repair",
        "common",
        "similarity-metrics",
        "matchers-common",
        "matcher-advanced-similarity",
        "matcher-base-similarity",
        "ui"
)

for (project in rootProject.children) {
    project.apply {
        projectDir = file("projects/$name")
        buildFileName = "$name.gradle.kts"
    }
}