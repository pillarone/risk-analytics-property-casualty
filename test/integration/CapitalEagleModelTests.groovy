import models.capitalEagle.CapitalEagleModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

class CapitalEagleModelTests extends ModelTest {

    Class getModelClass() {
        CapitalEagleModel
    }

    String getParameterDisplayName() {
        'One Reinsurance Program'
    }

    String getResultConfigurationDisplayName() {
        'newCollectors'
    }

    // todo: PMO-654
//    protected boolean shouldCompareResults() {
//        true
//    }
}