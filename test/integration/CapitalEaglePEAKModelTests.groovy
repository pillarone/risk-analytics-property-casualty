import models.capitalEagle.CapitalEagleModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest
import org.apache.commons.logging.LogFactory

class CapitalEaglePEAKModelTests extends ModelTest {

    @Override
    protected void setUp() {
        LogFactory.getLog(getClass()).error("Currently running ${getClass().name}")
        super.setUp()
    }

    Class getModelClass() {
        CapitalEagleModel
    }

    String getParameterFileName(){
        'CapitalEagleReinsuranceProgramPEAKParameters'
    }

    String getParameterDisplayName() {
        'CapitalEagle PEAK'
    }

    String getResultConfigurationDisplayName() {
        'newCollectors'
    }

    protected boolean shouldCompareResults() {
        true
    }
}