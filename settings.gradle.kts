rootProject.name = "agreementmaker"

include(
        "api",
        "wordnet",
        "alignment-repair",
        "core",
        "similarity-metrics",
        "matchers-common",
        "matcher-advanced-similarity",
        "matcher-base-similarity",
        "matcher-linked-open-data",
        "matcher-oaei",
        "matcher-pra",
        "matcher-imei2013",
        "ui"
)

for (project in rootProject.children) {
    project.apply {
        projectDir = file("projects/$name")
        buildFileName = "$name.gradle.kts"
    }
}