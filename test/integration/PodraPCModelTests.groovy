import org.pillarone.riskanalytics.core.simulation.engine.ModelTest
import models.podraPC.PodraPCModel
import org.apache.commons.logging.LogFactory

class PodraPCModelTests extends ModelTest {

    @Override
    protected void setUp() {
        def log = LogFactory.getLog(getClass())
        log.error("Currently running ${getClass().name}")
        Thread.start {
            while (true) {
                sleep(60000)
                log.error(Thread.allStackTraces.toString())
            }
        }
        super.setUp()
    }

    Class getModelClass() {
        PodraPCModel
    }

    String getResultConfigurationDisplayName() {
        "Aggregated Overview"
    }

    String getParameterDisplayName() {
        "One Line Example P&C"
    }

    protected boolean shouldCompareResults() {
        false
    }
}