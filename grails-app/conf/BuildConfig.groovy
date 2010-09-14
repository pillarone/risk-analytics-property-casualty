import org.apache.ivy.plugins.resolver.URLResolver

grails.project.dependency.resolution = {
    inherits "global" // inherit Grails' default dependencies
    log "warn"

    repositories {
        grailsHome()
        grailsCentral()
    }

    def myResolver = new URLResolver()
    myResolver.addArtifactPattern "https://svn.intuitive-collaboration.com/GrailsPlugins/grails-[artifact]/tags/LATEST_RELEASE/grails-[artifact]-[revision].[ext]"

    resolver myResolver
}

coverage {
    exclusions = [
            'models/**',
            '**/org/grails/tomcat/*',
            '**/org/pillarone/riskanalytics/core/**',
            '**/*Test*',
            '**/com/ulcjava/**',
            '**/com/energizedwork/grails/plugins/jodatime/**',
            '**/jasper/**',
            '**/com/canoo/**',
            '**/*Ulc*',
            '**/*ULC*',
            '**/grails/util/**',
            '**/org/codehaus/**',
            '**/org/grails/**',
            '**/de/andreasschmitt/**',
            '**GrailsPlugin**',
            '**Taglib**',
            '**TagLib**'
    ]
}