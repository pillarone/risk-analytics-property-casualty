import models.mark.MarkModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

class MarkModelTests extends ModelTest {

    Class getModelClass() {
        MarkModel
    }

    protected boolean shouldCompareResults() {
        true
    }
}