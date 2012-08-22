import models.reinsuranceComparison.ReinsuranceComparisonModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest
import org.apache.commons.logging.LogFactory

class ReinsuranceComparisonModelTests extends ModelTest {

    @Override
    protected void setUp() {
        LogFactory.getLog(getClass()).error("Currently running ${getClass().name}")
        super.setUp()
    }

    Class getModelClass() {
        ReinsuranceComparisonModel
    }

    protected boolean shouldCompareResults() {
        false
    }

}