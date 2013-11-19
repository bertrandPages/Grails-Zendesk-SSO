grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsCentral()
    }
    dependencies {
        compile 'com.nimbusds:nimbus-jose-jwt:2.16'
    }

    plugins {
        build(':release:2.2.1',
              ':rest-client-builder:1.0.3') {
            export = false
        }
    }
}
