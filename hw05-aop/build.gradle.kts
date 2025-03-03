import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow")
}

dependencies {
    implementation ("org.assertj:assertj-core")
    implementation ("ch.qos.logback:logback-classic")
    implementation("net.bytebuddy:byte-buddy")
    testImplementation("net.bytebuddy:byte-buddy-agent")


}

tasks {

    create<ShadowJar>("proxyDemoJar") {
        archiveBaseName.set("proxyDemo")
        archiveVersion.set("")
        archiveClassifier.set("")
        manifest {
            attributes(mapOf("Main-Class" to "ru.sinvic.homework.instrumentation.proxy.ProxyDemo",
                "Premain-Class" to "ru.sinvic.homework.instrumentation.proxy.Agent"))
        }
        from(sourceSets.main.get().output)
        configurations = listOf(project.configurations.runtimeClasspath.get())
    }

    build {
        dependsOn("proxyDemoJar")
    }
}
