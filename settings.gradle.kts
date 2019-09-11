rootProject.name = "agreementmaker"

include(
        "alignment-repair",
        "api",
        "batch-mode",
        "collaboration-client",
        "core",
        "matchers-common",
        "matcher-advanced-similarity",
        "matcher-base-similarity",
        "matcher-imei2013",
        "matcher-linked-open-data",
        "matcher-oaei",
        "matcher-pra",
        "matcher-registry",
        "similarity-metrics",
        "ui",
        "wordnet"
)

for (project in rootProject.children) {
    project.apply {
        projectDir = file("projects/$name")
        buildFileName = "$name.gradle.kts"
    }
}