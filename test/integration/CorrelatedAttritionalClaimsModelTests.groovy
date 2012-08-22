import models.correlatedAttritionalClaims.CorrelatedAttritionalClaimsModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest
import org.apache.commons.logging.LogFactory

class CorrelatedAttritionalClaimsModelTests extends ModelTest {

    @Override
    protected void setUp() {
        LogFactory.getLog(getClass()).error("Currently running ${getClass().name}")
        super.setUp()
    }

    Class getModelClass() {
        return CorrelatedAttritionalClaimsModel
    }

    protected boolean shouldCompareResults() {
        true
    }
}