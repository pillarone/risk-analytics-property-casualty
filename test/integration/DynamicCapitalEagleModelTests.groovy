import models.dynamicCapitalEagle.DynamicCapitalEagleModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

class DynamicCapitalEagleModelTests extends ModelTest {

    Class getModelClass() {
        DynamicCapitalEagleModel
    }

    String getParameterDisplayName() {
        return "No Reinsurance"
    }

    String getResultConfigurationDisplayName() {
        return "Reinsurance Contracts Details"
    }

    protected boolean shouldCompareResults() {
        false
    }
}