import models.finiteRe.FiniteReModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

class FiniteReModelTests extends ModelTest {

    Class getModelClass() {
        FiniteReModel
    }

    String getParameterDisplayName() {
        "Example EAB"
    }

    String getResultConfigurationDisplayName() {
        "EAB, Claims (aggregated)"
    }

    int getIterationCount() {
        100
    }


}