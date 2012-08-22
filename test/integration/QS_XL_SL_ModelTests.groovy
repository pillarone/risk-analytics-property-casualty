import models.attrLargeClaimsRiProgram.QS_XL_SL_Model
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest
import org.apache.commons.logging.LogFactory

class QS_XL_SL_ModelTests extends ModelTest {

    @Override
    protected void setUp() {
        LogFactory.getLog(getClass()).error("Currently running ${getClass().name}")
        super.setUp()
    }

    Class getModelClass() {
        QS_XL_SL_Model
    }

    protected boolean shouldCompareResults() {
        true
    }
}