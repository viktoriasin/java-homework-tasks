dependencies {
    implementation ("org.assertj:assertj-core")
    implementation ("ch.qos.logback:logback-classic")
}

val launchCustomTestFramework = tasks.register<JavaExec>("launchCustomTestFramework") {
    mainClass.set("homework.Main")
    classpath = java.sourceSets["main"].runtimeClasspath
}