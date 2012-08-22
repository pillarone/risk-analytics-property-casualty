import models.dependency.TwoLobDependencyModel
import org.pillarone.riskanalytics.core.model.Model
import org.apache.commons.logging.LogFactory

//todo runs alone but not during cruise
class TwoLobDependencyModelTests /*extends AbstractModelTest*/ extends GroovyTestCase {

    @Override
    protected void setUp() {
        LogFactory.getLog(getClass()).error("Currently running ${getClass().name}")
        super.setUp()
    }

    void testNothing() {
        assertTrue true
    }

    protected String getResultDescriptorFileName() {
        return "src/java/models/dependency/TwoLobDependencyResultDescriptor"
    }

    protected String getStructureFileName() {
        return "src/java/models/dependency/TwoLobDependencyStructure"
    }

    protected String getParameterFileName() {
        return "src/java/models/dependency/TwoLobDependencyParameters"
    }

    protected Model getModel() {
        return new TwoLobDependencyModel()
    }
}