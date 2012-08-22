import org.pillarone.riskanalytics.core.simulation.engine.ModelTest
import models.oneLobQSXL.OneLobQSXLModel
import org.apache.commons.logging.LogFactory

class OneLobQSXLModelTests extends ModelTest {

    @Override
    protected void setUp() {
        LogFactory.getLog(getClass()).error("Currently running ${getClass().name}")
        super.setUp()
    }

    Class getModelClass() {
        OneLobQSXLModel
    }

//    protected boolean shouldCompareResults() {
//        true
//    }
}