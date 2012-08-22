import models.dynamicCapitalEagle.DynamicCapitalEagleModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest
import org.apache.commons.logging.LogFactory

class DynamicCapitalEagleModelTests extends ModelTest {

    @Override
    protected void setUp() {
        LogFactory.getLog(getClass()).error("Currently running ${getClass().name}")
        super.setUp()
    }

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