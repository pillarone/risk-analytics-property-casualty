import models.capitalEagle.CapitalEagleModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest
import org.apache.commons.logging.LogFactory

class CapitalEagleModelTests extends ModelTest {

    @Override
    protected void setUp() {
        LogFactory.getLog(getClass()).error("Currently running ${getClass().name}")
        super.setUp()
    }

    Class getModelClass() {
        CapitalEagleModel
    }

    String getParameterDisplayName() {
        'One Reinsurance Program'
    }

    String getResultConfigurationDisplayName() {
        'newCollectors'
    }

    protected boolean shouldCompareResults() {
        true
    }
}