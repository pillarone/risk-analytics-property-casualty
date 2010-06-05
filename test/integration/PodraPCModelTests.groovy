import org.pillarone.riskanalytics.core.simulation.engine.ModelTest
import models.podraPC.PodraPCModel

class PodraPCModelTests extends ModelTest {

    Class getModelClass() {
        PodraPCModel
    }

    String getResultConfigurationDisplayName() {
        "Aggregated Overview"
    }

    String getParameterDisplayName() {
        "One Line Example P&C"
    }

    // todo: PMO-654
//    protected boolean shouldCompareResults() {
//        true
//    }
}