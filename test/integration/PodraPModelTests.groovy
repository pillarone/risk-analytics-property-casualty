import org.pillarone.riskanalytics.core.simulation.engine.ModelTest
import models.podraP.PodraPModel
import org.apache.commons.logging.LogFactory

class PodraPModelTests extends ModelTest {

    @Override
    protected void setUp() {
        LogFactory.getLog(getClass()).error("Currently running ${getClass().name}")
        super.setUp()
    }

    Class getModelClass() {
        PodraPModel
    }

    String getResultConfigurationDisplayName() {
        "newResultDescriptor"
    }

    String getParameterDisplayName() {
        "No Reinsurance Contracts"
    }

    protected boolean shouldCompareResults() {
        false
    }
}