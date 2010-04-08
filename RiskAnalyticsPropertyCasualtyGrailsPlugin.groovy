import org.pillarone.riskanalytics.domain.pc.constraints.ReservePortion
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.pc.constraints.UnderwritingPortion
import org.pillarone.riskanalytics.domain.pc.constraints.PerilPortion
import org.pillarone.riskanalytics.core.parameterization.SimpleConstraint
import org.pillarone.riskanalytics.domain.pc.output.AggregatedDrillDownCollectingModeStrategy
import org.pillarone.riskanalytics.core.output.CollectingModeFactory
import org.pillarone.riskanalytics.domain.utils.constraints.DoubleConstraints

class RiskAnalyticsPropertyCasualtyGrailsPlugin {
    // the plugin version
    def version = "0.6"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.2.0 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def author = "Intuitive Collaboration"
    def authorEmail = "info (at) intuitive-collaboration (dot) com"
    def title = "Property Casualty Library and Example Models"
    def description = '''\\

'''

    // URL to the plugin's documentation
    def documentation = "http://www.pillarone.org"

    def doWithWebDescriptor = {xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before 
    }

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = {ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = {applicationContext ->
        CollectingModeFactory.registerStrategy(new AggregatedDrillDownCollectingModeStrategy())

        ConstraintsFactory.registerConstraint(new SimpleConstraint())
        ConstraintsFactory.registerConstraint(new PerilPortion())
        ConstraintsFactory.registerConstraint(new UnderwritingPortion())
        ConstraintsFactory.registerConstraint(new ReservePortion())
        ConstraintsFactory.registerConstraint(new DoubleConstraints())
    }

    def onChange = {event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = {event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}
