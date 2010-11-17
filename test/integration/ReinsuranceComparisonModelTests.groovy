import models.reinsuranceComparison.ReinsuranceComparisonModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

class ReinsuranceComparisonModelTests extends ModelTest {

    Class getModelClass() {
        ReinsuranceComparisonModel
    }

    // KTI-21
//    protected boolean shouldCompareResults() {
//        true
//    }

}