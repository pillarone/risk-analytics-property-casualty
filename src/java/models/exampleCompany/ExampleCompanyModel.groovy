package models.exampleCompany

import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.pc.lob.ExampleLob
import org.pillarone.riskanalytics.domain.pc.aggregators.ClaimsMerger

/**
 * @author: stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ExampleCompanyModel extends StochasticModel {

    ExampleLob mtpl
    ExampleLob motorHull
    ExampleLob personalAccident
    ExampleLob property
    ClaimsMerger claimsAggregator

    void initComponents() {
        mtpl = new ExampleLob()
        motorHull = new ExampleLob()
        personalAccident = new ExampleLob()
        property = new ExampleLob()
        claimsAggregator = new ClaimsMerger()

        allComponents << mtpl
        allComponents << motorHull
        allComponents << personalAccident
        allComponents << property
        allComponents << claimsAggregator

        addStartComponent mtpl
        addStartComponent motorHull
        addStartComponent personalAccident
        addStartComponent property
    }

    void wireComponents() {
        claimsAggregator.inClaimsGross = mtpl.subClaimsGenerator.outClaims
        claimsAggregator.inClaimsCeded = mtpl.subRiProgram.outClaimsCeded
        claimsAggregator.inClaimsGross = motorHull.subClaimsGenerator.outClaims
        claimsAggregator.inClaimsCeded = motorHull.subRiProgram.subContract1.outCoveredClaims
        claimsAggregator.inClaimsGross = personalAccident.subClaimsGenerator.outClaims
        claimsAggregator.inClaimsCeded = personalAccident.subRiProgram.subContract1.outCoveredClaims
        claimsAggregator.inClaimsGross = property.subClaimsGenerator.outClaims
        claimsAggregator.inClaimsCeded = property.subRiProgram.subContract1.outCoveredClaims
    }
}