group = "${parent?.group}"
version = "${parent?.version}"

dependencies {
    compileOnly("net.md-5:bungeecord-api:1.19-R0.1-SNAPSHOT")
    implementation(project(":serversync-common"))
}

tasks.compileJava {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}

tasks.named<ProcessResources>("processResources") {
    expand("projectVersion" to project.version)
    filesMatching("bungee.yml") {
        expand("project.version" to project.version)
    }
}