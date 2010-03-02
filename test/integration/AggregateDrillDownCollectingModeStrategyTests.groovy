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
//        correctPaths()
        correctFields()
        correctResults()
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
                'Podra:linesOfBusiness:subMotorHull:subMotorHullWxl:outClaimsDevelopmentLeanCeded',
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
                'Podra:reinsurance:subMotorHullWxl:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subMotorHullWxl:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subMotorHullWxl:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subMotorHullWxl:subMotorHull:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subMotorHullWxl:subMotorHull:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subMotorHullWxl:subMotorHull:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subMotorHullWxl:subMotorHullAttritional:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subMotorHullWxl:subMotorHullAttritional:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subMotorHullWxl:subMotorHullAttritional:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subMotorHullWxl:subMotorHullSingle:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subMotorHullWxl:subMotorHullSingle:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subMotorHullWxl:subMotorHullSingle:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subPropertyCxl:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subPropertyCxl:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subPropertyCxl:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subPropertyCxl:subProperty:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subPropertyCxl:subProperty:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subPropertyCxl:subProperty:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subPropertyCxl:subPropertyAttritional:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subPropertyCxl:subPropertyAttritional:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subPropertyCxl:subPropertyAttritional:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subPropertyCxl:subPropertyEarthquake:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subPropertyCxl:subPropertyEarthquake:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subPropertyCxl:subPropertyEarthquake:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subPropertyCxl:subPropertySingle:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subPropertyCxl:subPropertySingle:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subPropertyCxl:subPropertySingle:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subPropertyQuotaShare:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subPropertyQuotaShare:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subPropertyQuotaShare:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subPropertyQuotaShare:subProperty:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subPropertyQuotaShare:subProperty:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subPropertyQuotaShare:subProperty:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subPropertyQuotaShare:subPropertyAttritional:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subPropertyQuotaShare:subPropertyAttritional:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subPropertyQuotaShare:subPropertyAttritional:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subPropertyQuotaShare:subPropertyEarthquake:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subPropertyQuotaShare:subPropertyEarthquake:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subPropertyQuotaShare:subPropertyEarthquake:outClaimsDevelopmentLeanNet',
                'Podra:reinsurance:subPropertyQuotaShare:subPropertySingle:outClaimsDevelopmentLeanCeded',
                'Podra:reinsurance:subPropertyQuotaShare:subPropertySingle:outClaimsDevelopmentLeanGross',
                'Podra:reinsurance:subPropertyQuotaShare:subPropertySingle:outClaimsDevelopmentLeanNet'
        ]
        def collectedPaths = PathMapping.list()
        // there are 6 paths of the dynamic containers itself, in the following for loop they are ignored.
        assertEquals '# of paths correct', paths.size(), collectedPaths.size() - 6

        for (int i = 0; i < collectedPaths.size(); i++) {
            if (collectedPaths[i].pathName.contains("sublineOfBusiness")) continue
            if (collectedPaths[i].pathName.contains("subcontracts")) continue
            assertTrue "$i ${collectedPaths[i].pathName} found", paths.remove(collectedPaths[i].pathName)
        }

        assertTrue 'all paths found', paths.size() == 0
    }

    void correctFields() {
        List<String> fields = ['incurred', 'paid', 'reserved']
        def collectedFields = FieldMapping.list()
        assertEquals '# of fields correct', fields.size(), collectedFields.size()

        for (FieldMapping field : collectedFields) {
            assertTrue "${field.fieldName}", fields.remove(field.fieldName)
        }
        assertTrue 'all field found', fields.size() == 0
    }

    void correctResults() {
        Map<String, Double> resultsPerPath = new LinkedHashMap<String, Double>()
        resultsPerPath['Podra:linesOfBusiness:outClaimsDevelopmentLeanCeded']=570d
        resultsPerPath['Podra:linesOfBusiness:outClaimsDevelopmentLeanGross']=2700d
        resultsPerPath['Podra:linesOfBusiness:outClaimsDevelopmentLeanNet']=2130d
        resultsPerPath['Podra:linesOfBusiness:subMotorHull:outClaimsDevelopmentLeanCeded']=200d
        resultsPerPath['Podra:linesOfBusiness:subMotorHull:outClaimsDevelopmentLeanGross']=1100d
        resultsPerPath['Podra:linesOfBusiness:subMotorHull:outClaimsDevelopmentLeanNet']=900d
        resultsPerPath['Podra:linesOfBusiness:subMotorHull:subMotorHullAttritional:outClaimsDevelopmentLeanCeded']=0d
        resultsPerPath['Podra:linesOfBusiness:subMotorHull:subMotorHullAttritional:outClaimsDevelopmentLeanGross']=100d
        resultsPerPath['Podra:linesOfBusiness:subMotorHull:subMotorHullAttritional:outClaimsDevelopmentLeanNet']=100d
        resultsPerPath['Podra:linesOfBusiness:subMotorHull:subMotorHullSingle:outClaimsDevelopmentLeanCeded']=200d
        resultsPerPath['Podra:linesOfBusiness:subMotorHull:subMotorHullSingle:outClaimsDevelopmentLeanGross']=1000d
        resultsPerPath['Podra:linesOfBusiness:subMotorHull:subMotorHullSingle:outClaimsDevelopmentLeanNet']=800d
        resultsPerPath['Podra:linesOfBusiness:subMotorHull:subMotorHullWxl:outClaimsDevelopmentLeanCeded']=200d
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
        resultsPerPath['Podra:reinsurance:outClaimsDevelopmentLeanCeded']=570d
        resultsPerPath['Podra:reinsurance:outClaimsDevelopmentLeanGross']=2700d
        resultsPerPath['Podra:reinsurance:outClaimsDevelopmentLeanNet']=2130d
        resultsPerPath['Podra:reinsurance:subMotorHullWxl:outClaimsDevelopmentLeanCeded']=200d
        resultsPerPath['Podra:reinsurance:subMotorHullWxl:outClaimsDevelopmentLeanGross']=1100d
        resultsPerPath['Podra:reinsurance:subMotorHullWxl:outClaimsDevelopmentLeanNet']=900d
        resultsPerPath['Podra:reinsurance:subMotorHullWxl:subMotorHull:outClaimsDevelopmentLeanGross']=1100d
        resultsPerPath['Podra:reinsurance:subMotorHullWxl:subMotorHull:outClaimsDevelopmentLeanCeded']=200d
        resultsPerPath['Podra:reinsurance:subMotorHullWxl:subMotorHull:outClaimsDevelopmentLeanNet']=900d
        resultsPerPath['Podra:reinsurance:subMotorHullWxl:subMotorHullAttritional:outClaimsDevelopmentLeanCeded']=0d
        resultsPerPath['Podra:reinsurance:subMotorHullWxl:subMotorHullAttritional:outClaimsDevelopmentLeanGross']=100d
        resultsPerPath['Podra:reinsurance:subMotorHullWxl:subMotorHullAttritional:outClaimsDevelopmentLeanNet']=100d
        resultsPerPath['Podra:reinsurance:subMotorHullWxl:subMotorHullSingle:outClaimsDevelopmentLeanCeded']=200d
        resultsPerPath['Podra:reinsurance:subMotorHullWxl:subMotorHullSingle:outClaimsDevelopmentLeanGross']=1000d
        resultsPerPath['Podra:reinsurance:subMotorHullWxl:subMotorHullSingle:outClaimsDevelopmentLeanNet']=800d
        resultsPerPath['Podra:reinsurance:subPropertyCxl:outClaimsDevelopmentLeanCeded']=50d
        resultsPerPath['Podra:reinsurance:subPropertyCxl:outClaimsDevelopmentLeanGross']=1280d
        resultsPerPath['Podra:reinsurance:subPropertyCxl:outClaimsDevelopmentLeanNet']=1230d
        resultsPerPath['Podra:reinsurance:subPropertyCxl:subProperty:outClaimsDevelopmentLeanGross']=1280d
        resultsPerPath['Podra:reinsurance:subPropertyCxl:subProperty:outClaimsDevelopmentLeanCeded']=50d
        resultsPerPath['Podra:reinsurance:subPropertyCxl:subProperty:outClaimsDevelopmentLeanNet']=1230d
        resultsPerPath['Podra:reinsurance:subPropertyCxl:subPropertyAttritional:outClaimsDevelopmentLeanCeded']=0d
        resultsPerPath['Podra:reinsurance:subPropertyCxl:subPropertyAttritional:outClaimsDevelopmentLeanGross']=160d
        resultsPerPath['Podra:reinsurance:subPropertyCxl:subPropertyAttritional:outClaimsDevelopmentLeanNet']=160d
        resultsPerPath['Podra:reinsurance:subPropertyCxl:subPropertyEarthquake:outClaimsDevelopmentLeanCeded']=50d
        resultsPerPath['Podra:reinsurance:subPropertyCxl:subPropertyEarthquake:outClaimsDevelopmentLeanGross']=400d
        resultsPerPath['Podra:reinsurance:subPropertyCxl:subPropertyEarthquake:outClaimsDevelopmentLeanNet']=350d
        resultsPerPath['Podra:reinsurance:subPropertyCxl:subPropertySingle:outClaimsDevelopmentLeanCeded']=0d
        resultsPerPath['Podra:reinsurance:subPropertyCxl:subPropertySingle:outClaimsDevelopmentLeanGross']=720d
        resultsPerPath['Podra:reinsurance:subPropertyCxl:subPropertySingle:outClaimsDevelopmentLeanNet']=720d
        resultsPerPath['Podra:reinsurance:subPropertyQuotaShare:outClaimsDevelopmentLeanCeded']=320d
        resultsPerPath['Podra:reinsurance:subPropertyQuotaShare:outClaimsDevelopmentLeanGross']=1600d
        resultsPerPath['Podra:reinsurance:subPropertyQuotaShare:outClaimsDevelopmentLeanNet']=1280d
        resultsPerPath['Podra:reinsurance:subPropertyQuotaShare:subProperty:outClaimsDevelopmentLeanGross']=1600d
        resultsPerPath['Podra:reinsurance:subPropertyQuotaShare:subProperty:outClaimsDevelopmentLeanCeded']=320d
        resultsPerPath['Podra:reinsurance:subPropertyQuotaShare:subProperty:outClaimsDevelopmentLeanNet']=1280d
        resultsPerPath['Podra:reinsurance:subPropertyQuotaShare:subPropertyAttritional:outClaimsDevelopmentLeanCeded']=40d
        resultsPerPath['Podra:reinsurance:subPropertyQuotaShare:subPropertyAttritional:outClaimsDevelopmentLeanGross']=200d
        resultsPerPath['Podra:reinsurance:subPropertyQuotaShare:subPropertyAttritional:outClaimsDevelopmentLeanNet']=160d
        resultsPerPath['Podra:reinsurance:subPropertyQuotaShare:subPropertyEarthquake:outClaimsDevelopmentLeanCeded']=100d
        resultsPerPath['Podra:reinsurance:subPropertyQuotaShare:subPropertyEarthquake:outClaimsDevelopmentLeanGross']=500d
        resultsPerPath['Podra:reinsurance:subPropertyQuotaShare:subPropertyEarthquake:outClaimsDevelopmentLeanNet']=400d
        resultsPerPath['Podra:reinsurance:subPropertyQuotaShare:subPropertySingle:outClaimsDevelopmentLeanCeded']=180d
        resultsPerPath['Podra:reinsurance:subPropertyQuotaShare:subPropertySingle:outClaimsDevelopmentLeanGross']=900d
        resultsPerPath['Podra:reinsurance:subPropertyQuotaShare:subPropertySingle:outClaimsDevelopmentLeanNet']=720d

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
//                println "$result.key ${result.value} ${collectedResultsPerPath.get(result.key)}"
//            }
            assertEquals "$result.key", resultsPerPath.get(result.key), result.value
        }
    }
}