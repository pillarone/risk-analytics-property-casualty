import org.pillarone.riskanalytics.core.simulation.engine.ModelTest
import models.example.ExampleModel
import org.apache.commons.logging.LogFactory
/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ExampleModelTests extends ModelTest {

    @Override
    protected void setUp() {
        LogFactory.getLog(getClass()).error("Currently running ${getClass().name}")
        super.setUp()
    }
    
    Class getModelClass() {
        ExampleModel
    }

    String getParameterFileName() {
        'ExampleAbsoluteParameters.groovy'
    }

    String getParameterDisplayName() {
        'Moderate Inflation (Absolute)'
    }

    String getResultConfigurationDisplayName() {
        'All Aggregated'        
    }

//    protected boolean shouldCompareResults() {
//        true
//    }
}
