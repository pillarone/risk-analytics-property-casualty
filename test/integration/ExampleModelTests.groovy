import org.pillarone.riskanalytics.core.simulation.engine.ModelTest
import models.example.ExampleModel
/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ExampleModelTests extends ModelTest {
    
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
}
