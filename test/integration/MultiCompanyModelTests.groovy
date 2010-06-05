import models.multiCompany.MultiCompanyModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

class MultiCompanyModelTests extends ModelTest {

    Class getModelClass() {
        MultiCompanyModel
    }

    String getResultConfigurationDisplayName() {
        "Aggregated Overview 3"
    }

    String getParameterDisplayName() {
        "Three Companies"
    }

    // PMO-654
//    protected boolean shouldCompareResults() {
//        true
//    }
}