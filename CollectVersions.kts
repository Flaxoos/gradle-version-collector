import java.io.File

val projectRoot = "your project root"

val versionsFile = File("$projectRoot/buildSrc/src/main/kotlin/Versions.kt")

versionsFile.writeText(
    File(projectRoot).walkTopDown().filter { it.name.endsWith("gradle.kts") }.map { file ->
        extractVersions(file)
    }.toList().reduce { a, b ->
        a + b
    }.entries.map {
        "const val ${it.key} = \"${it.value}\""
    }.joinToString("\n")
)

fun extractVersions(file: File): Map<String, String> {
    val versions = mutableMapOf<String, String>()
    val edited = file.readLines().map { line ->
        if (
            // improve by taking lines in "dependencies { }"
            (line.trimStart().startsWith("implementation") || line.trimStart().startsWith("api")) &&
            (line.count { it == ':' } == 2)
        ) {
            val version = line.substringAfterLast(":").substringBefore("\")")
            val name = line.substringAfter(":").substringBefore(":").let {
                it.mapIndexed { i, c ->
                    if (i > 0 && it[i - 1] == '-') {
                        c.uppercaseChar()
                    } else {
                        c
                    }
                }.asSequence()
                    .filter { it != '-' }
                    .map { if (it == '.') '_' else it }
                    .joinToString("")
            } + "Version"
            versions[name] = version
            line.replace("$version\")", "\$$name\")")
        } else {
            line
        }
    }.toList()
    file.writeText(edited.joinToString("\n").plus("\n"))
    return versions
}
