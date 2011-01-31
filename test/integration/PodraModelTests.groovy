import models.podra.PodraModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

class PodraModelTests extends ModelTest {

    Class getModelClass() {
        PodraModel
    }

    String getResultConfigurationDisplayName() {
        'Aggregated Overview'
    }

    @Override
    String getParameterFileName() {
        'PodraReinsuranceProgramNPALL50Parameters'
    }

    String getParameterDisplayName() {
        'CapitalEagle NP+ALL50'
    }

    // KTI-21
//    protected boolean shouldCompareResults() {
//        true
//    }
}