import org.pillarone.riskanalytics.core.output.CollectingModeFactory
import org.pillarone.riskanalytics.core.output.CollectorMapping
import org.pillarone.riskanalytics.core.output.ICollectingModeStrategy

class PropertyCasualtiesBootStrap {

    def init = {servletContext ->

        CollectorMapping.withTransaction { status ->
            // All available collectors must exist within the DB before simulation can run.
            CollectingModeFactory.getAvailableStrategies().each { ICollectingModeStrategy strategy ->
                def c = new CollectorMapping(collectorName: strategy.identifier)
                if (CollectorMapping.find(c) == null)
                    c.save()
            }
        }

    }

    def destroy = {
    }
}