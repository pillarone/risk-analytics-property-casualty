import org.pillarone.riskanalytics.core.output.CollectorMapping
import org.pillarone.riskanalytics.domain.pc.output.AggregateDrillDownCollectingModeStrategy

class PropertyCasualtiesBootStrap {

    def init = {servletContext ->

        CollectorMapping.withTransaction { status ->
            def c = new CollectorMapping(collectorName: AggregateDrillDownCollectingModeStrategy.IDENTIFIER);
            if (CollectorMapping.find(c) == null)
                c.save()
        }

    }

    def destroy = {
    }
}