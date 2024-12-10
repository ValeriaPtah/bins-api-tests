plugins {
    id("java")
}

group = "ptah"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

//Versions
val commonsLang3Version = "3.15.0"
val lombokVersion = "1.18.34"
val moshiVersion = "1.15.1"
val restAssuredVersion = "5.5.0"
val testNgVersion = "7.10.2"

dependencies {
    testImplementation("org.testng:testng:$testNgVersion")
    testImplementation("org.apache.commons:commons-lang3:$commonsLang3Version")
    testImplementation("org.projectlombok:lombok:$lombokVersion")
    testImplementation("com.squareup.moshi:moshi:$moshiVersion")
    testImplementation("com.squareup.moshi:moshi-adapters:$moshiVersion")
    testImplementation("io.rest-assured:rest-assured:$restAssuredVersion")
    testImplementation("io.rest-assured:json-schema-validator:$restAssuredVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")
}

tasks.test {
    useTestNG()
}
