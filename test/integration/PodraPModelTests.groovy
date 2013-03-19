import org.pillarone.riskanalytics.core.simulation.engine.ModelTest
import models.podraP.PodraPModel

class PodraPModelTests extends ModelTest {

    Class getModelClass() {
        PodraPModel
    }

    String getResultConfigurationDisplayName() {
        "newResultDescriptor"
    }

    String getParameterDisplayName() {
        "No Reinsurance Contracts"
    }

    // KTI-21
//    protected boolean shouldCompareResults() {
//        true
//    }
}