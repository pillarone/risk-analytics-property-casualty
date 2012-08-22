import models.reinsuranceComparison.ReinsuranceComparisonModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

class ReinsuranceComparisonModelTests extends ModelTest {

    Class getModelClass() {
        ReinsuranceComparisonModel
    }

    protected boolean shouldCompareResults() {
        false
    }

}