import models.capitalEagle.CapitalEagleModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

class CapitalEaglePEAKModelTests extends ModelTest {

    Class getModelClass() {
        CapitalEagleModel
    }

    String getParameterFileName(){
        'CapitalEagleReinsuranceProgramPeakParameters'
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