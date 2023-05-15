plugins {
    `java-library`
    kotlin("jvm") version "1.8.21"
}

repositories {
    mavenCentral()
}

dependencies {
    api("com.google.code.findbugs:jsr305:3.0.2")
    api("com.fasterxml.jackson.core:jackson-databind:2.15.0")
    implementation("org.glassfish.jersey.core:jersey-server:3.1.1")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.2.1")
    
    testImplementation(platform("org.junit:junit-bom:5.9.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("com.natpryce:hamkrest:1.8.0.1")
    testImplementation("com.oneeyedmen:okeydoke:1.3.3")
    implementation(kotlin("stdlib-jdk8"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
kotlin {
    jvmToolchain {
        java.toolchain.languageVersion
    }
}
