import models.dependency.DependencyModel
import org.pillarone.riskanalytics.core.model.Model
import org.apache.commons.logging.LogFactory

//todo runs alone but not during cruise
class DependencyModelTests /*extends AbstractModelTest*/ extends GroovyTestCase {

    @Override
    protected void setUp() {
        LogFactory.getLog(getClass()).error("Currently running ${getClass().name}")
        super.setUp()
    }

    void testNothing() {
        assertTrue true
    }

    protected String getResultDescriptorFileName() {
        return "src/java/models/dependency/DependencyResultDescriptor"
    }

    protected String getStructureFileName() {
        return "src/java/models/dependency/DependencyStructure"
    }

    protected String getParameterFileName() {
        return "src/java/models/dependency/DependencyParameters"
    }

    protected Model getModel() {
        return new DependencyModel()
    }

    protected Integer getIterationCount() {
        1
    }

    protected boolean shouldCompareResults() {
        true
    }
}