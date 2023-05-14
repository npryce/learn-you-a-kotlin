plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    api("com.google.code.findbugs:jsr305:3.0.2")
    api("com.fasterxml.jackson.core:jackson-databind:2.15.0")
    implementation("org.glassfish.jersey.core:jersey-server:3.1.1")
    
    testImplementation(platform("org.junit:junit-bom:5.9.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("com.natpryce:hamkrest:1.8.0.1")
    testImplementation("com.oneeyedmen:okeydoke:1.3.3")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
