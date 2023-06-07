plugins {
    `java-library`
    kotlin("jvm") version "1.8.21"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    api("org.jetbrains:annotations:24.0.1")
    api("com.fasterxml.jackson.core:jackson-databind:2.15.0")
    implementation("org.glassfish.jersey.core:jersey-server:3.1.1")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.2.1")
    
    testImplementation(platform("org.junit:junit-bom:5.9.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("com.natpryce:hamkrest:1.8.0.1")
    testImplementation("com.oneeyedmen:okeydoke:1.3.3")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
