import models.podra.PodraModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest
import org.pillarone.riskanalytics.core.output.PathMapping
import org.pillarone.riskanalytics.core.output.FieldMapping
import org.pillarone.riskanalytics.core.output.SingleValueResult
import org.pillarone.riskanalytics.core.output.DBOutput
import org.pillarone.riskanalytics.core.output.ICollectorOutputStrategy
import org.pillarone.riskanalytics.core.output.CollectorMapping
import org.pillarone.riskanalytics.core.output.AggregatedCollectingModeStrategy

class AggregateDrillDownCollectingModeStrategyTests extends ModelTest {

    Class getModelClass() {
        PodraModel
    }

    String getResultConfigurationFileName() {
        'TestPodraDrillDownResultConfiguration'
    }

    String getResultConfigurationDisplayName() {
        'Drill Down'
    }

    String getParameterFileName() {
        'TestPodraParameters'
    }

    String getParameterDisplayName() {
        'Drill Down Test Parameter'
    }

    protected ICollectorOutputStrategy getOutputStrategy() {
        new DBOutput()
    }

    int getIterationCount() {
        1
    }

    void setUp() {
        super.setUp()
        assertNotNull new CollectorMapping(collectorName: AggregatedCollectingModeStrategy.IDENTIFIER).save()
    }

    void postSimulationEvaluation() {
        correctPaths()
        correctFields(['incurred', 'paid', 'reserved','commission', 'fixed commission', 'variable commission', 'premium', 'fixed premium', 'variable premium'])
        correctPaidClaimsResults()
        correctCommissionsResults()
        correctPremiumResults()
    }

    void correctPaths() {
        List<String> paths = [
                'Podra:linesOfBusiness:outClaimsDevelopmentLeanCeded',
                'Podra:linesOfBusiness:outClaimsDevelopmentLeanGross',
                'Podra:linesOfBusiness:outClaimsDevelopmentLeanNet',
                'Podra:linesOfBusiness:subMotorHull:claimsGenerators:subMotorHullAttritional:outClaimsDevelopmentLeanCeded',
                'Podra:linesOfBusiness:subMotorHull:claimsGenerators:subMotorHullAttritional:outClaimsDevelopmentLeanGross',
                'Podra:linesOfBusiness:subMotorHull:claimsGenerators:subMotorHullAttritional:outClaimsDevelopmentLeanNet',
                'Podra:linesOfBusiness:subMotorHull:claimsGenerators:subMotorHullSingle:outClaimsDevelopmentLeanCeded',
                'Podra:linesOfBusiness:subMotorHull:claimsGenerators:subMotorHullSingle:outClaimsDevelopmentLeanGross',
                'Podra:linesOfBusiness:subMotorHull:claimsGenerators:subMotorHullSingle:outClaimsDevelopmentLeanNet',
                'Podra:linesOfBusiness:subMotorHull:outClaimsDevelopmentLeanCeded',
                'Podra:linesOfBusiness:subMotorHull:outClaimsDevelopmentLeanGross',
                'Podra:linesOfBusiness:subMotorHull:outClaimsDevelopmentLeanNet',
                'Podra:linesOfBusiness:subMotorHull:reinsuranceContracts:subMotorHullWxl:outClaimsDevelopmentLeanCeded',
                'Podra:linesOfBusiness:subMotorHull:reinsuranceContracts:subPropertyQuotaShare:outClaimsDevelopmentLeanCeded',
                'Podra:linesOfBusiness:subProperty:claimsGenerators:subPropertyAttritional:outClaimsDevelopmentLeanCeded',
                'Podra:linesOfBusiness:subProperty:claimsGenerators:subPropertyAttritional:outClaimsDevelopmentLeanGross',
                'Podra:linesOfBusiness:subProperty:claimsGenerators:subPropertyAttritional:outClaimsDevelopmentLeanNet',
                'Podra:linesOfBusiness:subProperty:claimsGenerators:subPropertyEarthquake:outClaimsDevelopmentLeanCeded',
                'Podra:linesOfBusiness:subProperty:claimsGenerators:subPropertyEarthquake:outClaimsDevelopmentLeanGross',
                'Podra:linesOfBusiness:subProperty:claimsGenerators:subPropertyEarthquake:outClaimsDevelopmentLeanNet',
                'Podra:linesOfBusiness:subProperty:claimsGenerators:subPropertySingle:outClaimsDevelopmentLeanCeded',
                'Podra:linesOfBusiness:subProperty:claimsGenerators:subPropertySingle:outClaimsDevelopmentLeanGross',
                'Podra:linesOfBusiness:subProperty:claimsGenerators:subPropertySingle:outClaimsDevelopmentLeanNet',
                'Podra:linesOfBusiness:subProperty:outClaimsDevelopmentLeanCeded',
                'Podra:linesOfBusiness:subProperty:outClaimsDevelopmentLeanGross',
                'Podra:linesOfBusiness:subProperty:outClaimsDevelopmentLeanNet',
                'Podra:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyCxl:outClaimsDevelopmentLeanCeded',
                'Podra:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyQuotaShare:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subMotorHullWxl:claimsGenerators:subMotorHullAttritional:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subContracts:subMotorHullWxl:claimsGenerators:subMotorHullAttritional:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subContracts:subMotorHullWxl:claimsGenerators:subMotorHullAttritional:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subMotorHullWxl:claimsGenerators:subMotorHullSingle:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subContracts:subMotorHullWxl:claimsGenerators:subMotorHullSingle:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subContracts:subMotorHullWxl:claimsGenerators:subMotorHullSingle:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subMotorHullWxl:linesOfBusiness:subMotorHull:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subContracts:subMotorHullWxl:linesOfBusiness:subMotorHull:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subContracts:subMotorHullWxl:linesOfBusiness:subMotorHull:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subMotorHullWxl:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subContracts:subMotorHullWxl:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subContracts:subMotorHullWxl:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subPropertyCxl:claimsGenerators:subPropertyAttritional:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subContracts:subPropertyCxl:claimsGenerators:subPropertyAttritional:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subContracts:subPropertyCxl:claimsGenerators:subPropertyAttritional:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subPropertyCxl:claimsGenerators:subPropertyEarthquake:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subContracts:subPropertyCxl:claimsGenerators:subPropertyEarthquake:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subContracts:subPropertyCxl:claimsGenerators:subPropertyEarthquake:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subPropertyCxl:claimsGenerators:subPropertySingle:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subContracts:subPropertyCxl:claimsGenerators:subPropertySingle:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subContracts:subPropertyCxl:claimsGenerators:subPropertySingle:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subPropertyCxl:linesOfBusiness:subProperty:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subContracts:subPropertyCxl:linesOfBusiness:subProperty:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subContracts:subPropertyCxl:linesOfBusiness:subProperty:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subPropertyCxl:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subContracts:subPropertyCxl:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subContracts:subPropertyCxl:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:claimsGenerators:subMotorHullAttritional:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:claimsGenerators:subMotorHullAttritional:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:claimsGenerators:subMotorHullAttritional:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:claimsGenerators:subMotorHullSingle:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:claimsGenerators:subMotorHullSingle:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:claimsGenerators:subMotorHullSingle:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:claimsGenerators:subPropertyAttritional:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:claimsGenerators:subPropertyAttritional:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:claimsGenerators:subPropertyAttritional:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:claimsGenerators:subPropertyEarthquake:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:claimsGenerators:subPropertyEarthquake:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:claimsGenerators:subPropertyEarthquake:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:claimsGenerators:subPropertySingle:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:claimsGenerators:subPropertySingle:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:claimsGenerators:subPropertySingle:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:linesOfBusiness:subMotorHull:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:linesOfBusiness:subMotorHull:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:linesOfBusiness:subMotorHull:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:linesOfBusiness:subProperty:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:linesOfBusiness:subProperty:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:linesOfBusiness:subProperty:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subriContract:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subContracts:subriContract:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subContracts:subriContract:outClaimsDevelopmentLeanNet',
                'Podra:structures:subFrance:claimsGenerators:subMotorHullAttritional:outClaimsCeded',
                'Podra:structures:subFrance:claimsGenerators:subMotorHullAttritional:outClaimsGross',
                'Podra:structures:subFrance:claimsGenerators:subMotorHullAttritional:outClaimsNet',
                'Podra:structures:subFrance:claimsGenerators:subMotorHullSingle:outClaimsCeded',
                'Podra:structures:subFrance:claimsGenerators:subMotorHullSingle:outClaimsGross',
                'Podra:structures:subFrance:claimsGenerators:subMotorHullSingle:outClaimsNet',
                'Podra:structures:subFrance:claimsGenerators:subPropertyAttritional:outClaimsCeded',
                'Podra:structures:subFrance:claimsGenerators:subPropertyAttritional:outClaimsGross',
                'Podra:structures:subFrance:claimsGenerators:subPropertyAttritional:outClaimsNet',
                'Podra:structures:subFrance:claimsGenerators:subPropertyEarthquake:outClaimsCeded',
                'Podra:structures:subFrance:claimsGenerators:subPropertyEarthquake:outClaimsGross',
                'Podra:structures:subFrance:claimsGenerators:subPropertyEarthquake:outClaimsNet',
                'Podra:structures:subFrance:claimsGenerators:subPropertySingle:outClaimsCeded',
                'Podra:structures:subFrance:claimsGenerators:subPropertySingle:outClaimsGross',
                'Podra:structures:subFrance:claimsGenerators:subPropertySingle:outClaimsNet',
                'Podra:structures:subFrance:linesOfBusiness:subMotorHull:claimsGenerators:subMotorHullAttritional:outClaimsCeded',
                'Podra:structures:subFrance:linesOfBusiness:subMotorHull:claimsGenerators:subMotorHullAttritional:outClaimsGross',
                'Podra:structures:subFrance:linesOfBusiness:subMotorHull:claimsGenerators:subMotorHullAttritional:outClaimsNet',
                'Podra:structures:subFrance:linesOfBusiness:subMotorHull:claimsGenerators:subMotorHullSingle:outClaimsCeded',
                'Podra:structures:subFrance:linesOfBusiness:subMotorHull:claimsGenerators:subMotorHullSingle:outClaimsGross',
                'Podra:structures:subFrance:linesOfBusiness:subMotorHull:claimsGenerators:subMotorHullSingle:outClaimsNet',
                'Podra:structures:subFrance:linesOfBusiness:subMotorHull:outClaimsCeded',
                'Podra:structures:subFrance:linesOfBusiness:subMotorHull:outClaimsGross',
                'Podra:structures:subFrance:linesOfBusiness:subMotorHull:outClaimsNet',
                'Podra:structures:subFrance:linesOfBusiness:subMotorHull:reinsuranceContracts:subMotorHullWxl:claimsGenerators:subMotorHullAttritional:outClaimsCeded',
                'Podra:structures:subFrance:linesOfBusiness:subMotorHull:reinsuranceContracts:subMotorHullWxl:claimsGenerators:subMotorHullSingle:outClaimsCeded',
                'Podra:structures:subFrance:linesOfBusiness:subMotorHull:reinsuranceContracts:subMotorHullWxl:outClaimsCeded',
                'Podra:structures:subFrance:linesOfBusiness:subMotorHull:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subMotorHullAttritional:outClaimsCeded',
                'Podra:structures:subFrance:linesOfBusiness:subMotorHull:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subMotorHullSingle:outClaimsCeded',
                'Podra:structures:subFrance:linesOfBusiness:subMotorHull:reinsuranceContracts:subPropertyQuotaShare:outClaimsCeded',
                'Podra:structures:subFrance:linesOfBusiness:subProperty:claimsGenerators:subPropertyAttritional:outClaimsCeded',
                'Podra:structures:subFrance:linesOfBusiness:subProperty:claimsGenerators:subPropertyAttritional:outClaimsGross',
                'Podra:structures:subFrance:linesOfBusiness:subProperty:claimsGenerators:subPropertyAttritional:outClaimsNet',
                'Podra:structures:subFrance:linesOfBusiness:subProperty:claimsGenerators:subPropertyEarthquake:outClaimsCeded',
                'Podra:structures:subFrance:linesOfBusiness:subProperty:claimsGenerators:subPropertyEarthquake:outClaimsGross',
                'Podra:structures:subFrance:linesOfBusiness:subProperty:claimsGenerators:subPropertyEarthquake:outClaimsNet',
                'Podra:structures:subFrance:linesOfBusiness:subProperty:claimsGenerators:subPropertySingle:outClaimsCeded',
                'Podra:structures:subFrance:linesOfBusiness:subProperty:claimsGenerators:subPropertySingle:outClaimsGross',
                'Podra:structures:subFrance:linesOfBusiness:subProperty:claimsGenerators:subPropertySingle:outClaimsNet',
                'Podra:structures:subFrance:linesOfBusiness:subProperty:outClaimsCeded',
                'Podra:structures:subFrance:linesOfBusiness:subProperty:outClaimsGross',
                'Podra:structures:subFrance:linesOfBusiness:subProperty:outClaimsNet',
                'Podra:structures:subFrance:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertyAttritional:outClaimsCeded',
                'Podra:structures:subFrance:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertyEarthquake:outClaimsCeded',
                'Podra:structures:subFrance:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertySingle:outClaimsCeded',
                'Podra:structures:subFrance:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyCxl:outClaimsCeded',
                'Podra:structures:subFrance:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertyAttritional:outClaimsCeded',
                'Podra:structures:subFrance:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertyEarthquake:outClaimsCeded',
                'Podra:structures:subFrance:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertySingle:outClaimsCeded',
                'Podra:structures:subFrance:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyQuotaShare:outClaimsCeded',
                'Podra:structures:subFrance:outClaimsCeded',
                'Podra:structures:subFrance:outClaimsGross',
                'Podra:structures:subFrance:outClaimsNet',
                'Podra:structures:subFrance:reinsuranceContracts:subMotorHullWxl:claimsGenerators:subMotorHullAttritional:outClaimsCeded',
                'Podra:structures:subFrance:reinsuranceContracts:subMotorHullWxl:claimsGenerators:subMotorHullSingle:outClaimsCeded',
                'Podra:structures:subFrance:reinsuranceContracts:subMotorHullWxl:outClaimsCeded',
                'Podra:structures:subFrance:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertyAttritional:outClaimsCeded',
                'Podra:structures:subFrance:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertyEarthquake:outClaimsCeded',
                'Podra:structures:subFrance:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertySingle:outClaimsCeded',
                'Podra:structures:subFrance:reinsuranceContracts:subPropertyCxl:outClaimsCeded',
                'Podra:structures:subFrance:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subMotorHullAttritional:outClaimsCeded',
                'Podra:structures:subFrance:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subMotorHullSingle:outClaimsCeded',
                'Podra:structures:subFrance:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertyAttritional:outClaimsCeded',
                'Podra:structures:subFrance:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertyEarthquake:outClaimsCeded',
                'Podra:structures:subFrance:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertySingle:outClaimsCeded',
                'Podra:structures:subFrance:reinsuranceContracts:subPropertyQuotaShare:outClaimsCeded',
                'Podra:structures:subSuisse:claimsGenerators:subMotorHullAttritional:outClaimsCeded',
                'Podra:structures:subSuisse:claimsGenerators:subMotorHullAttritional:outClaimsGross',
                'Podra:structures:subSuisse:claimsGenerators:subMotorHullAttritional:outClaimsNet',
                'Podra:structures:subSuisse:claimsGenerators:subMotorHullSingle:outClaimsCeded',
                'Podra:structures:subSuisse:claimsGenerators:subMotorHullSingle:outClaimsGross',
                'Podra:structures:subSuisse:claimsGenerators:subMotorHullSingle:outClaimsNet',
                'Podra:structures:subSuisse:claimsGenerators:subPropertyAttritional:outClaimsCeded',
                'Podra:structures:subSuisse:claimsGenerators:subPropertyAttritional:outClaimsGross',
                'Podra:structures:subSuisse:claimsGenerators:subPropertyAttritional:outClaimsNet',
                'Podra:structures:subSuisse:claimsGenerators:subPropertyEarthquake:outClaimsCeded',
                'Podra:structures:subSuisse:claimsGenerators:subPropertyEarthquake:outClaimsGross',
                'Podra:structures:subSuisse:claimsGenerators:subPropertyEarthquake:outClaimsNet',
                'Podra:structures:subSuisse:claimsGenerators:subPropertySingle:outClaimsCeded',
                'Podra:structures:subSuisse:claimsGenerators:subPropertySingle:outClaimsGross',
                'Podra:structures:subSuisse:claimsGenerators:subPropertySingle:outClaimsNet',
                'Podra:structures:subSuisse:linesOfBusiness:subMotorHull:claimsGenerators:subMotorHullAttritional:outClaimsCeded',
                'Podra:structures:subSuisse:linesOfBusiness:subMotorHull:claimsGenerators:subMotorHullAttritional:outClaimsGross',
                'Podra:structures:subSuisse:linesOfBusiness:subMotorHull:claimsGenerators:subMotorHullAttritional:outClaimsNet',
                'Podra:structures:subSuisse:linesOfBusiness:subMotorHull:claimsGenerators:subMotorHullSingle:outClaimsCeded',
                'Podra:structures:subSuisse:linesOfBusiness:subMotorHull:claimsGenerators:subMotorHullSingle:outClaimsGross',
                'Podra:structures:subSuisse:linesOfBusiness:subMotorHull:claimsGenerators:subMotorHullSingle:outClaimsNet',
                'Podra:structures:subSuisse:linesOfBusiness:subMotorHull:outClaimsCeded',
                'Podra:structures:subSuisse:linesOfBusiness:subMotorHull:outClaimsGross',
                'Podra:structures:subSuisse:linesOfBusiness:subMotorHull:outClaimsNet',
                'Podra:structures:subSuisse:linesOfBusiness:subMotorHull:reinsuranceContracts:subMotorHullWxl:claimsGenerators:subMotorHullAttritional:outClaimsCeded',
                'Podra:structures:subSuisse:linesOfBusiness:subMotorHull:reinsuranceContracts:subMotorHullWxl:claimsGenerators:subMotorHullSingle:outClaimsCeded',
                'Podra:structures:subSuisse:linesOfBusiness:subMotorHull:reinsuranceContracts:subMotorHullWxl:outClaimsCeded',
                'Podra:structures:subSuisse:linesOfBusiness:subMotorHull:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subMotorHullAttritional:outClaimsCeded',
                'Podra:structures:subSuisse:linesOfBusiness:subMotorHull:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subMotorHullSingle:outClaimsCeded',
                'Podra:structures:subSuisse:linesOfBusiness:subMotorHull:reinsuranceContracts:subPropertyQuotaShare:outClaimsCeded',
                'Podra:structures:subSuisse:linesOfBusiness:subProperty:claimsGenerators:subPropertyAttritional:outClaimsCeded',
                'Podra:structures:subSuisse:linesOfBusiness:subProperty:claimsGenerators:subPropertyAttritional:outClaimsGross',
                'Podra:structures:subSuisse:linesOfBusiness:subProperty:claimsGenerators:subPropertyAttritional:outClaimsNet',
                'Podra:structures:subSuisse:linesOfBusiness:subProperty:claimsGenerators:subPropertyEarthquake:outClaimsCeded',
                'Podra:structures:subSuisse:linesOfBusiness:subProperty:claimsGenerators:subPropertyEarthquake:outClaimsGross',
                'Podra:structures:subSuisse:linesOfBusiness:subProperty:claimsGenerators:subPropertyEarthquake:outClaimsNet',
                'Podra:structures:subSuisse:linesOfBusiness:subProperty:claimsGenerators:subPropertySingle:outClaimsCeded',
                'Podra:structures:subSuisse:linesOfBusiness:subProperty:claimsGenerators:subPropertySingle:outClaimsGross',
                'Podra:structures:subSuisse:linesOfBusiness:subProperty:claimsGenerators:subPropertySingle:outClaimsNet',
                'Podra:structures:subSuisse:linesOfBusiness:subProperty:outClaimsCeded',
                'Podra:structures:subSuisse:linesOfBusiness:subProperty:outClaimsGross',
                'Podra:structures:subSuisse:linesOfBusiness:subProperty:outClaimsNet',
                'Podra:structures:subSuisse:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertyAttritional:outClaimsCeded',
                'Podra:structures:subSuisse:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertyEarthquake:outClaimsCeded',
                'Podra:structures:subSuisse:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertySingle:outClaimsCeded',
                'Podra:structures:subSuisse:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyCxl:outClaimsCeded',
                'Podra:structures:subSuisse:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertyAttritional:outClaimsCeded',
                'Podra:structures:subSuisse:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertyEarthquake:outClaimsCeded',
                'Podra:structures:subSuisse:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertySingle:outClaimsCeded',
                'Podra:structures:subSuisse:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyQuotaShare:outClaimsCeded',
                'Podra:structures:subSuisse:outClaimsCeded',
                'Podra:structures:subSuisse:outClaimsGross',
                'Podra:structures:subSuisse:outClaimsNet',
                'Podra:structures:subSuisse:reinsuranceContracts:subMotorHullWxl:claimsGenerators:subMotorHullAttritional:outClaimsCeded',
                'Podra:structures:subSuisse:reinsuranceContracts:subMotorHullWxl:claimsGenerators:subMotorHullSingle:outClaimsCeded',
                'Podra:structures:subSuisse:reinsuranceContracts:subMotorHullWxl:outClaimsCeded',
                'Podra:structures:subSuisse:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertyAttritional:outClaimsCeded',
                'Podra:structures:subSuisse:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertyEarthquake:outClaimsCeded',
                'Podra:structures:subSuisse:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertySingle:outClaimsCeded',
                'Podra:structures:subSuisse:reinsuranceContracts:subPropertyCxl:outClaimsCeded',
                'Podra:structures:subSuisse:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subMotorHullAttritional:outClaimsCeded',
                'Podra:structures:subSuisse:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subMotorHullSingle:outClaimsCeded',
                'Podra:structures:subSuisse:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertyAttritional:outClaimsCeded',
                'Podra:structures:subSuisse:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertyEarthquake:outClaimsCeded',
                'Podra:structures:subSuisse:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertySingle:outClaimsCeded',
                'Podra:structures:subSuisse:reinsuranceContracts:subPropertyQuotaShare:outClaimsCeded',
                'Podra:structures:subsegmentFilter:outClaimsCeded',
                'Podra:structures:subsegmentFilter:outClaimsGross',
                'Podra:structures:subsegmentFilter:outClaimsNet',
                'Podra:linesOfBusiness:outUnderwritingInfoCeded',
                'Podra:linesOfBusiness:subMotorHull:outUnderwritingInfoCeded',
                'Podra:linesOfBusiness:subMotorHull:reinsuranceContracts:subMotorHullWxl:outUnderwritingInfoCeded',
                'Podra:linesOfBusiness:subMotorHull:reinsuranceContracts:subPropertyQuotaShare:outUnderwritingInfoCeded',
                'Podra:linesOfBusiness:subProperty:outUnderwritingInfoCeded',
                'Podra:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyCxl:outUnderwritingInfoCeded',
                'Podra:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyQuotaShare:outUnderwritingInfoCeded',
                'Podra:reinsurance:outCoverUnderwritingInfo',
                'Podra:reinsurance:subContracts:subMotorHullWxl:linesOfBusiness:subMotorHull:outCoverUnderwritingInfo',
                'Podra:reinsurance:subContracts:subMotorHullWxl:outCoverUnderwritingInfo',
                'Podra:reinsurance:subContracts:subPropertyCxl:linesOfBusiness:subProperty:outCoverUnderwritingInfo',
                'Podra:reinsurance:subContracts:subPropertyCxl:outCoverUnderwritingInfo',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:linesOfBusiness:subMotorHull:outCoverUnderwritingInfo',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:linesOfBusiness:subProperty:outCoverUnderwritingInfo',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:outCoverUnderwritingInfo',
                'Podra:reinsurance:subContracts:subriContract:outCoverUnderwritingInfo',
                'Podra:structures:subFrance:linesOfBusiness:subMotorHull:outUnderwritingInfoCeded',
                'Podra:structures:subFrance:linesOfBusiness:subMotorHull:outUnderwritingInfoGross',
                'Podra:structures:subFrance:linesOfBusiness:subMotorHull:outUnderwritingInfoNet',
                'Podra:structures:subFrance:linesOfBusiness:subMotorHull:reinsuranceContracts:subMotorHullWxl:outUnderwritingInfoCeded',
                'Podra:structures:subFrance:linesOfBusiness:subMotorHull:reinsuranceContracts:subPropertyQuotaShare:outUnderwritingInfoCeded',
                'Podra:structures:subFrance:linesOfBusiness:subProperty:outUnderwritingInfoCeded',
                'Podra:structures:subFrance:linesOfBusiness:subProperty:outUnderwritingInfoGross',
                'Podra:structures:subFrance:linesOfBusiness:subProperty:outUnderwritingInfoNet',
                'Podra:structures:subFrance:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyCxl:outUnderwritingInfoCeded',
                'Podra:structures:subFrance:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyQuotaShare:outUnderwritingInfoCeded',
                'Podra:structures:subFrance:outUnderwritingInfoCeded',
                'Podra:structures:subFrance:outUnderwritingInfoGross',
                'Podra:structures:subFrance:outUnderwritingInfoNet',
                'Podra:structures:subFrance:reinsuranceContracts:subMotorHullWxl:outUnderwritingInfoCeded',
                'Podra:structures:subFrance:reinsuranceContracts:subPropertyCxl:outUnderwritingInfoCeded',
                'Podra:structures:subFrance:reinsuranceContracts:subPropertyQuotaShare:outUnderwritingInfoCeded',
                'Podra:structures:subsegmentFilter:outUnderwritingInfoCeded',
                'Podra:structures:subsegmentFilter:outUnderwritingInfoGross',
                'Podra:structures:subsegmentFilter:outUnderwritingInfoNet',
                'Podra:structures:subSuisse:linesOfBusiness:subMotorHull:outUnderwritingInfoCeded',
                'Podra:structures:subSuisse:linesOfBusiness:subMotorHull:outUnderwritingInfoGross',
                'Podra:structures:subSuisse:linesOfBusiness:subMotorHull:outUnderwritingInfoNet',
                'Podra:structures:subSuisse:linesOfBusiness:subMotorHull:reinsuranceContracts:subMotorHullWxl:outUnderwritingInfoCeded',
                'Podra:structures:subSuisse:linesOfBusiness:subMotorHull:reinsuranceContracts:subPropertyQuotaShare:outUnderwritingInfoCeded',
                'Podra:structures:subSuisse:linesOfBusiness:subProperty:outUnderwritingInfoCeded',
                'Podra:structures:subSuisse:linesOfBusiness:subProperty:outUnderwritingInfoGross',
                'Podra:structures:subSuisse:linesOfBusiness:subProperty:outUnderwritingInfoNet',
                'Podra:structures:subSuisse:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyCxl:outUnderwritingInfoCeded',
                'Podra:structures:subSuisse:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyQuotaShare:outUnderwritingInfoCeded',
                'Podra:structures:subSuisse:outUnderwritingInfoCeded',
                'Podra:structures:subSuisse:outUnderwritingInfoGross',
                'Podra:structures:subSuisse:outUnderwritingInfoNet',
                'Podra:structures:subSuisse:reinsuranceContracts:subMotorHullWxl:outUnderwritingInfoCeded',
                'Podra:structures:subSuisse:reinsuranceContracts:subPropertyCxl:outUnderwritingInfoCeded',
                'Podra:structures:subSuisse:reinsuranceContracts:subPropertyQuotaShare:outUnderwritingInfoCeded',
        ]
        def collectedPaths = PathMapping.list()     //.sort{ it.pathName }
        // there are 6 paths of the dynamic containers itself, in the following for loop they are ignored.
        assertEquals '# of paths correct', paths.size(), collectedPaths.size() - 4

        for (int i = 0; i < collectedPaths.size(); i++) {
            if (collectedPaths[i].pathName.contains("sublineOfBusiness")) continue
            if (collectedPaths[i].pathName.contains("subRiContracts")) continue
//            def init = paths.contains(collectedPaths[i].pathName)
//            if (!paths.remove(collectedPaths[i].pathName)) {
//                println collectedPaths[i].pathName
//            }
            assertTrue "$i ${collectedPaths[i].pathName} found", paths.remove(collectedPaths[i].pathName)
        }

        assertTrue 'all paths found', paths.size() == 0
    }

    void correctFields(List<String> fields) {
        def collectedFields = FieldMapping.list()
        assertEquals '# of fields correct', fields.size(), collectedFields.size()

        for (FieldMapping field : collectedFields) {
            assertTrue "${field.fieldName}", fields.remove(field.fieldName)
        }
        assertTrue 'all field found', fields.size() == 0
    }

    void correctPaidClaimsResults() {
        Map<String, Double> resultsPerPath = new LinkedHashMap<String, Double>()
        resultsPerPath['Podra:linesOfBusiness:outClaimsDevelopmentLeanCeded']=790d
        resultsPerPath['Podra:linesOfBusiness:outClaimsDevelopmentLeanGross']=2700d
        resultsPerPath['Podra:linesOfBusiness:outClaimsDevelopmentLeanNet']=1910d
        resultsPerPath['Podra:linesOfBusiness:subMotorHull:outClaimsDevelopmentLeanCeded']=420d
        resultsPerPath['Podra:linesOfBusiness:subMotorHull:outClaimsDevelopmentLeanGross']=1100d
        resultsPerPath['Podra:linesOfBusiness:subMotorHull:outClaimsDevelopmentLeanNet']=680d
        resultsPerPath['Podra:linesOfBusiness:subMotorHull:claimsGenerators:subMotorHullAttritional:outClaimsDevelopmentLeanCeded']=20d
        resultsPerPath['Podra:linesOfBusiness:subMotorHull:claimsGenerators:subMotorHullAttritional:outClaimsDevelopmentLeanGross']=100d
        resultsPerPath['Podra:linesOfBusiness:subMotorHull:claimsGenerators:subMotorHullAttritional:outClaimsDevelopmentLeanNet']=80d
        resultsPerPath['Podra:linesOfBusiness:subMotorHull:claimsGenerators:subMotorHullSingle:outClaimsDevelopmentLeanCeded']=400d
        resultsPerPath['Podra:linesOfBusiness:subMotorHull:claimsGenerators:subMotorHullSingle:outClaimsDevelopmentLeanGross']=1000d
        resultsPerPath['Podra:linesOfBusiness:subMotorHull:claimsGenerators:subMotorHullSingle:outClaimsDevelopmentLeanNet']=600d
        resultsPerPath['Podra:linesOfBusiness:subMotorHull:reinsuranceContracts:subMotorHullWxl:outClaimsDevelopmentLeanCeded']=200d
        resultsPerPath['Podra:linesOfBusiness:subMotorHull:reinsuranceContracts:subPropertyQuotaShare:outClaimsDevelopmentLeanCeded']=220d
        resultsPerPath['Podra:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyQuotaShare:outClaimsDevelopmentLeanCeded']=320d
        resultsPerPath['Podra:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyCxl:outClaimsDevelopmentLeanCeded']=50d
        resultsPerPath['Podra:linesOfBusiness:subProperty:outClaimsDevelopmentLeanCeded']=370d
        resultsPerPath['Podra:linesOfBusiness:subProperty:outClaimsDevelopmentLeanGross']=1600d
        resultsPerPath['Podra:linesOfBusiness:subProperty:outClaimsDevelopmentLeanNet']=1230d
        resultsPerPath['Podra:linesOfBusiness:subProperty:claimsGenerators:subPropertyAttritional:outClaimsDevelopmentLeanCeded']=40d
        resultsPerPath['Podra:linesOfBusiness:subProperty:claimsGenerators:subPropertyAttritional:outClaimsDevelopmentLeanGross']=200d
        resultsPerPath['Podra:linesOfBusiness:subProperty:claimsGenerators:subPropertyAttritional:outClaimsDevelopmentLeanNet']=160d
        resultsPerPath['Podra:linesOfBusiness:subProperty:claimsGenerators:subPropertyEarthquake:outClaimsDevelopmentLeanCeded']=150d
        resultsPerPath['Podra:linesOfBusiness:subProperty:claimsGenerators:subPropertyEarthquake:outClaimsDevelopmentLeanGross']=500d
        resultsPerPath['Podra:linesOfBusiness:subProperty:claimsGenerators:subPropertyEarthquake:outClaimsDevelopmentLeanNet']=350d
        resultsPerPath['Podra:linesOfBusiness:subProperty:claimsGenerators:subPropertySingle:outClaimsDevelopmentLeanCeded']=180d
        resultsPerPath['Podra:linesOfBusiness:subProperty:claimsGenerators:subPropertySingle:outClaimsDevelopmentLeanGross']=900d
        resultsPerPath['Podra:linesOfBusiness:subProperty:claimsGenerators:subPropertySingle:outClaimsDevelopmentLeanNet']=720d
        resultsPerPath['Podra:reinsurance:outClaimsDevelopmentLeanCeded']=790d
        resultsPerPath['Podra:reinsurance:outClaimsDevelopmentLeanGross']=2700d
        resultsPerPath['Podra:reinsurance:outClaimsDevelopmentLeanNet']=1910d
        resultsPerPath['Podra:reinsurance:subContracts:subMotorHullWxl:outClaimsDevelopmentLeanCeded']=200d
        resultsPerPath['Podra:reinsurance:subContracts:subMotorHullWxl:outClaimsDevelopmentLeanGross']=880d
        resultsPerPath['Podra:reinsurance:subContracts:subMotorHullWxl:outClaimsDevelopmentLeanNet']=680d
        resultsPerPath['Podra:reinsurance:subContracts:subMotorHullWxl:linesOfBusiness:subMotorHull:outClaimsDevelopmentLeanGross']=880d
        resultsPerPath['Podra:reinsurance:subContracts:subMotorHullWxl:linesOfBusiness:subMotorHull:outClaimsDevelopmentLeanCeded']=200d
        resultsPerPath['Podra:reinsurance:subContracts:subMotorHullWxl:linesOfBusiness:subMotorHull:outClaimsDevelopmentLeanNet']=680d
        resultsPerPath['Podra:reinsurance:subContracts:subMotorHullWxl:claimsGenerators:subMotorHullAttritional:outClaimsDevelopmentLeanCeded']=0d
        resultsPerPath['Podra:reinsurance:subContracts:subMotorHullWxl:claimsGenerators:subMotorHullAttritional:outClaimsDevelopmentLeanGross']=80d
        resultsPerPath['Podra:reinsurance:subContracts:subMotorHullWxl:claimsGenerators:subMotorHullAttritional:outClaimsDevelopmentLeanNet']=80d
        resultsPerPath['Podra:reinsurance:subContracts:subMotorHullWxl:claimsGenerators:subMotorHullSingle:outClaimsDevelopmentLeanCeded']=200d
        resultsPerPath['Podra:reinsurance:subContracts:subMotorHullWxl:claimsGenerators:subMotorHullSingle:outClaimsDevelopmentLeanGross']=800d
        resultsPerPath['Podra:reinsurance:subContracts:subMotorHullWxl:claimsGenerators:subMotorHullSingle:outClaimsDevelopmentLeanNet']=600d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:outClaimsDevelopmentLeanCeded']=50d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:outClaimsDevelopmentLeanGross']=1280d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:outClaimsDevelopmentLeanNet']=1230d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:linesOfBusiness:subProperty:outClaimsDevelopmentLeanGross']=1280d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:linesOfBusiness:subProperty:outClaimsDevelopmentLeanCeded']=50d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:linesOfBusiness:subProperty:outClaimsDevelopmentLeanNet']=1230d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:claimsGenerators:subPropertyAttritional:outClaimsDevelopmentLeanCeded']=0d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:claimsGenerators:subPropertyAttritional:outClaimsDevelopmentLeanGross']=160d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:claimsGenerators:subPropertyAttritional:outClaimsDevelopmentLeanNet']=160d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:claimsGenerators:subPropertyEarthquake:outClaimsDevelopmentLeanCeded']=50d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:claimsGenerators:subPropertyEarthquake:outClaimsDevelopmentLeanGross']=400d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:claimsGenerators:subPropertyEarthquake:outClaimsDevelopmentLeanNet']=350d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:claimsGenerators:subPropertySingle:outClaimsDevelopmentLeanCeded']=0d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:claimsGenerators:subPropertySingle:outClaimsDevelopmentLeanGross']=720d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:claimsGenerators:subPropertySingle:outClaimsDevelopmentLeanNet']=720d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:outClaimsDevelopmentLeanCeded']=540d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:outClaimsDevelopmentLeanGross']=2700d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:outClaimsDevelopmentLeanNet']=2160d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:linesOfBusiness:subMotorHull:outClaimsDevelopmentLeanGross']=1100d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:linesOfBusiness:subMotorHull:outClaimsDevelopmentLeanCeded']=220d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:linesOfBusiness:subMotorHull:outClaimsDevelopmentLeanNet']=880d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:claimsGenerators:subMotorHullAttritional:outClaimsDevelopmentLeanGross']=100d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:claimsGenerators:subMotorHullAttritional:outClaimsDevelopmentLeanCeded']=20d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:claimsGenerators:subMotorHullAttritional:outClaimsDevelopmentLeanNet']=80d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:claimsGenerators:subMotorHullSingle:outClaimsDevelopmentLeanGross']=1000d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:claimsGenerators:subMotorHullSingle:outClaimsDevelopmentLeanCeded']=200d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:claimsGenerators:subMotorHullSingle:outClaimsDevelopmentLeanNet']=800d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:linesOfBusiness:subProperty:outClaimsDevelopmentLeanGross']=1600d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:linesOfBusiness:subProperty:outClaimsDevelopmentLeanCeded']=320d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:linesOfBusiness:subProperty:outClaimsDevelopmentLeanNet']=1280d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:claimsGenerators:subPropertyAttritional:outClaimsDevelopmentLeanCeded']=40d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:claimsGenerators:subPropertyAttritional:outClaimsDevelopmentLeanGross']=200d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:claimsGenerators:subPropertyAttritional:outClaimsDevelopmentLeanNet']=160d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:claimsGenerators:subPropertyEarthquake:outClaimsDevelopmentLeanCeded']=100d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:claimsGenerators:subPropertyEarthquake:outClaimsDevelopmentLeanGross']=500d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:claimsGenerators:subPropertyEarthquake:outClaimsDevelopmentLeanNet']=400d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:claimsGenerators:subPropertySingle:outClaimsDevelopmentLeanCeded']=180d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:claimsGenerators:subPropertySingle:outClaimsDevelopmentLeanGross']=900d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:claimsGenerators:subPropertySingle:outClaimsDevelopmentLeanNet']=720d

        resultsPerPath['Podra:structures:subFrance:claimsGenerators:subMotorHullAttritional:outClaimsCeded']=12d
        resultsPerPath['Podra:structures:subFrance:claimsGenerators:subMotorHullAttritional:outClaimsGross']=60d
        resultsPerPath['Podra:structures:subFrance:claimsGenerators:subMotorHullAttritional:outClaimsNet']=48d
        resultsPerPath['Podra:structures:subFrance:claimsGenerators:subMotorHullSingle:outClaimsCeded']=240d
        resultsPerPath['Podra:structures:subFrance:claimsGenerators:subMotorHullSingle:outClaimsGross']=600d
        resultsPerPath['Podra:structures:subFrance:claimsGenerators:subMotorHullSingle:outClaimsNet']=360d
        resultsPerPath['Podra:structures:subFrance:claimsGenerators:subPropertyAttritional:outClaimsCeded']=12d
        resultsPerPath['Podra:structures:subFrance:claimsGenerators:subPropertyAttritional:outClaimsGross']=60d
        resultsPerPath['Podra:structures:subFrance:claimsGenerators:subPropertyAttritional:outClaimsNet']=48d
        resultsPerPath['Podra:structures:subFrance:claimsGenerators:subPropertyEarthquake:outClaimsCeded']=45d
        resultsPerPath['Podra:structures:subFrance:claimsGenerators:subPropertyEarthquake:outClaimsGross']=150d
        resultsPerPath['Podra:structures:subFrance:claimsGenerators:subPropertyEarthquake:outClaimsNet']=105d
        resultsPerPath['Podra:structures:subFrance:claimsGenerators:subPropertySingle:outClaimsCeded']=54d
        resultsPerPath['Podra:structures:subFrance:claimsGenerators:subPropertySingle:outClaimsGross']=270d
        resultsPerPath['Podra:structures:subFrance:claimsGenerators:subPropertySingle:outClaimsNet']=216d
        resultsPerPath['Podra:structures:subFrance:linesOfBusiness:subMotorHull:claimsGenerators:subMotorHullAttritional:outClaimsCeded']=12d
        resultsPerPath['Podra:structures:subFrance:linesOfBusiness:subMotorHull:claimsGenerators:subMotorHullAttritional:outClaimsGross']=60d
        resultsPerPath['Podra:structures:subFrance:linesOfBusiness:subMotorHull:claimsGenerators:subMotorHullAttritional:outClaimsNet']=48d
        resultsPerPath['Podra:structures:subFrance:linesOfBusiness:subMotorHull:claimsGenerators:subMotorHullSingle:outClaimsCeded']=240d
        resultsPerPath['Podra:structures:subFrance:linesOfBusiness:subMotorHull:claimsGenerators:subMotorHullSingle:outClaimsGross']=600d
        resultsPerPath['Podra:structures:subFrance:linesOfBusiness:subMotorHull:claimsGenerators:subMotorHullSingle:outClaimsNet']=360d
        resultsPerPath['Podra:structures:subFrance:linesOfBusiness:subMotorHull:outClaimsCeded']=252d
        resultsPerPath['Podra:structures:subFrance:linesOfBusiness:subMotorHull:outClaimsGross']=660d
        resultsPerPath['Podra:structures:subFrance:linesOfBusiness:subMotorHull:outClaimsNet']=408d
        resultsPerPath['Podra:structures:subFrance:linesOfBusiness:subMotorHull:reinsuranceContracts:subMotorHullWxl:claimsGenerators:subMotorHullAttritional:outClaimsCeded']=0d
        resultsPerPath['Podra:structures:subFrance:linesOfBusiness:subMotorHull:reinsuranceContracts:subMotorHullWxl:claimsGenerators:subMotorHullSingle:outClaimsCeded']=120d
        resultsPerPath['Podra:structures:subFrance:linesOfBusiness:subMotorHull:reinsuranceContracts:subMotorHullWxl:outClaimsCeded']=120d
        resultsPerPath['Podra:structures:subFrance:linesOfBusiness:subMotorHull:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subMotorHullAttritional:outClaimsCeded']=12d
        resultsPerPath['Podra:structures:subFrance:linesOfBusiness:subMotorHull:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subMotorHullSingle:outClaimsCeded']=120d
        resultsPerPath['Podra:structures:subFrance:linesOfBusiness:subMotorHull:reinsuranceContracts:subPropertyQuotaShare:outClaimsCeded']=132d
        resultsPerPath['Podra:structures:subFrance:linesOfBusiness:subProperty:claimsGenerators:subPropertyAttritional:outClaimsCeded']=12d
        resultsPerPath['Podra:structures:subFrance:linesOfBusiness:subProperty:claimsGenerators:subPropertyAttritional:outClaimsGross']=60d
        resultsPerPath['Podra:structures:subFrance:linesOfBusiness:subProperty:claimsGenerators:subPropertyAttritional:outClaimsNet']=48d
        resultsPerPath['Podra:structures:subFrance:linesOfBusiness:subProperty:claimsGenerators:subPropertyEarthquake:outClaimsCeded']=45d
        resultsPerPath['Podra:structures:subFrance:linesOfBusiness:subProperty:claimsGenerators:subPropertyEarthquake:outClaimsGross']=150d
        resultsPerPath['Podra:structures:subFrance:linesOfBusiness:subProperty:claimsGenerators:subPropertyEarthquake:outClaimsNet']=105d
        resultsPerPath['Podra:structures:subFrance:linesOfBusiness:subProperty:claimsGenerators:subPropertySingle:outClaimsCeded']=54d
        resultsPerPath['Podra:structures:subFrance:linesOfBusiness:subProperty:claimsGenerators:subPropertySingle:outClaimsGross']=270d
        resultsPerPath['Podra:structures:subFrance:linesOfBusiness:subProperty:claimsGenerators:subPropertySingle:outClaimsNet']=216d
        resultsPerPath['Podra:structures:subFrance:linesOfBusiness:subProperty:outClaimsCeded']=111d
        resultsPerPath['Podra:structures:subFrance:linesOfBusiness:subProperty:outClaimsGross']=480d
        resultsPerPath['Podra:structures:subFrance:linesOfBusiness:subProperty:outClaimsNet']=369d
        resultsPerPath['Podra:structures:subFrance:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertyAttritional:outClaimsCeded']=0d
        resultsPerPath['Podra:structures:subFrance:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertyEarthquake:outClaimsCeded']=15d
        resultsPerPath['Podra:structures:subFrance:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertySingle:outClaimsCeded']=0d
        resultsPerPath['Podra:structures:subFrance:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyCxl:outClaimsCeded']=15d
        resultsPerPath['Podra:structures:subFrance:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertyAttritional:outClaimsCeded']=12d
        resultsPerPath['Podra:structures:subFrance:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertyEarthquake:outClaimsCeded']=30d
        resultsPerPath['Podra:structures:subFrance:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertySingle:outClaimsCeded']=54d
        resultsPerPath['Podra:structures:subFrance:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyQuotaShare:outClaimsCeded']=96d
        resultsPerPath['Podra:structures:subFrance:outClaimsCeded']=363d
        resultsPerPath['Podra:structures:subFrance:outClaimsGross']=1140d
        resultsPerPath['Podra:structures:subFrance:outClaimsNet']=777d
        resultsPerPath['Podra:structures:subFrance:reinsuranceContracts:subMotorHullWxl:claimsGenerators:subMotorHullAttritional:outClaimsCeded']=0d
        resultsPerPath['Podra:structures:subFrance:reinsuranceContracts:subMotorHullWxl:claimsGenerators:subMotorHullSingle:outClaimsCeded']=120d
        resultsPerPath['Podra:structures:subFrance:reinsuranceContracts:subMotorHullWxl:outClaimsCeded']=120d
        resultsPerPath['Podra:structures:subFrance:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertyAttritional:outClaimsCeded']=0d
        resultsPerPath['Podra:structures:subFrance:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertyEarthquake:outClaimsCeded']=15d
        resultsPerPath['Podra:structures:subFrance:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertySingle:outClaimsCeded']=0d
        resultsPerPath['Podra:structures:subFrance:reinsuranceContracts:subPropertyCxl:outClaimsCeded']=15d
        resultsPerPath['Podra:structures:subFrance:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subMotorHullAttritional:outClaimsCeded']=12d
        resultsPerPath['Podra:structures:subFrance:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subMotorHullSingle:outClaimsCeded']=120d
        resultsPerPath['Podra:structures:subFrance:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertyAttritional:outClaimsCeded']=12d
        resultsPerPath['Podra:structures:subFrance:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertyEarthquake:outClaimsCeded']=30d
        resultsPerPath['Podra:structures:subFrance:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertySingle:outClaimsCeded']=54d
        resultsPerPath['Podra:structures:subFrance:reinsuranceContracts:subPropertyQuotaShare:outClaimsCeded']=228d

        resultsPerPath['Podra:structures:subSuisse:claimsGenerators:subMotorHullAttritional:outClaimsCeded']=8d
        resultsPerPath['Podra:structures:subSuisse:claimsGenerators:subMotorHullAttritional:outClaimsGross']=40d
        resultsPerPath['Podra:structures:subSuisse:claimsGenerators:subMotorHullAttritional:outClaimsNet']=32d
        resultsPerPath['Podra:structures:subSuisse:claimsGenerators:subMotorHullSingle:outClaimsCeded']=160d
        resultsPerPath['Podra:structures:subSuisse:claimsGenerators:subMotorHullSingle:outClaimsGross']=400d
        resultsPerPath['Podra:structures:subSuisse:claimsGenerators:subMotorHullSingle:outClaimsNet']=240d
        resultsPerPath['Podra:structures:subSuisse:claimsGenerators:subPropertyAttritional:outClaimsCeded']=28d
        resultsPerPath['Podra:structures:subSuisse:claimsGenerators:subPropertyAttritional:outClaimsGross']=140d
        resultsPerPath['Podra:structures:subSuisse:claimsGenerators:subPropertyAttritional:outClaimsNet']=112d
        resultsPerPath['Podra:structures:subSuisse:claimsGenerators:subPropertyEarthquake:outClaimsCeded']=105d
        resultsPerPath['Podra:structures:subSuisse:claimsGenerators:subPropertyEarthquake:outClaimsGross']=350d
        resultsPerPath['Podra:structures:subSuisse:claimsGenerators:subPropertyEarthquake:outClaimsNet']=245d
        resultsPerPath['Podra:structures:subSuisse:claimsGenerators:subPropertySingle:outClaimsCeded']=126d
        resultsPerPath['Podra:structures:subSuisse:claimsGenerators:subPropertySingle:outClaimsGross']=630d
        resultsPerPath['Podra:structures:subSuisse:claimsGenerators:subPropertySingle:outClaimsNet']=504d
        resultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subMotorHull:claimsGenerators:subMotorHullAttritional:outClaimsCeded']=8d
        resultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subMotorHull:claimsGenerators:subMotorHullAttritional:outClaimsGross']=40d
        resultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subMotorHull:claimsGenerators:subMotorHullAttritional:outClaimsNet']=32d
        resultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subMotorHull:claimsGenerators:subMotorHullSingle:outClaimsCeded']=160d
        resultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subMotorHull:claimsGenerators:subMotorHullSingle:outClaimsGross']=400d
        resultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subMotorHull:claimsGenerators:subMotorHullSingle:outClaimsNet']=240d
        resultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subMotorHull:outClaimsCeded']=168d
        resultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subMotorHull:outClaimsGross']=440d
        resultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subMotorHull:outClaimsNet']=272d
        resultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subMotorHull:reinsuranceContracts:subMotorHullWxl:claimsGenerators:subMotorHullAttritional:outClaimsCeded']=0d
        resultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subMotorHull:reinsuranceContracts:subMotorHullWxl:claimsGenerators:subMotorHullSingle:outClaimsCeded']=80d
        resultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subMotorHull:reinsuranceContracts:subMotorHullWxl:outClaimsCeded']=80d
        resultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subMotorHull:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subMotorHullAttritional:outClaimsCeded']=8d
        resultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subMotorHull:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subMotorHullSingle:outClaimsCeded']=80d
        resultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subMotorHull:reinsuranceContracts:subPropertyQuotaShare:outClaimsCeded']=88d
        resultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subProperty:claimsGenerators:subPropertyAttritional:outClaimsCeded']=28d
        resultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subProperty:claimsGenerators:subPropertyAttritional:outClaimsGross']=140d
        resultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subProperty:claimsGenerators:subPropertyAttritional:outClaimsNet']=112d
        resultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subProperty:claimsGenerators:subPropertyEarthquake:outClaimsCeded']=105d
        resultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subProperty:claimsGenerators:subPropertyEarthquake:outClaimsGross']=350d
        resultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subProperty:claimsGenerators:subPropertyEarthquake:outClaimsNet']=245d
        resultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subProperty:claimsGenerators:subPropertySingle:outClaimsCeded']=126d
        resultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subProperty:claimsGenerators:subPropertySingle:outClaimsGross']=630d
        resultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subProperty:claimsGenerators:subPropertySingle:outClaimsNet']=504d
        resultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subProperty:outClaimsCeded']=259d
        resultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subProperty:outClaimsGross']=1120d
        resultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subProperty:outClaimsNet']=861d
        resultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertyAttritional:outClaimsCeded']=0d
        resultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertyEarthquake:outClaimsCeded']=35d
        resultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertySingle:outClaimsCeded']=0d
        resultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyCxl:outClaimsCeded']=35d
        resultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertyAttritional:outClaimsCeded']=28d
        resultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertyEarthquake:outClaimsCeded']=70d
        resultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertySingle:outClaimsCeded']=126d
        resultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyQuotaShare:outClaimsCeded']=224d
        resultsPerPath['Podra:structures:subSuisse:outClaimsCeded']=427d
        resultsPerPath['Podra:structures:subSuisse:outClaimsGross']=1560d
        resultsPerPath['Podra:structures:subSuisse:outClaimsNet']=1133d
        resultsPerPath['Podra:structures:subSuisse:reinsuranceContracts:subMotorHullWxl:claimsGenerators:subMotorHullAttritional:outClaimsCeded']=0d
        resultsPerPath['Podra:structures:subSuisse:reinsuranceContracts:subMotorHullWxl:claimsGenerators:subMotorHullSingle:outClaimsCeded']=80d
        resultsPerPath['Podra:structures:subSuisse:reinsuranceContracts:subMotorHullWxl:outClaimsCeded']=80d
        resultsPerPath['Podra:structures:subSuisse:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertyAttritional:outClaimsCeded']=0d
        resultsPerPath['Podra:structures:subSuisse:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertyEarthquake:outClaimsCeded']=35d
        resultsPerPath['Podra:structures:subSuisse:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertySingle:outClaimsCeded']=0d
        resultsPerPath['Podra:structures:subSuisse:reinsuranceContracts:subPropertyCxl:outClaimsCeded']=35d
        resultsPerPath['Podra:structures:subSuisse:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subMotorHullAttritional:outClaimsCeded']=8d
        resultsPerPath['Podra:structures:subSuisse:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subMotorHullSingle:outClaimsCeded']=80d
        resultsPerPath['Podra:structures:subSuisse:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertyAttritional:outClaimsCeded']=28d
        resultsPerPath['Podra:structures:subSuisse:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertyEarthquake:outClaimsCeded']=70d
        resultsPerPath['Podra:structures:subSuisse:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertySingle:outClaimsCeded']=126d
        resultsPerPath['Podra:structures:subSuisse:reinsuranceContracts:subPropertyQuotaShare:outClaimsCeded']=312d

        Map<String, Double> collectedResultsPerPath = new HashMap<String, Double>()
        def results = SingleValueResult.list()
        for (SingleValueResult result : results) {
            if (result.field.fieldName == "incurred") {
                collectedResultsPerPath[result.path.pathName] = result.value
            }
        }

        for (Map.Entry<String, Double> result : resultsPerPath.entrySet()) {
//            re-enable for debugging
//            if (result.value != collectedResultsPerPath.get(result.key) && collectedResultsPerPath.get(result.key) != null) {
//                println "$result.key paid claims ${result.value} ${collectedResultsPerPath.get(result.key)}"
//            }
            assertEquals "$result.key paid claims", result.value, collectedResultsPerPath.get(result.key), 1E-8
        }
    }

    void correctCommissionsResults() {
        Map<String, Double> expectedResultsPerPath = new LinkedHashMap<String, Double>()

        expectedResultsPerPath['Podra:linesOfBusiness:subMotorHull:outUnderwritingInfoCeded']=-160d
        expectedResultsPerPath['Podra:linesOfBusiness:subProperty:outUnderwritingInfoCeded']=-140d
        expectedResultsPerPath['Podra:linesOfBusiness:subMotorHull:reinsuranceContracts:subPropertyQuotaShare:outUnderwritingInfoCeded']=-160d
        expectedResultsPerPath['Podra:linesOfBusiness:subMotorHull:reinsuranceContracts:subMotorHullWxl:outUnderwritingInfoCeded']=0d
        expectedResultsPerPath['Podra:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyCxl:outUnderwritingInfoCeded']=-20d
        expectedResultsPerPath['Podra:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyQuotaShare:outUnderwritingInfoCeded']=-120d
        expectedResultsPerPath['Podra:reinsurance:subContracts:subMotorHullWxl:outCoverUnderwritingInfo']=-0d
        expectedResultsPerPath['Podra:reinsurance:subContracts:subMotorHullWxl:linesOfBusiness:subMotorHull:outCoverUnderwritingInfo']=-0d
        expectedResultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:outCoverUnderwritingInfo']=-20d
        expectedResultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:linesOfBusiness:subProperty:outCoverUnderwritingInfo']=-20d
        expectedResultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:outCoverUnderwritingInfo']=-280d
        expectedResultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:linesOfBusiness:subMotorHull:outCoverUnderwritingInfo']=-160
        expectedResultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:linesOfBusiness:subProperty:outCoverUnderwritingInfo']=-120

        Map<String, Double> collectedResultsPerPath = new HashMap<String, Double>()
        def results = SingleValueResult.list()
        for (SingleValueResult result : results) {
            if (result.field.fieldName == "commission") {
                collectedResultsPerPath[result.path.pathName] = result.value
            }
        }

        for (Map.Entry<String, Double> result : expectedResultsPerPath.entrySet()) {
//            re-enable for debugging
//            if (result.value != collectedResultsPerPath.get(result.key) && collectedResultsPerPath.get(result.key) != null) {
//                println "$result.key commission ${result.value} ${collectedResultsPerPath.get(result.key)}"
//            }
            assertEquals "$result.key commission", result.value, collectedResultsPerPath.get(result.key), 1E-14
        }
    }

    void correctPremiumResults() {
        Map<String, Double> expectedResultsPerPath = new LinkedHashMap<String, Double>()
        expectedResultsPerPath['Podra:linesOfBusiness:subMotorHull:outUnderwritingInfoCeded']=1800d
        expectedResultsPerPath['Podra:linesOfBusiness:subProperty:outUnderwritingInfoCeded']=1300d
        expectedResultsPerPath['Podra:linesOfBusiness:subMotorHull:reinsuranceContracts:subPropertyQuotaShare:outUnderwritingInfoCeded']=1600d
        expectedResultsPerPath['Podra:linesOfBusiness:subMotorHull:reinsuranceContracts:subMotorHullWxl:outUnderwritingInfoCeded']=200d
        expectedResultsPerPath['Podra:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyCxl:outUnderwritingInfoCeded']=100d
        expectedResultsPerPath['Podra:linesOfBusiness:subProperty:reinsuranceContracts:subPropertyQuotaShare:outUnderwritingInfoCeded']=1200d
        expectedResultsPerPath['Podra:reinsurance:subContracts:subMotorHullWxl:outCoverUnderwritingInfo']=200d
        expectedResultsPerPath['Podra:reinsurance:subContracts:subMotorHullWxl:linesOfBusiness:subMotorHull:outCoverUnderwritingInfo']=200d
        expectedResultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:outCoverUnderwritingInfo']=100d
        expectedResultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:linesOfBusiness:subProperty:outCoverUnderwritingInfo']=100d
        expectedResultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:outCoverUnderwritingInfo']=2800d
        expectedResultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:linesOfBusiness:subMotorHull:outCoverUnderwritingInfo']=1600
        expectedResultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:linesOfBusiness:subProperty:outCoverUnderwritingInfo']=1200

        expectedResultsPerPath['Podra:structures:subFrance:reinsuranceContracts:subPropertyQuotaShare:outUnderwritingInfoCeded']=1320d
        expectedResultsPerPath['Podra:structures:subFrance:reinsuranceContracts:subPropertyCxl:outUnderwritingInfoCeded']=30d
        expectedResultsPerPath['Podra:structures:subFrance:linesOfBusiness:subMotorHull:outUnderwritingInfoCeded']=1080d
        expectedResultsPerPath['Podra:structures:subFrance:linesOfBusiness:subProperty:outUnderwritingInfoCeded']=390d
        expectedResultsPerPath['Podra:structures:subFrance:reinsuranceContracts:subMotorHullWxl:outUnderwritingInfoCeded']=120d
        expectedResultsPerPath['Podra:structures:subFrance:outUnderwritingInfoCeded']=1470d
        expectedResultsPerPath['Podra:structures:subFrance:linesOfBusiness:subMotorHull:outUnderwritingInfoGross']=4800d
        expectedResultsPerPath['Podra:structures:subFrance:outUnderwritingInfoGross']=6600d
        expectedResultsPerPath['Podra:structures:subFrance:linesOfBusiness:subProperty:outUnderwritingInfoGross']=1800d
        expectedResultsPerPath['Podra:structures:subFrance:outUnderwritingInfoNet']=5130d
        expectedResultsPerPath['Podra:structures:subFrance:linesOfBusiness:subProperty:outUnderwritingInfoNet']=1410d
        expectedResultsPerPath['Podra:structures:subFrance:linesOfBusiness:subMotorHull:outUnderwritingInfoNet']=3720d
        expectedResultsPerPath['Podra:structures:subSuisse:outUnderwritingInfoCeded']=1630d
        expectedResultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subProperty:outUnderwritingInfoCeded']=910d
        expectedResultsPerPath['Podra:structures:subSuisse:reinsuranceContracts:subPropertyCxl:outUnderwritingInfoCeded']=70d
        expectedResultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subMotorHull:outUnderwritingInfoCeded']=720d
        expectedResultsPerPath['Podra:structures:subSuisse:reinsuranceContracts:subMotorHullWxl:outUnderwritingInfoCeded']=80d
        expectedResultsPerPath['Podra:structures:subSuisse:reinsuranceContracts:subPropertyQuotaShare:outUnderwritingInfoCeded']=1480d
        expectedResultsPerPath['Podra:structures:subSuisse:outUnderwritingInfoGross']=7400d
        expectedResultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subProperty:outUnderwritingInfoGross']=4200d
        expectedResultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subMotorHull:outUnderwritingInfoGross']=3200d
        expectedResultsPerPath['Podra:structures:subSuisse:outUnderwritingInfoNet']=5770d
        expectedResultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subProperty:outUnderwritingInfoNet']=3290d
        expectedResultsPerPath['Podra:structures:subSuisse:linesOfBusiness:subMotorHull:outUnderwritingInfoNet']=2480d

        Map<String, Double> collectedResultsPerPath = new HashMap<String, Double>()
        def results = SingleValueResult.list()
        for (SingleValueResult result : results) {
            if (result.field.fieldName == "premium") {
                collectedResultsPerPath[result.path.pathName] = result.value
            }
        }

        for (Map.Entry<String, Double> result : expectedResultsPerPath.entrySet()) {
//            re-enable for debugging
//            if (result.value != collectedResultsPerPath.get(result.key) && collectedResultsPerPath.get(result.key) != null) {
//                println "$result.key premium ${result.value} ${collectedResultsPerPath.get(result.key)}"
//            }
            assertEquals "$result.key premium", result.value, collectedResultsPerPath.get(result.key)
        }
    }
}
