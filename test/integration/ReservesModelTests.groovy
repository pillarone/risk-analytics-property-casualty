import models.reserves.ReservesModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

class ReservesModelTests extends ModelTest {

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