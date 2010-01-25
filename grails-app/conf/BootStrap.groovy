import org.pillarone.riskanalytics.core.output.CollectingModeFactory
import org.pillarone.riskanalytics.domain.pc.output.AggregatedDrillDownCollectingModeStrategy
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.parameterization.SimpleConstraint
import org.pillarone.riskanalytics.domain.pc.constraints.PerilPortion
import org.pillarone.riskanalytics.domain.pc.constraints.UnderwritingPortion
import org.pillarone.riskanalytics.domain.pc.constraints.ReservePortion


class BootStrap {

    def init = {servletContext ->

        CollectingModeFactory.registerStrategy(new AggregatedDrillDownCollectingModeStrategy())

        ConstraintsFactory.registerConstraint(new SimpleConstraint())
        ConstraintsFactory.registerConstraint(new PerilPortion())
        ConstraintsFactory.registerConstraint(new UnderwritingPortion())
        ConstraintsFactory.registerConstraint(new ReservePortion())

    }

    def destroy = {
    }
}
