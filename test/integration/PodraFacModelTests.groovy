import models.podraFac.PodraFacModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

class PodraFacModelTests extends ModelTest {

    Class getModelClass() {
        PodraFacModel
    }

    String getResultConfigurationDisplayName() {
        "Aggregated Overview"
    }

    String getParameterDisplayName() {
//        "One Line Example"
        "Test Example"
    }

    @Override
    String getParameterFileName() {
        "TestPodraFacParameters"
    }

    protected boolean shouldCompareResults() {
        true
    }
}