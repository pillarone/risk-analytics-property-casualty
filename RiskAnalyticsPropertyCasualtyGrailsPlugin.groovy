import org.pillarone.riskanalytics.core.output.CollectingModeFactory
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.parameterization.SimpleConstraint
import org.pillarone.riskanalytics.core.parameterization.validation.ValidatorRegistry
import org.pillarone.riskanalytics.core.util.ResourceBundleRegistry
import org.pillarone.riskanalytics.domain.pc.constraints.CompanyPortion
import org.pillarone.riskanalytics.domain.pc.constraints.validation.UnityPortionValidator
import org.pillarone.riskanalytics.domain.pc.filter.SegmentFilterValidator
import org.pillarone.riskanalytics.domain.pc.generators.claims.validation.ClaimsGeneratorStrategyValidator
import org.pillarone.riskanalytics.domain.pc.generators.claims.validation.TypableClaimsGeneratorValidator
import org.pillarone.riskanalytics.domain.pc.generators.copulas.validation.DependencyMatrixValidator
import org.pillarone.riskanalytics.domain.pc.output.AggregateDrillDownCollectingModeStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.validation.CommissionStrategyTypeValidator
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.validation.XLStrategyValidator
import org.pillarone.riskanalytics.domain.pc.underwriting.validation.RiskBandsValidator
import org.pillarone.riskanalytics.domain.pc.validation.UnityDoubleValidator
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints
import org.pillarone.riskanalytics.domain.utils.validation.DistributionTypeValidatorPC

class RiskAnalyticsPropertyCasualtyGrailsPlugin {
    // the plugin version
    def version = "1.6-RC-1-kti"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.3.7 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def author = "Intuitive Collaboration AG"
    def authorEmail = "info (at) intuitive-collaboration (dot) com"
    def title = "Property Casualty Library and Example Models"
    def description = '''\\

'''

    // URL to the plugin's documentation
    def documentation = "http://www.pillarone.org"

    def groupId = "org.pillarone"

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
        CollectingModeFactory.registerStrategy(new AggregateDrillDownCollectingModeStrategy())
        ConstraintsFactory.registerConstraint(new DoubleConstraints())
        ConstraintsFactory.registerConstraint(new SimpleConstraint())
        ConstraintsFactory.registerConstraint(new CompanyPortion())

        ValidatorRegistry.addValidator(new DistributionTypeValidatorPC())
//        ValidatorRegistry.addValidator(new SurplusStrategyValidator())
        ValidatorRegistry.addValidator(new CommissionStrategyTypeValidator())
        ValidatorRegistry.addValidator(new ClaimsGeneratorStrategyValidator())
        ValidatorRegistry.addValidator(new UnityPortionValidator())
        ValidatorRegistry.addValidator(new UnityDoubleValidator())
        ValidatorRegistry.addValidator(new XLStrategyValidator())
        ValidatorRegistry.addValidator(new SegmentFilterValidator())
        ValidatorRegistry.addValidator(new DependencyMatrixValidator())
        ValidatorRegistry.addValidator(new RiskBandsValidator())
//        ValidatorRegistry.addValidator(new FacShareAndDistributionsValidator())
        ValidatorRegistry.addValidator(new TypableClaimsGeneratorValidator())

        // add resource bundle for exceptions
        ResourceBundleRegistry.addBundle(ResourceBundleRegistry.RESOURCE, "org.pillarone.riskanalytics.exceptionResources")

        //add resource bundle for validation
        //new
        ResourceBundleRegistry.addBundle(ResourceBundleRegistry.VALIDATION, "org.pillarone.riskanalytics.domain.pc.validation.commissionStrategyType")
        // note: distributionType.properties is outdated, new validation i18n is distributionTypeValidatorPC
        ResourceBundleRegistry.addBundle(ResourceBundleRegistry.VALIDATION, "org.pillarone.riskanalytics.domain.utils.validation.distributionTypeValidatorPC")
        ResourceBundleRegistry.addBundle(ResourceBundleRegistry.VALIDATION, "org.pillarone.riskanalytics.domain.pc.validation.unityDouble")
        ResourceBundleRegistry.addBundle(ResourceBundleRegistry.VALIDATION, "org.pillarone.riskanalytics.domain.pc.generators.claims.validation.claimsGeneratorStrategyValidator")
        ResourceBundleRegistry.addBundle(ResourceBundleRegistry.VALIDATION, "org.pillarone.riskanalytics.domain.pc.filter.segmentFilterValidator")
        ResourceBundleRegistry.addBundle(ResourceBundleRegistry.VALIDATION, "org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.validation.xlStrategyValidator")
        ResourceBundleRegistry.addBundle(ResourceBundleRegistry.VALIDATION, "org.pillarone.riskanalytics.domain.pc.underwriting.validation.riskBands")
        ResourceBundleRegistry.addBundle(ResourceBundleRegistry.VALIDATION, "org.pillarone.riskanalytics.domain.pc.generators.claims.validation.typableClaimsGeneratorValidator")

        //"org/pillarone/riskanalytics/application/help/ComponentHelp"
        ResourceBundleRegistry.addBundle(ResourceBundleRegistry.HELP, "org/pillarone/riskanalytics/help/ComponentHelp")
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
