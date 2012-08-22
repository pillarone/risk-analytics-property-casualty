import models.podraFac.PodraFacModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest
import org.apache.commons.logging.LogFactory

class PodraFacModelTests extends ModelTest {

    @Override
    protected void setUp() {
        LogFactory.getLog(getClass()).error("Currently running ${getClass().name}")
        super.setUp()
    }

    Class getModelClass() {
        PodraFacModel
    }

    String getResultConfigurationDisplayName() {
        "CapitalEagle Analysis"
    }

    @Override
    String getResultConfigurationFileName() {
        "PodraFacAnalysisResultConfiguration"
    }

    String getParameterDisplayName() {
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