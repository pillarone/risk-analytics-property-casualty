import models.profiling.ExampleLobSplittedForProfilingModel
import org.pillarone.riskanalytics.core.simulation.engine.ModelTest
import org.apache.commons.logging.LogFactory

class ExampleLobSplittedForProfilingModelTests extends ModelTest {

    @Override
    protected void setUp() {
        LogFactory.getLog(getClass()).error("Currently running ${getClass().name}")
        super.setUp()
    }

    Class getModelClass() {
        ExampleLobSplittedForProfilingModel
    }

    //int getIterationCount() {
    //    return 10000
    //}

}
