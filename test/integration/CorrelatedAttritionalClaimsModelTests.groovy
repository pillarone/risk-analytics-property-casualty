import models.correlatedAttritionalClaims.CorrelatedAttritionalClaimsModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

class CorrelatedAttritionalClaimsModelTests extends ModelTest {

    Class getModelClass() {
        return CorrelatedAttritionalClaimsModel
    }

    protected boolean shouldCompareResults() {
        true
    }
}