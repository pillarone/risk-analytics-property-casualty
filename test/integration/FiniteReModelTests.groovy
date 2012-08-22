import models.finiteRe.FiniteReModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest
import org.apache.commons.logging.LogFactory

class FiniteReModelTests extends ModelTest {

    @Override
    protected void setUp() {
        LogFactory.getLog(getClass()).error("Currently running ${getClass().name}")
        super.setUp()
    }

    Class getModelClass() {
        FiniteReModel
    }

    String getParameterDisplayName() {
        "Example EAB"
    }

    String getResultConfigurationDisplayName() {
        "EAB, Claims (aggregated)"
    }

    protected boolean shouldCompareResults() {
        true
    }
}