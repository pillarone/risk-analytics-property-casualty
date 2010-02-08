import models.podra.PodraModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

class PodraModelTests extends ModelTest {

    Class getModelClass() {
        PodraModel
    }

    String getResultConfigurationDisplayName() {
        "Aggregated Overview 2"
    }

    String getParameterDisplayName() {
        "One Line Example"
    }
}