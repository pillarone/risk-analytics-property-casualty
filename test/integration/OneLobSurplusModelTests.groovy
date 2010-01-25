import models.onelobsurplus.OneLobSurplusModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

/**
 * @author Michael-Noe (at) Web (dot) de
 */

class OneLobSurplusModelTests extends ModelTest {

    Class getModelClass() {
        OneLobSurplusModel
    }

    // todo(sku): works locally but not in hudson
//    protected boolean shouldCompareResults() {
//        true
//    }
}