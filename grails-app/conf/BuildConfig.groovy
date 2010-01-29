grails.plugin.repos.discovery.pillarone ="https://readplugins:readplugins@svn.intuitive-collaboration.com/GrailsPlugins/"
grails.plugin.repos.resolveOrder = ['pillarone', 'default', 'core']

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