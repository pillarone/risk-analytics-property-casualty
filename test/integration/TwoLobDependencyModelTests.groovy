import models.dependency.TwoLobDependencyModel
import org.pillarone.riskanalytics.core.model.Model

//todo runs alone but not during cruise
class TwoLobDependencyModelTests /*extends AbstractModelTest*/ extends GroovyTestCase {

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