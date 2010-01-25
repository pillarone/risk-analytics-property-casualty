import org.pillarone.riskanalytics.core.simulation.engine.ModelTest
import models.podraWithReserves.PodraWithReservesModel

class PodraWithReservesModelTests extends ModelTest {

    Class getModelClass() {
        PodraWithReservesModel
    }

    String getResultConfigurationDisplayName() {
        "Aggregated Overview"
    }

    String getParameterDisplayName() {
        "One Line Example"
    }
}