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

    // KTI-21
//    protected boolean shouldCompareResults() {
//        true
//    }
}