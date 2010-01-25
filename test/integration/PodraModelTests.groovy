import models.podra.PodraModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

class PodraModelTests extends ModelTest {

    Class getModelClass() {
        PodraModel
    }

    String getResultConfigurationDisplayName() {
        "newResultDescriptor"
    }

    String getParameterDisplayName() {
        "No Reinsurance Contracts"
    }

//    int getIterationCount() {
//        1000
//    }


}