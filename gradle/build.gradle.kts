plugins {
    java

    // Resolved via the Gradle Plugin Portal, which redirects its dependencies to Maven Central.
    // Not applied, resolving the plugin classpath is enough to hit Central.
    id("org.springframework.boot") version "4.2.0" apply false
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.commons:commons-lang3:3.22.0")

    testImplementation(platform("org.junit:junit-bom:6.1.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 25
}

tasks.test {
    useJUnitPlatform()
}
