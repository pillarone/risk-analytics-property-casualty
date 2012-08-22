import models.sparrow.SparrowModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest
import org.apache.commons.logging.LogFactory

class SparrowModelTests extends ModelTest {

    @Override
    protected void setUp() {
        LogFactory.getLog(getClass()).error("Currently running ${getClass().name}")
        super.setUp()
    }

    Class getModelClass() {
        SparrowModel
    }

//    protected boolean shouldCompareResults() {
//        true
//    }
}