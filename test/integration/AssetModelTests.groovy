import models.asset.AssetModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest
import org.apache.commons.logging.LogFactory

class AssetModelTests extends ModelTest {

    @Override
    protected void setUp() {
        LogFactory.getLog(getClass()).error("Currently running ${getClass().name}")
        super.setUp()
    }

    Class getModelClass() {
        AssetModel
    }

    String getParameterDisplayName() {
        return "Test"
    }

    String getResultConfigurationDisplayName() {
        return "Asset"
    }
}