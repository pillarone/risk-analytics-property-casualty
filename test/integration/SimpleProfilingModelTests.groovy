import models.profiling.SimpleProfilingModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest
import org.apache.commons.logging.LogFactory

class SimpleProfilingModelTests extends ModelTest {

    @Override
    protected void setUp() {
        LogFactory.getLog(getClass()).error("Currently running ${getClass().name}")
        super.setUp()
    }

    String getResultConfigurationFileName() {
        return "SimpleProfilingNoCollectorsResultConfiguration"
    }

    Class getModelClass() {
        SimpleProfilingModel
    }

}