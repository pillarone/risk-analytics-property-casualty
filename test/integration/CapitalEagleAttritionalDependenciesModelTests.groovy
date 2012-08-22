import models.capitalEagle.CapitalEagleAttritionalDependenciesModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest
import org.apache.commons.logging.LogFactory

class CapitalEagleAttritionalDependenciesModelTests extends ModelTest {

    @Override
    protected void setUp() {
        LogFactory.getLog(getClass()).error("Currently running ${getClass().name}")
        super.setUp()
    }

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
}