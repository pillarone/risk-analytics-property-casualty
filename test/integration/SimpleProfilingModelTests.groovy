import models.profiling.SimpleProfilingModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest

class SimpleProfilingModelTests extends ModelTest {

    String getResultConfigurationFileName() {
        return "SimpleProfilingNoCollectorsResultConfiguration"
    }

    Class getModelClass() {
        SimpleProfilingModel
    }

}