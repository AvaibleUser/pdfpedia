plugins {
	java
	jacoco
	id("org.springframework.boot") version "3.4.3"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "org.cunoc"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
	implementation("com.warrenstrange:googleauth:1.4.0")
	implementation("com.cloudinary:cloudinary-http44:1.39.0")
	implementation("org.xhtmlrenderer:flying-saucer-pdf:9.9.1")
	compileOnly("org.projectlombok:lombok")
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testRuntimeOnly("com.h2database:h2")
	testCompileOnly("org.projectlombok:lombok")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)

	reports {
		xml.required = true
		html.required = true
		csv.required = false
		xml.outputLocation = layout.buildDirectory.file("reports/jacoco/test/jacoco.xml")
	}

    classDirectories.setFrom(
        files(
            classDirectories.files.map {
                fileTree(it) {
                    setExcludes(listOf(
                        "**/entity/**/*",
                        "**/dto/**/*",
						"**/export/**/*"))
                }
            }
        )
    )
}

tasks.jacocoTestCoverageVerification {
	dependsOn(tasks.jacocoTestReport)

	violationRules {
		rule {
            classDirectories.setFrom(tasks.jacocoTestReport.get().classDirectories)
			limit {
				minimum = "0.85".toBigDecimal()
			}
		}
	}
}