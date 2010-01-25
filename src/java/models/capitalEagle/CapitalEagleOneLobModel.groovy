package models.capitalEagle


import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.pc.lob.ExampleLob
import org.pillarone.riskanalytics.domain.pc.aggregators.ClaimsAggregator

class CapitalEagleOneLobModel extends StochasticModel {
    ExampleLob mtpl
    ClaimsAggregator claimsAggregator


    public void initComponents() {
        mtpl = new ExampleLob()
        claimsAggregator = new ClaimsAggregator()

        addStartComponent mtpl
    }

    public void wireComponents() {
        claimsAggregator.inClaimsGross = mtpl.subRiProgram.outClaimsGross
        claimsAggregator.inClaimsCeded = mtpl.subRiProgram.outClaimsCeded
    }
}