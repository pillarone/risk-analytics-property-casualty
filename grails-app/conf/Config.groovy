import org.pillarone.riskanalytics.core.output.batch.DerbyBulkInsert
import org.pillarone.riskanalytics.core.output.batch.MysqlBulkInsert
import org.pillarone.riskanalytics.core.output.batch.SQLServerBulkInsert

// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if(System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.types = [html: ['text/html', 'application/xhtml+xml'],
        xml: ['text/xml', 'application/xml'],
        text: 'text-plain',
        js: 'text/javascript',
        rss: 'application/rss+xml',
        atom: 'application/atom+xml',
        css: 'text/css',
        csv: 'text/csv',
        all: '*/*',
        json: ['application/json', 'text/json'],
        form: 'application/x-www-form-urlencoded',
        multipartForm: 'multipart/form-data'
]
// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"

// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true

maxIterations = 100000
keyFiguresToCalculate = null
batchInsert = null
userLogin = false
// a cron for a batch, A cron expression is a string comprised of 6 or 7 fields separated by white space.
// Fields can contain any of the allowed values: Sec Min Hour dayOfMonth month dayOfWeek Year
// Fire every 60 minutes
batchCron = "0 0/10 * * * ?"
environments {

    development {
        ExceptionSafeOut = System.out
        log4j = {
            info 'org.pillarone.riskanalytics.core.output',
                    'org.pillarone.riskanalytics.core.components',
                    'org.pillarone.riskanalytics.core.simulation',
                    'org.pillarone.modelling.fileimport',
                    'org.pillarone.modelling.domain',
                    'org.pillarone.modelling.packets',
                    'org.pillarone.riskanalytics.core.simulation.engine',
                    'org.pillarone.riskanalytics.core.parameterization'

            debug 'org.pillarone.modelling.output',
                    'org.pillarone.riskanalytics.domain.life.reinsurance.UnitLinkedLifeReinsuranceContractPacket'

            warn()
        }
        keyFiguresToCalculate = [
                'stdev': true,
                'percentile': [0.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0],
                'var': [99, 99.5],
                'tvar': [99, 99.5],
                'pdf': 200
        ]
    }
    test {
        ExceptionSafeOut = System.out
        keyFiguresToCalculate = [
                'stdev': true,
                'percentile': [0.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0],
                'var': [99, 99.5],
                'tvar': [99, 99.5],
                'pdf': 200
        ]
    }
    sqlserver {
        models = ["FiniteReModel"]
        batchInsert = SQLServerBulkInsert
        keyFiguresToCalculate = [
                'stdev': true,
                'percentile': [0.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0],
                'var': [99, 99.5],
                'tvar': [99, 99.5],
        ]
        log4j = {
            info 'org.pillarone.riskanalytics.core.output',
                    'org.pillarone.riskanalytics.core.components',
                    'org.pillarone.riskanalytics.core.simulation',
                    'org.pillarone.modelling.fileimport',
                    'org.pillarone.modelling.domain',
                    'org.pillarone.modelling.packets',
                    'org.pillarone.riskanalytics.core.parameterization',
                    'org.pillarone.application.jobs.JobScheduler',
                    'org.pillarone.riskanalytics.core.simulation.engine',
                    'org.pillarone.application.jobs.BatchRunner'
        }
    }
    mysql {
        batchInsert = MysqlBulkInsert
        ExceptionSafeOut = System.out
//        models = ["PodraPModel"]
//        models << "CapitalEagleModel"
//        models << "DynamicCapitalEagleModel"
        models = ["MultiProductStatutoryLifeModel"]
//        models << "PodraPModel"
//        models << "ReservesWithPatternModel"
//        models << "FiniteReModel"
        log4j = {
            appenders {
                console name: 'stdout', layout: pattern(conversionPattern: '[%d] %-5p %c{1} %m%n')
                file name: 'file', file: 'RiskAnalytics.log', layout: pattern(conversionPattern: '[%d] %-5p %c{1} %m%n')
            }
            root {
                error 'stdout', 'file'
                additivity = false
            }
            info 'org.pillarone.riskanalytics.core.output',
                    'org.pillarone.riskanalytics.core.components',
                    'org.pillarone.riskanalytics.core.simulation',
                    'org.pillarone.modelling.fileimport',
                    'org.pillarone.modelling.domain',
                    'org.pillarone.modelling.packets',
                    'org.pillarone.riskanalytics.core.parameterization',
                    'org.pillarone.application.jobs.JobScheduler',
                    'org.pillarone.riskanalytics.core.simulation.engine',
                    'org.pillarone.application.jobs.BatchRunner',
                    'org.pillarone.modelling.ui.main.action.ImportAllAction',
                    'org.pillarone.modelling.ui.main.action.ItemLoadHandler'

            debug 'org.pillarone.modelling.output',
                    //'org.pillarone.riskanalytics.domain.life.accounting.Account'
                    //'org.pillarone.riskanalytics.domain.life.reinsurance.UnitLinkedLifeReinsuranceContractPacket',
                    //'org.pillarone.modelling.fileimport'

                    warn()
        }
        keyFiguresToCalculate = [
                'stdev': true,
                'percentile': [0.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0],
                'var': [99, 99.5],
                'tvar': [99, 99.5],
                'pdf': 200
        ]
    }
    trunksandbox {
        batchInsert = MysqlBulkInsert
        userLogin = true
        keyFiguresToCalculate = [
                'stdev': true,
                'percentile': [0.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0],
                'var': [99, 99.5],
                'tvar': [99, 99.5],
                'pdf': 200
        ]
    }

    production {
        batchInsert = MysqlBulkInsert
        userLogin = true
        maxIterations = 10000
        models = ["CapitalEagleModel", "DependencyModel", "DynamicCapitalEagleModel", "MultiLineReinsuranceModel", "TwoLobDependencyModel", "PodraPModel"]
        keyFiguresToCalculate = [
                'stdev': true,
                'percentile': [0.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0],
                'var': [99, 99.5],
                'tvar': [99, 99.5],
                'pdf': 200
        ]
    }

    standalone {
        batchInsert = DerbyBulkInsert
        ExceptionSafeOut = System.err
        maxIterations = 10000
        models = ["CapitalEagleModel", "DependencyModel", "DynamicCapitalEagleModel", "MultiLineReinsuranceModel", "TwoLobDependencyModel", "PodraPModel"]
        keyFiguresToCalculate = [
                'stdev': true,
                'percentile': [0.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0],
                'var': [99, 99.5],
                'tvar': [99, 99.5],
                'pdf': 200
        ]
    }
}

log4j = {
    appenders {
        console name: 'stdout', layout: pattern(conversionPattern: '[%d] %-5p %c{1} %m%n')
        file name: 'file', file: 'RiskAnalytics.log', layout: pattern(conversionPattern: '[%d] %-5p %c{1} %m%n')
    }
    root {
        error 'stdout', 'file'
        additivity = false
    }
    error 'org.codehaus.groovy.grails.web.servlet',  //  controllers
            'org.codehaus.groovy.grails.web.pages', //  GSP
            'org.codehaus.groovy.grails.web.sitemesh', //  layouts
            'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
            'org.codehaus.groovy.grails.web.mapping', // URL mapping
            'org.codehaus.groovy.grails.commons', // core / classloading
            'org.codehaus.groovy.grails.plugins', // plugins
            'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
//        'org.springframework',
//        'org.hibernate',
            'org.pillarone.modelling.fileimport',
            'org.pillarone.modelling.ui.util.ExceptionSafe',
            'org.pillarone.riskanalytics.core.wiring',
            'org.pillarone.modelling.domain',
            'org.pillarone.modelling.util'
    info()
    debug()
    warn()
}