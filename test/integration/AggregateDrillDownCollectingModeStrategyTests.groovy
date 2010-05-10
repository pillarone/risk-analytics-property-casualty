import models.podra.PodraModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest
import org.pillarone.riskanalytics.core.output.PathMapping
import org.pillarone.riskanalytics.core.output.FieldMapping
import org.pillarone.riskanalytics.core.output.SingleValueResult
import org.pillarone.riskanalytics.core.output.DBOutput
import org.pillarone.riskanalytics.core.output.ICollectorOutputStrategy

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
                'Podra:linesOfBusiness:subMotorHull:subMotorHullAttritional:outClaimsDevelopmentLeanCeded',
                'Podra:linesOfBusiness:subMotorHull:subMotorHullAttritional:outClaimsDevelopmentLeanGross',
                'Podra:linesOfBusiness:subMotorHull:subMotorHullAttritional:outClaimsDevelopmentLeanNet',
                'Podra:linesOfBusiness:subMotorHull:subMotorHullSingle:outClaimsDevelopmentLeanCeded',
                'Podra:linesOfBusiness:subMotorHull:subMotorHullSingle:outClaimsDevelopmentLeanGross',
                'Podra:linesOfBusiness:subMotorHull:subMotorHullSingle:outClaimsDevelopmentLeanNet',
//                'Podra:linesOfBusiness:subMotorHull:subMotorHullWxl:outClaimsDevelopmentLeanCeded',
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
                'Podra:linesOfBusiness:subMotorHull:outUnderwritingInfoCeded',
                'Podra:linesOfBusiness:subProperty:outUnderwritingInfoCeded',
                'Podra:linesOfBusiness:outUnderwritingInfoCeded',
        ]
        def collectedPaths = PathMapping.list()
        // there are 6 paths of the dynamic containers itself, in the following for loop they are ignored.
        assertEquals '# of paths correct', paths.size(), collectedPaths.size() - 4

        for (int i = 0; i < collectedPaths.size(); i++) {
            if (collectedPaths[i].pathName.contains("sublineOfBusiness")) continue
            if (collectedPaths[i].pathName.contains("subRiContracts")) continue
            def init = paths.contains(collectedPaths[i].pathName)
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
//        resultsPerPath['Podra:linesOfBusiness:subMotorHull:subMotorHullWxl:outClaimsDevelopmentLeanCeded']=200d   missing
//        resultsPerPath['Podra:linesOfBusiness:subMotorHull:subPropertyQuotaShare:outClaimsDevelopmentLeanCeded']=220d     missing
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
        expectedResultsPerPath['Podra:reinsurance:subContracts:subMotorHullWxl:outCoverUnderwritingInfo']=200d
        expectedResultsPerPath['Podra:reinsurance:subContracts:subMotorHullWxl:subMotorHull:outCoverUnderwritingInfo']=200d
        expectedResultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:outCoverUnderwritingInfo']=100d
        expectedResultsPerPath['Podra:reinsurance:subContracts:subPropertyCxl:subProperty:outCoverUnderwritingInfo']=100d
        expectedResultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:outCoverUnderwritingInfo']=2800d
        expectedResultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:subMotorHull:outCoverUnderwritingInfo']=1600
        expectedResultsPerPath['Podra:reinsurance:subContracts:subPropertyQuotaShare:subProperty:outCoverUnderwritingInfo']=1200

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
