import org.pillarone.riskanalytics.core.simulation.engine.ModelTest
import models.podraPC.PodraPCModel
import org.apache.commons.logging.LogFactory

class PodraPCModelTests extends ModelTest {

    @Override
    protected void setUp() {
        LogFactory.getLog(getClass()).error("Currently running ${getClass().name}")
        super.setUp()
    }

    Class getModelClass() {
        PodraPCModel
    }

    String getResultConfigurationDisplayName() {
        "Aggregated Overview"
    }

    String getParameterDisplayName() {
        "One Line Example P&C"
    }

    protected boolean shouldCompareResults() {
        false
    }
}