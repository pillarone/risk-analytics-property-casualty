package models.test

import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.pc.aggregators.ClaimsMerger
import org.pillarone.riskanalytics.domain.pc.lob.LobUnderwritingSegment

class StructureTestModel extends StochasticModel {

    ClaimsMerger claimsAggregator
    LobUnderwritingSegment mtpl


    DateTimeComponent dateComponent

    public void initComponents() {
        mtpl = new LobUnderwritingSegment()
        claimsAggregator = new ClaimsMerger()
        dateComponent = new DateTimeComponent()
        allComponents << mtpl
        allComponents << claimsAggregator
        allComponents << dateComponent
        addStartComponent mtpl

    }

    public void wireComponents() {
        claimsAggregator.inClaimsGross = mtpl.subClaimsGenerator.outClaims
        claimsAggregator.inClaimsCeded = mtpl.subRiProgram.subContract1.outCoveredClaims
    }
}

class DateTimeComponent extends Component {
    DateTime parmStartDate = new DateTime()

    protected void doCalculation() {
        // do nothing
    }


}