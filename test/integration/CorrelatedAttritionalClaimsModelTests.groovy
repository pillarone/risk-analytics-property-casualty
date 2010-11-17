import models.correlatedAttritionalClaims.CorrelatedAttritionalClaimsModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

class CorrelatedAttritionalClaimsModelTests extends ModelTest {

    Class getModelClass() {
        return CorrelatedAttritionalClaimsModel
    }

    // KTI-21
//    protected boolean shouldCompareResults() {
//        true
//    }
}