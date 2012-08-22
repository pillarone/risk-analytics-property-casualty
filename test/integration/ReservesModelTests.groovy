import models.reserves.ReservesModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest
import org.apache.commons.logging.LogFactory

class ReservesModelTests extends ModelTest {

    @Override
    protected void setUp() {
        LogFactory.getLog(getClass()).error("Currently running ${getClass().name}")
        super.setUp()
    }

    Class getModelClass() {
        ReservesModel
    }

    String getParameterDisplayName() {
        return "Prior Period Example"
    }

    String getResultConfigurationDisplayName() {
        return "Reserved Claims"
    }

    // todo(sku): works locally but not in hudson
//    protected boolean shouldCompareResults() {
//        true
//    }
}