package models.singleLob

import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.pc.lob.MultiSingleLob
import org.pillarone.riskanalytics.domain.pc.aggregators.ClaimsAggregator

class SingleLobMultiClaimsGeneratorModel extends StochasticModel {
    MultiSingleLob mtpl
    ClaimsAggregator claimsAggregator


    public void initComponents() {
        mtpl = new MultiSingleLob()
        claimsAggregator = new ClaimsAggregator()

        addStartComponent mtpl
    }

    public void wireComponents() {
        claimsAggregator.inClaimsGross = mtpl.subRiProgram.outClaimsGross
        claimsAggregator.inClaimsCeded = mtpl.subRiProgram.outClaimsCeded
    }
}