//Use a custom plugins dir, because different branches use different plugin versions
grails.project.plugins.dir = "../local-plugins/RiskAnalyticsPropertyCasualty-master"

grails.project.dependency.resolution = {
    inherits "global" // inherit Grails' default dependencies
    log "warn"

    repositories {
        grailsHome()
        grailsPlugins()
        grailsCentral()

        mavenCentral()
        mavenRepo "https://repository.intuitive-collaboration.com/nexus/content/repositories/pillarone-public/"
    }

    plugins {
        runtime ":background-thread:1.3"
        runtime ":hibernate:2.2.1"
        runtime ":joda-time:0.5"
        runtime ":maven-publisher:0.7.5", {
            excludes "groovy"
        }
        runtime ":quartz:0.4.2"
        runtime ":spring-security-core:1.2.7.3"
        runtime ":tomcat:2.2.1"

        test ":code-coverage:1.2.4"
        compile ":excel-import:0.9.6"

        if (appName == "RiskAnalyticsPropertyCasualty") {
            runtime "org.pillarone:risk-analytics-core:1.7-a5"
            runtime("org.pillarone:risk-analytics-commons:0.5") { transitive = false }
        }
    }

    dependencies {
        test 'hsqldb:hsqldb:1.8.0.10'
    }
}

grails.project.dependency.distribution = {
    String password = ""
    String user = ""
    String scpUrl = ""
    try {
        Properties properties = new Properties()
        properties.load(new File("${userHome}/deployInfo.properties").newInputStream())

        user = properties.get("user")
        password = properties.get("password")
        scpUrl = properties.get("url")
    } catch (Throwable t) {
    }
    remoteRepository(id: "pillarone", url: scpUrl) {
        authentication username: user, password: password
    }
}

coverage {
    enabledByDefault = false
    xml = true
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