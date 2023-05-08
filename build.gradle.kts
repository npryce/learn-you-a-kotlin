plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    api("com.google.code.findbugs:jsr305:3.0.2")
    api("com.fasterxml.jackson.core:jackson-databind:2.15.0")

    testImplementation("junit:junit:4.13.2")
    testImplementation("com.natpryce:hamkrest:1.8.0.1")
    testImplementation("com.oneeyedmen:okeydoke:1.3.3")
}
