plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.calcite:calcite-core:1.26.0")
    implementation("org.apache.calcite:calcite-file:1.26.0")
    implementation("org.apache.calcite:calcite-linq4j:1.26.0")
    implementation("org.apache.calcite.avatica:avatica-core:1.17.0")

    implementation("com.google.guava:guava:30.1-jre")
    implementation("commons-io:commons-io:2.8.0")
    implementation("com.opencsv:opencsv:5.3")

    implementation("sqlline:sqlline:1.11.0")
}

val buildSqllineClasspath by tasks.registering(Jar::class) {
    archiveClassifier.set("all")
    archiveFileName.set("sqllineClasspath.jar")

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes("Main-Class" to "sqlline.SqlLine")
    }

    from(configurations.runtimeClasspath.get()
            .onEach { println("add from dependencies: ${it.name}") }
            .map { if (it.isDirectory) it else zipTree(it) })
            .exclude(
                "META-INF/MANIFEST.MF", "META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA"
            )

    val sourcesMain = sourceSets.main.get()
    sourcesMain.allSource.forEach { println("add from sources: ${it.name}") }
    from(sourcesMain.output)
}
