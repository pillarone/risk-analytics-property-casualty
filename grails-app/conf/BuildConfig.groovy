//Use a custom plugins dir, because different branches use different plugin versions
grails.project.plugins.dir = "../local-plugins/RiskAnalyticsPropertyCasualty-master"

grails.project.dependency.resolver = "maven"

grails.project.dependency.resolution = {
    inherits "global" // inherit Grails' default dependencies
    log "warn"

    repositories {
        grailsHome()
        grailsPlugins()
        grailsCentral()

        mavenCentral()
        mavenRepo "https://repository.intuitive-collaboration.com/nexus/content/repositories/pillarone-public/"
        mavenRepo("https://repository.intuitive-collaboration.com/nexus/content/repositories/pillarone-public-snapshot/") {
            updatePolicy System.getProperty('snapshotUpdatePolicy') ?: 'daily'
        }
        mavenRepo "http://repo.spring.io/milestone/" //needed for spring-security-core 2.0-rc2 plugin

    }

    plugins {
        runtime ":background-thread:1.3"
        runtime ":hibernate:3.6.10.3"
        runtime ":joda-time:0.5"
        runtime ":release:3.0.1", {
            excludes "groovy"
        }
        runtime ":quartz:0.4.2"
        runtime ":spring-security-core:2.0-RC2"
        runtime ":tomcat:7.0.42"

        test ":code-coverage:1.2.7"
        compile ":excel-import:1.0.0"

        if (appName == "RiskAnalyticsPropertyCasualty") {
            runtime "org.pillarone:risk-analytics-core:1.9-SNAPSHOT"
            runtime("org.pillarone:risk-analytics-commons:1.9-SNAPSHOT") { transitive = false }
        }
    }

    dependencies {
        test 'hsqldb:hsqldb:1.8.0.10'
        compile(group: 'org.apache.poi', name: 'poi', version: '3.9');
        compile(group: 'org.apache.poi', name: 'poi-ooxml', version: '3.9') {
            excludes 'xmlbeans'
        }
    }
}

grails.project.dependency.distribution = {
    String password = ""
    String user = ""
    String scpUrl = ""
    try {
        Properties properties = new Properties()
        String version = new GroovyClassLoader().loadClass('RiskAnalyticsPropertyCasualtyGrailsPlugin').newInstance().version
        properties.load(new File("${userHome}/deployInfo.properties").newInputStream())
        user = properties.get("user")
        password = properties.get("password")

        if (version?.endsWith('-SNAPSHOT')) {
            scpUrl = properties.get("urlSnapshot")
        } else {
            scpUrl = properties.get("url")
        }
        remoteRepository(id: "pillarone", url: scpUrl) {
            authentication username: user, password: password
        }
    } catch (Throwable t) {
    }
}

coverage {
    enabledByDefault = false
    exclusions = [
            'models/**',
            '**/*Test*',
            '**/com/energizedwork/grails/plugins/jodatime/**',
            '**/grails/util/**',
            '**/org/codehaus/**',
            '**/org/grails/**',
            '**GrailsPlugin**',
            '**TagLib**'
    ]

}
