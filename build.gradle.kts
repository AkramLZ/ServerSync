/*
 * MIT License
 *
 * Copyright (c) 2023 Akram Louze
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

plugins {
    id("java")
}

group = "me.akraml"
version = "1.0-SNAPSHOT"

subprojects {
    apply(plugin = "java")

    repositories {
        mavenCentral()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }

    dependencies {
        implementation("com.rabbitmq:amqp-client:5.19.0")
        implementation("redis.clients:jedis:5.0.2")
        implementation("com.google.code.gson:gson:2.10.1")
    }

    val generateVersionClass by tasks.registering {
        val outputDir = File(project.buildDir, "generated/sources/version")
        val className = "VersionInfo"
        val packageName = "me.akraml.serversync"
        val fileContent = """
        package $packageName;

        public final class $className {
            public static final String VERSION = "${project.version}";
        }
    """.trimIndent()

        val filePath = packageName.replace('.', File.separatorChar)
        val file = File(outputDir, filePath)

        doLast {
            file.mkdirs()
            val javaFile = File(file, "$className.java")
            javaFile.writeText(fileContent)
        }
        outputs.dir(outputDir)
    }

    tasks.named("compileJava") {
        dependsOn(generateVersionClass)
    }

    sourceSets["main"].java.srcDir(File(buildDir, "generated/sources/version"))

}