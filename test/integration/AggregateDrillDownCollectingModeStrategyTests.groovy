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
        correctFields(['incurred', 'paid', 'reserved','commission', 'premium'])
        correctPaidClaimsResults()
        correctCommissionsResults()
        correctPremiumResults()
    }

    void correctPaths() {
        List<String> paths = [
                'Podra:linesOfBusiness:outClaimsDevelopmentLeanCeded',
                'Podra:linesOfBusiness:outClaimsDevelopmentLeanGross',
                'Podra:linesOfBusiness:outClaimsDevelopmentLeanNet',
                'Podra:linesOfBusiness:subMotorHull:outClaimsDevelopmentLeanCeded',
                'Podra:linesOfBusiness:subMotorHull:outClaimsDevelopmentLeanGross',
                'Podra:linesOfBusiness:subMotorHull:outClaimsDevelopmentLeanNet',
                'Podra:linesOfBusiness:subMotorHull:outUnderwritingInfoCeded',
                'Podra:linesOfBusiness:subMotorHull:subMotorHullAttritional:outClaimsDevelopmentLeanCeded',
                'Podra:linesOfBusiness:subMotorHull:subMotorHullAttritional:outClaimsDevelopmentLeanGross',
                'Podra:linesOfBusiness:subMotorHull:subMotorHullAttritional:outClaimsDevelopmentLeanNet',
                'Podra:linesOfBusiness:subMotorHull:subMotorHullSingle:outClaimsDevelopmentLeanCeded',
                'Podra:linesOfBusiness:subMotorHull:subMotorHullSingle:outClaimsDevelopmentLeanGross',
                'Podra:linesOfBusiness:subMotorHull:subMotorHullSingle:outClaimsDevelopmentLeanNet',
                'Podra:linesOfBusiness:subMotorHull:subMotorHullWxl:outClaimsDevelopmentLeanCeded',
                'Podra:linesOfBusiness:subMotorHull:subPropertyQuotaShare:outClaimsDevelopmentLeanCeded',
                'Podra:linesOfBusiness:subMotorHull:subPropertyQuotaShare:outUnderwritingInfoCeded',
                'Podra:linesOfBusiness:subMotorHull:subMotorHullWxl:outUnderwritingInfoCeded',

                'Podra:linesOfBusiness:subProperty:subPropertyQuotaShare:outClaimsDevelopmentLeanCeded',
                'Podra:linesOfBusiness:subProperty:subPropertyCxl:outClaimsDevelopmentLeanCeded',
                'Podra:linesOfBusiness:subProperty:subPropertyCxl:outUnderwritingInfoCeded',
                'Podra:linesOfBusiness:subProperty:subPropertyQuotaShare:outUnderwritingInfoCeded',
                'Podra:linesOfBusiness:subProperty:outClaimsDevelopmentLeanCeded',
                'Podra:linesOfBusiness:subProperty:outClaimsDevelopmentLeanGross',
                'Podra:linesOfBusiness:subProperty:outClaimsDevelopmentLeanNet',
                'Podra:linesOfBusiness:subProperty:subPropertyAttritional:outClaimsDevelopmentLeanCeded',
                'Podra:linesOfBusiness:subProperty:subPropertyAttritional:outClaimsDevelopmentLeanGross',
                'Podra:linesOfBusiness:subProperty:subPropertyAttritional:outClaimsDevelopmentLeanNet',
                'Podra:linesOfBusiness:subProperty:subPropertyEarthquake:outClaimsDevelopmentLeanCeded',
                'Podra:linesOfBusiness:subProperty:subPropertyEarthquake:outClaimsDevelopmentLeanGross',
                'Podra:linesOfBusiness:subProperty:subPropertyEarthquake:outClaimsDevelopmentLeanNet',
                'Podra:linesOfBusiness:subProperty:subPropertySingle:outClaimsDevelopmentLeanCeded',
                'Podra:linesOfBusiness:subProperty:subPropertySingle:outClaimsDevelopmentLeanGross',
                'Podra:linesOfBusiness:subProperty:subPropertySingle:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subMotorHullWxl:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subContracts:subMotorHullWxl:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subContracts:subMotorHullWxl:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subMotorHullWxl:subMotorHull:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subContracts:subMotorHullWxl:subMotorHull:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subContracts:subMotorHullWxl:subMotorHull:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subMotorHullWxl:subMotorHullAttritional:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subContracts:subMotorHullWxl:subMotorHullAttritional:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subContracts:subMotorHullWxl:subMotorHullAttritional:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subMotorHullWxl:subMotorHullSingle:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subContracts:subMotorHullWxl:subMotorHullSingle:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subContracts:subMotorHullWxl:subMotorHullSingle:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subPropertyCxl:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subContracts:subPropertyCxl:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subContracts:subPropertyCxl:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subPropertyCxl:subProperty:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subContracts:subPropertyCxl:subProperty:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subContracts:subPropertyCxl:subProperty:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subPropertyCxl:subPropertyAttritional:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subContracts:subPropertyCxl:subPropertyAttritional:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subContracts:subPropertyCxl:subPropertyAttritional:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subPropertyCxl:subPropertyEarthquake:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subContracts:subPropertyCxl:subPropertyEarthquake:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subContracts:subPropertyCxl:subPropertyEarthquake:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subPropertyCxl:subPropertySingle:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subContracts:subPropertyCxl:subPropertySingle:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subContracts:subPropertyCxl:subPropertySingle:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:subProperty:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:subProperty:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:subProperty:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:subPropertyAttritional:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:subPropertyAttritional:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:subPropertyAttritional:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:subPropertyEarthquake:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:subPropertyEarthquake:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:subPropertyEarthquake:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:subPropertySingle:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:subPropertySingle:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:subPropertySingle:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:subMotorHull:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:subMotorHullAttritional:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:subMotorHullAttritional:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:subMotorHullAttritional:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:subMotorHullSingle:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:subMotorHullSingle:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:subMotorHullSingle:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:subMotorHull:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:subMotorHull:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subContracts:subMotorHullWxl:outCoverUnderwritingInfo',
                'Podra:reinsurance:subContracts:subMotorHullWxl:subMotorHull:outCoverUnderwritingInfo',
                'Podra:reinsurance:subContracts:subPropertyCxl:outCoverUnderwritingInfo',
                'Podra:reinsurance:subContracts:subPropertyCxl:subProperty:outCoverUnderwritingInfo',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:outCoverUnderwritingInfo',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:subProperty:outCoverUnderwritingInfo',
                'Podra:reinsurance:subContracts:subPropertyQuotaShare:subMotorHull:outCoverUnderwritingInfo',
                'Podra:reinsurance:subContracts:subriContract:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subContracts:subriContract:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subContracts:subriContract:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subContracts:subriContract:outCoverUnderwritingInfo',
                'Podra:reinsurance:outCoverUnderwritingInfo',

                'Podra:linesOfBusiness:subProperty:outUnderwritingInfoCeded',
                'Podra:linesOfBusiness:outUnderwritingInfoCeded',
                'Podra:structures:subsegmentFilter:outClaimsCeded',
                'Podra:structures:subsegmentFilter:outClaimsGross',
                'Podra:structures:subsegmentFilter:outClaimsNet',
                'Podra:structures:subsegmentFilter:outUnderwritingInfoCeded',
                'Podra:structures:subsegmentFilter:outUnderwritingInfoGross',
                'Podra:structures:subsegmentFilter:outUnderwritingInfoNet',
                'Podra:structures:subFrance:outClaimsCeded',
                'Podra:structures:subFrance:subPropertyQuotaShare:outClaimsCeded',
                'Podra:structures:subFrance:subMotorHull:outClaimsCeded',
                'Podra:structures:subFrance:subMotorHullAttritional:outClaimsCeded',
                'Podra:structures:subFrance:subMotorHullSingle:outClaimsCeded',
                'Podra:structures:subFrance:subMotorHullWxl:outClaimsCeded',
                'Podra:structures:subFrance:subProperty:outClaimsCeded',
                'Podra:structures:subFrance:subPropertySingle:outClaimsCeded',
                'Podra:structures:subFrance:subPropertyEarthquake:outClaimsCeded',
                'Podra:structures:subFrance:subPropertyAttritional:outClaimsCeded',
                'Podra:structures:subFrance:subPropertyCxl:outClaimsCeded',
                'Podra:structures:subFrance:outClaimsGross',
                'Podra:structures:subFrance:subMotorHull:outClaimsGross',
                'Podra:structures:subFrance:subMotorHullAttritional:outClaimsGross',
                'Podra:structures:subFrance:subMotorHullSingle:outClaimsGross',
                'Podra:structures:subFrance:subProperty:outClaimsGross',
                'Podra:structures:subFrance:subPropertySingle:outClaimsGross',
                'Podra:structures:subFrance:subPropertyEarthquake:outClaimsGross',
                'Podra:structures:subFrance:subPropertyAttritional:outClaimsGross',
                'Podra:structures:subFrance:outClaimsNet',
                'Podra:structures:subFrance:subMotorHull:outClaimsNet',
                'Podra:structures:subFrance:subMotorHullAttritional:outClaimsNet',
                'Podra:structures:subFrance:subMotorHullSingle:outClaimsNet',
                'Podra:structures:subFrance:subProperty:outClaimsNet',
                'Podra:structures:subFrance:subPropertySingle:outClaimsNet',
                'Podra:structures:subFrance:subPropertyEarthquake:outClaimsNet',
                'Podra:structures:subFrance:subPropertyAttritional:outClaimsNet',
                'Podra:structures:subFrance:subPropertyQuotaShare:outUnderwritingInfoCeded',
                'Podra:structures:subFrance:subPropertyCxl:outUnderwritingInfoCeded',
                'Podra:structures:subFrance:subMotorHull:outUnderwritingInfoCeded',
                'Podra:structures:subFrance:subProperty:outUnderwritingInfoCeded',
                'Podra:structures:subFrance:subMotorHullWxl:outUnderwritingInfoCeded',
                'Podra:structures:subFrance:outUnderwritingInfoCeded',
                'Podra:structures:subFrance:subMotorHull:outUnderwritingInfoGross',
                'Podra:structures:subFrance:outUnderwritingInfoGross',
                'Podra:structures:subFrance:subProperty:outUnderwritingInfoGross',
                'Podra:structures:subFrance:outUnderwritingInfoNet',
                'Podra:structures:subFrance:subProperty:outUnderwritingInfoNet',
                'Podra:structures:subFrance:subMotorHull:outUnderwritingInfoNet',
                'Podra:structures:subSuisse:outClaimsCeded',
                'Podra:structures:subSuisse:subPropertyQuotaShare:outClaimsCeded',
                'Podra:structures:subSuisse:subMotorHull:outClaimsCeded',
                'Podra:structures:subSuisse:subMotorHullAttritional:outClaimsCeded',
                'Podra:structures:subSuisse:subMotorHullSingle:outClaimsCeded',
                'Podra:structures:subSuisse:subMotorHullWxl:outClaimsCeded',
                'Podra:structures:subSuisse:subProperty:outClaimsCeded',
                'Podra:structures:subSuisse:subPropertySingle:outClaimsCeded',
                'Podra:structures:subSuisse:subPropertyEarthquake:outClaimsCeded',
                'Podra:structures:subSuisse:subPropertyAttritional:outClaimsCeded',
                'Podra:structures:subSuisse:subPropertyCxl:outClaimsCeded',
                'Podra:structures:subSuisse:outClaimsGross',
                'Podra:structures:subSuisse:subMotorHull:outClaimsGross',
                'Podra:structures:subSuisse:subMotorHullAttritional:outClaimsGross',
                'Podra:structures:subSuisse:subMotorHullSingle:outClaimsGross',
                'Podra:structures:subSuisse:subProperty:outClaimsGross',
                'Podra:structures:subSuisse:subPropertySingle:outClaimsGross',
                'Podra:structures:subSuisse:subPropertyEarthquake:outClaimsGross',
                'Podra:structures:subSuisse:subPropertyAttritional:outClaimsGross',
                'Podra:structures:subSuisse:outClaimsNet',
                'Podra:structures:subSuisse:subMotorHull:outClaimsNet',
                'Podra:structures:subSuisse:subMotorHullAttritional:outClaimsNet',
                'Podra:structures:subSuisse:subMotorHullSingle:outClaimsNet',
                'Podra:structures:subSuisse:subProperty:outClaimsNet',
                'Podra:structures:subSuisse:subPropertySingle:outClaimsNet',
                'Podra:structures:subSuisse:subPropertyEarthquake:outClaimsNet',
                'Podra:structures:subSuisse:subPropertyAttritional:outClaimsNet',
                'Podra:structures:subSuisse:outUnderwritingInfoCeded',
                'Podra:structures:subSuisse:subProperty:outUnderwritingInfoCeded',
                'Podra:structures:subSuisse:subPropertyCxl:outUnderwritingInfoCeded',
                'Podra:structures:subSuisse:subMotorHull:outUnderwritingInfoCeded',
                'Podra:structures:subSuisse:subMotorHullWxl:outUnderwritingInfoCeded',
                'Podra:structures:subSuisse:subPropertyQuotaShare:outUnderwritingInfoCeded',
                'Podra:structures:subSuisse:outUnderwritingInfoGross',
                'Podra:structures:subSuisse:subProperty:outUnderwritingInfoGross',
                'Podra:structures:subSuisse:subMotorHull:outUnderwritingInfoGross',
                'Podra:structures:subSuisse:outUnderwritingInfoNet',
                'Podra:structures:subSuisse:subProperty:outUnderwritingInfoNet',
                'Podra:structures:subSuisse:subMotorHull:outUnderwritingInfoNet'
        ]
        def collectedPaths = PathMapping.list()
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
        resultsPerPath['Podra:linesOfBusiness:subMotorHull:subMotorHullAttritional:outClaimsDevelopmentLeanCeded']=20d
        resultsPerPath['Podra:linesOfBusiness:subMotorHull:subMotorHullAttritional:outClaimsDevelopmentLeanGross']=100d
        resultsPerPath['Podra:linesOfBusiness:subMotorHull:subMotorHullAttritional:outClaimsDevelopmentLeanNet']=80d
        resultsPerPath['Podra:linesOfBusiness:subMotorHull:subMotorHullSingle:outClaimsDevelopmentLeanCeded']=400d
        resultsPerPath['Podra:linesOfBusiness:subMotorHull:subMotorHullSingle:outClaimsDevelopmentLeanGross']=1000d
        resultsPerPath['Podra:linesOfBusiness:subMotorHull:subMotorHullSingle:outClaimsDevelopmentLeanNet']=600d
        resultsPerPath['Podra:linesOfBusiness:subMotorHull:subMotorHullWxl:outClaimsDevelopmentLeanCeded']=200d
        resultsPerPath['Podra:linesOfBusiness:subMotorHull:subPropertyQuotaShare:outClaimsDevelopmentLeanCeded']=220d
        resultsPerPath['Podra:linesOfBusiness:subProperty:subPropertyQuotaShare:outClaimsDevelopmentLeanCeded']=320d
        resultsPerPath['Podra:linesOfBusiness:subProperty:subPropertyCxl:outClaimsDevelopmentLeanCeded']=50d
        resultsPerPath['Podra:linesOfBusiness:subProperty:outClaimsDevelopmentLeanCeded']=370d
        resultsPerPath['Podra:linesOfBusiness:subProperty:outClaimsDevelopmentLeanGross']=1600d
        resultsPerPath['Podra:linesOfBusiness:subProperty:outClaimsDevelopmentLeanNet']=1230d
        resultsPerPath['Podra:linesOfBusiness:subProperty:subPropertyAttritional:outClaimsDevelopmentLeanCeded']=40d
        resultsPerPath['Podra:linesOfBusiness:subProperty:subPropertyAttritional:outClaimsDevelopmentLeanGross']=200d
        resultsPerPath['Podra:linesOfBusiness:subProperty:subPropertyAttritional:outClaimsDevelopmentLeanNet']=160d
        resultsPerPath['Podra:linesOfBusiness:subProperty:subPropertyEarthquake:outClaimsDevelopmentLeanCeded']=150d
        resultsPerPath['Podra:linesOfBusiness:subProperty:subPropertyEarthquake:outClaimsDevelopmentLeanGross']=500d
        resultsPerPath['Podra:linesOfBusiness:subProperty:subPropertyEarthquake:outClaimsDevelopmentLeanNet']=350d
        resultsPerPath['Podra:linesOfBusiness:subProperty:subPropertySingle:outClaimsDevelopmentLeanCeded']=180d
        resultsPerPath['Podra:linesOfBusiness:subProperty:subPropertySingle:outClaimsDevelopmentLeanGross']=900d
        resultsPerPath['Podra:linesOfBusiness:subProperty:subPropertySingle:outClaimsDevelopmentLeanNet']=720d
        resultsPerPath['Podra:reinsurance:outClaimsDevelopmentLeanCeded']=790d
        resultsPerPath['Podra:reinsurance:outClaimsDevelopmentLeanGross']=2700d
        resultsPerPath['Podra:reinsurance:outClaimsDevelopmentLeanNet']=1910d
        resultsPerPath['Podra:reinsurance:subContracts:subMotorHullWxl:outClaimsDevelopmentLeanCeded']=200d
        resultsPerPath['Podra:reinsurance:subContracts:subMotorHullWxl:outClaimsDevelopmentLeanGross']=880d
        resultsPerPath['Podra:reinsurance:subContracts:subMotorHullWxl:outClaimsDevelopmentLeanNet']=680d
        resultsPerPath['Podra:reinsurance:subContracts:subMotorHullWxl:subMotorHull:outClaimsDevelopmentLeanGross']=880d
        resultsPerPath['Podra:reinsurance:subContracts:subMotorHullWxl:subMotorHull:outClaimsDevelopmentLeanCeded']=200d
        resultsPerPath['Podra:reinsurance:subContracts:subMotorHullWxl:subMotorHull:outClaimsDevelopmentLeanNet']=680d
        resultsPerPath['Podra:reinsurance:subContracts:subMotorHullWxl:subMotorHullAttritional:outClaimsDevelopmentLeanCeded']=0d
        resultsPerPath['Podra:reinsurance:subContracts:subMotorHullWxl:subMotorHullAttritional:outClaimsDevelopmentLeanGross']=80d
        resultsPerPath['Podra:reinsurance:subContracts:subMotorHullWxl:subMotorHullAttritional:outClaimsDevelopmentLeanNet']=80d
        resultsPerPath['Podra:reinsurance:subContracts:subMotorHullWxl:subMotorHullSingle:outClaimsDevelopmentLeanCeded']=200d
        resultsPerPath['Podra:reinsurance:subContracts:subMotorHullWxl:subMotorHullSingle:outClaimsDevelopmentLeanGross']=800d
        resultsPerPath['Podra:reinsurance:subContracts:subMotorHullWxl:subMotorHullSingle:outClaimsDevelopmentLeanNet']=600d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:outClaimsDevelopmentLeanCeded']=50d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:outClaimsDevelopmentLeanGross']=1280d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:outClaimsDevelopmentLeanNet']=1230d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:subProperty:outClaimsDevelopmentLeanGross']=1280d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:subProperty:outClaimsDevelopmentLeanCeded']=50d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:subProperty:outClaimsDevelopmentLeanNet']=1230d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:subPropertyAttritional:outClaimsDevelopmentLeanCeded']=0d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:subPropertyAttritional:outClaimsDevelopmentLeanGross']=160d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:subPropertyAttritional:outClaimsDevelopmentLeanNet']=160d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:subPropertyEarthquake:outClaimsDevelopmentLeanCeded']=50d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:subPropertyEarthquake:outClaimsDevelopmentLeanGross']=400d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:subPropertyEarthquake:outClaimsDevelopmentLeanNet']=350d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:subPropertySingle:outClaimsDevelopmentLeanCeded']=0d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:subPropertySingle:outClaimsDevelopmentLeanGross']=720d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:subPropertySingle:outClaimsDevelopmentLeanNet']=720d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:outClaimsDevelopmentLeanCeded']=540d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:outClaimsDevelopmentLeanGross']=2700d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:outClaimsDevelopmentLeanNet']=2160d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:subMotorHull:outClaimsDevelopmentLeanGross']=1100d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:subMotorHull:outClaimsDevelopmentLeanCeded']=220d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:subMotorHull:outClaimsDevelopmentLeanNet']=880d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:subMotorHullAttritional:outClaimsDevelopmentLeanGross']=100d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:subMotorHullAttritional:outClaimsDevelopmentLeanCeded']=20d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:subMotorHullAttritional:outClaimsDevelopmentLeanNet']=80d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:subMotorHullSingle:outClaimsDevelopmentLeanGross']=1000d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:subMotorHullSingle:outClaimsDevelopmentLeanCeded']=200d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:subMotorHullSingle:outClaimsDevelopmentLeanNet']=800d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:subProperty:outClaimsDevelopmentLeanGross']=1600d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:subProperty:outClaimsDevelopmentLeanCeded']=320d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:subProperty:outClaimsDevelopmentLeanNet']=1280d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:subPropertyAttritional:outClaimsDevelopmentLeanCeded']=40d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:subPropertyAttritional:outClaimsDevelopmentLeanGross']=200d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:subPropertyAttritional:outClaimsDevelopmentLeanNet']=160d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:subPropertyEarthquake:outClaimsDevelopmentLeanCeded']=100d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:subPropertyEarthquake:outClaimsDevelopmentLeanGross']=500d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:subPropertyEarthquake:outClaimsDevelopmentLeanNet']=400d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:subPropertySingle:outClaimsDevelopmentLeanCeded']=180d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:subPropertySingle:outClaimsDevelopmentLeanGross']=900d
        resultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:subPropertySingle:outClaimsDevelopmentLeanNet']=720d

        resultsPerPath['Podra:structures:subFrance:outClaimsCeded']=363d
        resultsPerPath['Podra:structures:subFrance:subPropertyQuotaShare:outClaimsCeded']=228d
        resultsPerPath['Podra:structures:subFrance:subMotorHull:outClaimsCeded']=252d
        resultsPerPath['Podra:structures:subFrance:subMotorHullAttritional:outClaimsCeded']=12d
        resultsPerPath['Podra:structures:subFrance:subMotorHullSingle:outClaimsCeded']=240d
        resultsPerPath['Podra:structures:subFrance:subMotorHullWxl:outClaimsCeded']=120d
        resultsPerPath['Podra:structures:subFrance:subProperty:outClaimsCeded']=111d
        resultsPerPath['Podra:structures:subFrance:subPropertySingle:outClaimsCeded']=54d
        resultsPerPath['Podra:structures:subFrance:subPropertyEarthquake:outClaimsCeded']=45d
        resultsPerPath['Podra:structures:subFrance:subPropertyAttritional:outClaimsCeded']=12d
        resultsPerPath['Podra:structures:subFrance:subPropertyCxl:outClaimsCeded']=15d
        resultsPerPath['Podra:structures:subFrance:outClaimsGross']=1140d
        resultsPerPath['Podra:structures:subFrance:subMotorHull:outClaimsGross']=660d
        resultsPerPath['Podra:structures:subFrance:subMotorHullAttritional:outClaimsGross']=60d
        resultsPerPath['Podra:structures:subFrance:subMotorHullSingle:outClaimsGross']=600d
        resultsPerPath['Podra:structures:subFrance:subProperty:outClaimsGross']=480d
        resultsPerPath['Podra:structures:subFrance:subPropertySingle:outClaimsGross']=270d
        resultsPerPath['Podra:structures:subFrance:subPropertyEarthquake:outClaimsGross']=150d
        resultsPerPath['Podra:structures:subFrance:subPropertyAttritional:outClaimsGross']=60d
        resultsPerPath['Podra:structures:subFrance:outClaimsNet']=777d
        resultsPerPath['Podra:structures:subFrance:subMotorHull:outClaimsNet']=408d
        resultsPerPath['Podra:structures:subFrance:subMotorHullAttritional:outClaimsNet']=48d
        resultsPerPath['Podra:structures:subFrance:subMotorHullSingle:outClaimsNet']=360d
        resultsPerPath['Podra:structures:subFrance:subProperty:outClaimsNet']=369d
        resultsPerPath['Podra:structures:subFrance:subPropertySingle:outClaimsNet']=216d
        resultsPerPath['Podra:structures:subFrance:subPropertyEarthquake:outClaimsNet']=105d
        resultsPerPath['Podra:structures:subFrance:subPropertyAttritional:outClaimsNet']=48d
        resultsPerPath['Podra:structures:subSuisse:outClaimsCeded']=427d
        resultsPerPath['Podra:structures:subSuisse:subPropertyQuotaShare:outClaimsCeded']=312d
        resultsPerPath['Podra:structures:subSuisse:subMotorHull:outClaimsCeded']=168d
        resultsPerPath['Podra:structures:subSuisse:subMotorHullAttritional:outClaimsCeded']=8d
        resultsPerPath['Podra:structures:subSuisse:subMotorHullSingle:outClaimsCeded']=160d
        resultsPerPath['Podra:structures:subSuisse:subMotorHullWxl:outClaimsCeded']=80d
        resultsPerPath['Podra:structures:subSuisse:subProperty:outClaimsCeded']=259d
        resultsPerPath['Podra:structures:subSuisse:subPropertySingle:outClaimsCeded']=126d
        resultsPerPath['Podra:structures:subSuisse:subPropertyEarthquake:outClaimsCeded']=105d
        resultsPerPath['Podra:structures:subSuisse:subPropertyAttritional:outClaimsCeded']=28d
        resultsPerPath['Podra:structures:subSuisse:subPropertyCxl:outClaimsCeded']=35d
        resultsPerPath['Podra:structures:subSuisse:outClaimsGross']=1560d
        resultsPerPath['Podra:structures:subSuisse:subMotorHull:outClaimsGross']=440d
        resultsPerPath['Podra:structures:subSuisse:subMotorHullAttritional:outClaimsGross']=40d
        resultsPerPath['Podra:structures:subSuisse:subMotorHullSingle:outClaimsGross']=400d
        resultsPerPath['Podra:structures:subSuisse:subProperty:outClaimsGross']=1120d
        resultsPerPath['Podra:structures:subSuisse:subPropertySingle:outClaimsGross']=630d
        resultsPerPath['Podra:structures:subSuisse:subPropertyEarthquake:outClaimsGross']=350d
        resultsPerPath['Podra:structures:subSuisse:subPropertyAttritional:outClaimsGross']=140d
        resultsPerPath['Podra:structures:subSuisse:outClaimsNet']=1133d
        resultsPerPath['Podra:structures:subSuisse:subMotorHull:outClaimsNet']=272d
        resultsPerPath['Podra:structures:subSuisse:subMotorHullAttritional:outClaimsNet']=32d
        resultsPerPath['Podra:structures:subSuisse:subMotorHullSingle:outClaimsNet']=240d
        resultsPerPath['Podra:structures:subSuisse:subProperty:outClaimsNet']=861d
        resultsPerPath['Podra:structures:subSuisse:subPropertySingle:outClaimsNet']=504d
        resultsPerPath['Podra:structures:subSuisse:subPropertyEarthquake:outClaimsNet']=245d
        resultsPerPath['Podra:structures:subSuisse:subPropertyAttritional:outClaimsNet']=112d

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
        expectedResultsPerPath['Podra:linesOfBusiness:subMotorHull:subPropertyQuotaShare:outUnderwritingInfoCeded']=-160d
        expectedResultsPerPath['Podra:linesOfBusiness:subMotorHull:subMotorHullWxl:outUnderwritingInfoCeded']=0d
        expectedResultsPerPath['Podra:linesOfBusiness:subProperty:subPropertyCxl:outUnderwritingInfoCeded']=-20d
        expectedResultsPerPath['Podra:linesOfBusiness:subProperty:subPropertyQuotaShare:outUnderwritingInfoCeded']=-120d
        expectedResultsPerPath['Podra:reinsurance:subContracts:subMotorHullWxl:outCoverUnderwritingInfo']=-0d
        expectedResultsPerPath['Podra:reinsurance:subContracts:subMotorHullWxl:subMotorHull:outCoverUnderwritingInfo']=-0d
        expectedResultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:outCoverUnderwritingInfo']=-20d
        expectedResultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:subProperty:outCoverUnderwritingInfo']=-20d
        expectedResultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:outCoverUnderwritingInfo']=-280d
        expectedResultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:subMotorHull:outCoverUnderwritingInfo']=-160
        expectedResultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:subProperty:outCoverUnderwritingInfo']=-120

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
            assertEquals "$result.key commission", result.value, collectedResultsPerPath.get(result.key)
        }
    }

    void correctPremiumResults() {
        Map<String, Double> expectedResultsPerPath = new LinkedHashMap<String, Double>()
        expectedResultsPerPath['Podra:linesOfBusiness:subMotorHull:outUnderwritingInfoCeded']=1800d
        expectedResultsPerPath['Podra:linesOfBusiness:subProperty:outUnderwritingInfoCeded']=1300d
        expectedResultsPerPath['Podra:linesOfBusiness:subMotorHull:subPropertyQuotaShare:outUnderwritingInfoCeded']=1600d
        expectedResultsPerPath['Podra:linesOfBusiness:subMotorHull:subMotorHullWxl:outUnderwritingInfoCeded']=200d
        expectedResultsPerPath['Podra:linesOfBusiness:subProperty:subPropertyCxl:outUnderwritingInfoCeded']=100d
        expectedResultsPerPath['Podra:linesOfBusiness:subProperty:subPropertyQuotaShare:outUnderwritingInfoCeded']=1200d
        expectedResultsPerPath['Podra:reinsurance:subContracts:subMotorHullWxl:outCoverUnderwritingInfo']=200d
        expectedResultsPerPath['Podra:reinsurance:subContracts:subMotorHullWxl:subMotorHull:outCoverUnderwritingInfo']=200d
        expectedResultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:outCoverUnderwritingInfo']=100d
        expectedResultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:subProperty:outCoverUnderwritingInfo']=100d
        expectedResultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:outCoverUnderwritingInfo']=2800d
        expectedResultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:subMotorHull:outCoverUnderwritingInfo']=1600
        expectedResultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:subProperty:outCoverUnderwritingInfo']=1200

        expectedResultsPerPath['Podra:structures:subFrance:subPropertyQuotaShare:outUnderwritingInfoCeded']=1320d
        expectedResultsPerPath['Podra:structures:subFrance:subPropertyCxl:outUnderwritingInfoCeded']=30d
        expectedResultsPerPath['Podra:structures:subFrance:subMotorHull:outUnderwritingInfoCeded']=1080d
        expectedResultsPerPath['Podra:structures:subFrance:subProperty:outUnderwritingInfoCeded']=390d
        expectedResultsPerPath['Podra:structures:subFrance:subMotorHullWxl:outUnderwritingInfoCeded']=120d
        expectedResultsPerPath['Podra:structures:subFrance:outUnderwritingInfoCeded']=1470d
        expectedResultsPerPath['Podra:structures:subFrance:subMotorHull:outUnderwritingInfoGross']=4800d
        expectedResultsPerPath['Podra:structures:subFrance:outUnderwritingInfoGross']=6600d
        expectedResultsPerPath['Podra:structures:subFrance:subProperty:outUnderwritingInfoGross']=1800d
        expectedResultsPerPath['Podra:structures:subFrance:outUnderwritingInfoNet']=5130d
        expectedResultsPerPath['Podra:structures:subFrance:subProperty:outUnderwritingInfoNet']=1410d
        expectedResultsPerPath['Podra:structures:subFrance:subMotorHull:outUnderwritingInfoNet']=3720d
        expectedResultsPerPath['Podra:structures:subSuisse:outUnderwritingInfoCeded']=1630d
        expectedResultsPerPath['Podra:structures:subSuisse:subProperty:outUnderwritingInfoCeded']=910d
        expectedResultsPerPath['Podra:structures:subSuisse:subPropertyCxl:outUnderwritingInfoCeded']=70d
        expectedResultsPerPath['Podra:structures:subSuisse:subMotorHull:outUnderwritingInfoCeded']=720d
        expectedResultsPerPath['Podra:structures:subSuisse:subMotorHullWxl:outUnderwritingInfoCeded']=80d
        expectedResultsPerPath['Podra:structures:subSuisse:subPropertyQuotaShare:outUnderwritingInfoCeded']=1480d
        expectedResultsPerPath['Podra:structures:subSuisse:outUnderwritingInfoGross']=7400d
        expectedResultsPerPath['Podra:structures:subSuisse:subProperty:outUnderwritingInfoGross']=4200d
        expectedResultsPerPath['Podra:structures:subSuisse:subMotorHull:outUnderwritingInfoGross']=3200d
        expectedResultsPerPath['Podra:structures:subSuisse:outUnderwritingInfoNet']=5770d
        expectedResultsPerPath['Podra:structures:subSuisse:subProperty:outUnderwritingInfoNet']=3290d
        expectedResultsPerPath['Podra:structures:subSuisse:subMotorHull:outUnderwritingInfoNet']=2480d


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
