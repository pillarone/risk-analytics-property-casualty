import models.asset.AssetModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

class AssetModelTestsNot extends ModelTest {

    Class getModelClass() {
        AssetModel
    }

    String getParameterDisplayName() {
        return "Test"
    }

    String getResultConfigurationDisplayName() {
        return "Asset"
    }

    int getPeriodCount() {
        return 3
    }

}