import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.commons.ApplicationHolder

class ConfigAccessTests extends GroovyTestCase {

// TODO (Sep 8, 2009, msh): Why do they not run on Hudson
//    void testConfigAccessWithConfigurationHolder() {
//
//        assertNotNull "ConfigurationHolder.getConfig()", ConfigurationHolder.getConfig()
//        assertNotNull "ConfigurationHolder.config", ConfigurationHolder.config
//        assertNotNull "ConfigurationHolder.config.keyFiguresToCalculate", ConfigurationHolder.config.keyFiguresToCalculate
//    }

    void testConfigAccessWithApplication() {

        assertNotNull " ApplicationHolder.getApplication().getConfig()", ApplicationHolder.getApplication().getConfig()
        assertNotNull " ApplicationHolder.getApplication().config", ApplicationHolder.getApplication().config
        assertNotNull " ApplicationHolder.getApplication().config.keyFiguresToCalculate", ApplicationHolder.getApplication().config.keyFiguresToCalculate
    }
}