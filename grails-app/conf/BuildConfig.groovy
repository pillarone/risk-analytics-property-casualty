//Use a custom plugins dir, because different branches use different plugin versions
grails.project.plugins.dir = "../local-plugins/RiskAnalyticsPropertyCasualty-2.1.0"

grails.project.dependency.resolution = {
    inherits "global" // inherit Grails' default dependencies
    log "warn"

    repositories {
        grailsHome()
        grailsCentral()

        mavenRepo "https://repository.intuitive-collaboration.com/nexus/content/repositories/pillarone-public/"
    }


    plugins {
        runtime ":background-thread:1.3"
        runtime ":hibernate:2.1.0"
        runtime ":joda-time:0.5"
        runtime(":release:2.0.3") { excludes "groovy" }
        runtime ":quartz:0.4.2"
        runtime ":spring-security-core:1.2.7.3"
        runtime ":tomcat:2.0.1"

        test ":code-coverage:1.2.4"
        compile(":excel-import:0.9.6") { excludes "xmlbeans" }

        if (appName == "RiskAnalyticsPropertyCasualty") {
            runtime "org.pillarone:risk-analytics-core:1.6-ALPHA-4.12-2.1.0"
            runtime("org.pillarone:risk-analytics-commons:0.4.6-2.1.0") { transitive = false }
        }
    }

    dependencies {
        test "hsqldb:hsqldb:1.8.0.10"
    }
}
//grails.plugin.location.'risk-analytics-core' = "../RiskAnalyticsCore"
//grails.plugin.location.'risk-analytics-commons' = "../risk-analytics-commons"

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