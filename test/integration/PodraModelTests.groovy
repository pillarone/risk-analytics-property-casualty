import models.podra.PodraModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest
import org.apache.commons.logging.LogFactory

class PodraModelTests extends ModelTest {

    @Override
    protected void setUp() {
        LogFactory.getLog(getClass()).error("Currently running ${getClass().name}")
        super.setUp()
    }

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