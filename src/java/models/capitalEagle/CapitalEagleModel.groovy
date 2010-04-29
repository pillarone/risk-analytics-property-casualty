package models.capitalEagle

import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.pc.aggregators.ClaimsAggregator
import org.pillarone.riskanalytics.domain.pc.lob.ExampleLob
import org.pillarone.riskanalytics.domain.pc.lob.PropertyLob

class CapitalEagleModel extends StochasticModel {
    ExampleLob mtpl
    ExampleLob motorHull
    ExampleLob personalAccident
    PropertyLob property
    ClaimsAggregator claimsAggregator


    public void initComponents() {
        mtpl = new ExampleLob()
        motorHull = new ExampleLob()
        personalAccident = new ExampleLob()
        property = new PropertyLob()
        claimsAggregator = new ClaimsAggregator()

        addStartComponent mtpl
        addStartComponent motorHull
        addStartComponent personalAccident
        addStartComponent property
    }

    public void wireComponents() {
        claimsAggregator.inClaimsGross = mtpl.subRiProgram.outClaimsGross
        claimsAggregator.inClaimsGross = motorHull.subRiProgram.outClaimsGross
        claimsAggregator.inClaimsGross = personalAccident.subRiProgram.outClaimsGross
        claimsAggregator.inClaimsGross = property.subRiProgram.outClaimsGross
        claimsAggregator.inClaimsCeded = mtpl.subRiProgram.outClaimsCeded
        claimsAggregator.inClaimsCeded = motorHull.subRiProgram.outClaimsCeded
        claimsAggregator.inClaimsCeded = personalAccident.subRiProgram.outClaimsCeded
        claimsAggregator.inClaimsCeded = property.subRiProgram.outClaimsCeded
    }
}