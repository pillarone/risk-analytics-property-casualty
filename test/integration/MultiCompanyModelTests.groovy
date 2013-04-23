import models.multiCompany.MultiCompanyModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

class MultiCompanyModelTests extends ModelTest {

    Class getModelClass() {
        MultiCompanyModel
    }

    String getResultConfigurationDisplayName() {
        "Aggregated Overview"
    }

    String getParameterDisplayName() {
        "Three Companies"
    }

    protected boolean shouldCompareResults() {
        true
    }
}