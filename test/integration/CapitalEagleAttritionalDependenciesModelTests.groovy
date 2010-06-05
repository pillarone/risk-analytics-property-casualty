import models.capitalEagle.CapitalEagleAttritionalDependenciesModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

class CapitalEagleAttritionalDependenciesModelTests extends ModelTest {

    Class getModelClass() {
        CapitalEagleAttritionalDependenciesModel
    }

    String getParameterDisplayName() {
        "One Reinsurance Program (attr correlated)"
    }

    String getResultConfigurationDisplayName() {
        "Attritional Gross Claims"
    }

    String getParameterFileName() {
        return "CapitalEagleAttritionalDependencies4Parameters"
    }

    protected boolean shouldCompareResults() {
        true
    }
}