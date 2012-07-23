import models.asset.AssetModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

class AssetModelTests extends ModelTest {

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