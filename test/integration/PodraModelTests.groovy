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

    protected boolean shouldCompareResults() {
        false
    }
}