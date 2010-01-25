package models.reinsuranceComparison

import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.pc.lob.ExampleLob4RIPrograms
import org.pillarone.riskanalytics.domain.pc.lob.PropertyLob4RIPrograms
import org.pillarone.riskanalytics.domain.pc.aggregators.ClaimsAggregator

class ReinsuranceComparisonModel extends StochasticModel {
    ExampleLob4RIPrograms mtpl
    ExampleLob4RIPrograms motorHull
    ExampleLob4RIPrograms personalAccident
    PropertyLob4RIPrograms property

    ClaimsAggregator claimsAggregatorReinsuranceProgramA
    ClaimsAggregator claimsAggregatorReinsuranceProgramB
    ClaimsAggregator claimsAggregatorReinsuranceProgramC
    ClaimsAggregator claimsAggregatorReinsuranceProgramD

    public void initComponents() {
        mtpl = new ExampleLob4RIPrograms()
        motorHull = new ExampleLob4RIPrograms()
        personalAccident = new ExampleLob4RIPrograms()
        property = new PropertyLob4RIPrograms()

        claimsAggregatorReinsuranceProgramA = new ClaimsAggregator()
        claimsAggregatorReinsuranceProgramB = new ClaimsAggregator()
        claimsAggregatorReinsuranceProgramC = new ClaimsAggregator()
        claimsAggregatorReinsuranceProgramD = new ClaimsAggregator()

        addStartComponent mtpl
        addStartComponent motorHull
        addStartComponent personalAccident
        addStartComponent property
    }

    public void wireComponents() {
        claimsAggregatorReinsuranceProgramA.inClaimsGross = mtpl.subRiProgramA.outClaimsGross
        claimsAggregatorReinsuranceProgramA.inClaimsGross = motorHull.subRiProgramA.outClaimsGross
        claimsAggregatorReinsuranceProgramA.inClaimsGross = personalAccident.subRiProgramA.outClaimsGross
        claimsAggregatorReinsuranceProgramA.inClaimsGross = property.subRiProgramA.outClaimsGross
        claimsAggregatorReinsuranceProgramA.inClaimsCeded = mtpl.subRiProgramA.outClaimsCeded
        claimsAggregatorReinsuranceProgramA.inClaimsCeded = motorHull.subRiProgramA.outClaimsCeded
        claimsAggregatorReinsuranceProgramA.inClaimsCeded = personalAccident.subRiProgramA.outClaimsCeded
        claimsAggregatorReinsuranceProgramA.inClaimsCeded = property.subRiProgramA.outClaimsCeded

        claimsAggregatorReinsuranceProgramB.inClaimsGross = mtpl.subRiProgramB.outClaimsGross
        claimsAggregatorReinsuranceProgramB.inClaimsGross = motorHull.subRiProgramB.outClaimsGross
        claimsAggregatorReinsuranceProgramB.inClaimsGross = personalAccident.subRiProgramB.outClaimsGross
        claimsAggregatorReinsuranceProgramB.inClaimsGross = property.subRiProgramB.outClaimsGross
        claimsAggregatorReinsuranceProgramB.inClaimsCeded = mtpl.subRiProgramB.outClaimsCeded
        claimsAggregatorReinsuranceProgramB.inClaimsCeded = motorHull.subRiProgramB.outClaimsCeded
        claimsAggregatorReinsuranceProgramB.inClaimsCeded = personalAccident.subRiProgramB.outClaimsCeded
        claimsAggregatorReinsuranceProgramB.inClaimsCeded = property.subRiProgramB.outClaimsCeded

        claimsAggregatorReinsuranceProgramC.inClaimsGross = mtpl.subRiProgramC.outClaimsGross
        claimsAggregatorReinsuranceProgramC.inClaimsGross = motorHull.subRiProgramC.outClaimsGross
        claimsAggregatorReinsuranceProgramC.inClaimsGross = personalAccident.subRiProgramC.outClaimsGross
        claimsAggregatorReinsuranceProgramC.inClaimsGross = property.subRiProgramC.outClaimsGross
        claimsAggregatorReinsuranceProgramC.inClaimsCeded = mtpl.subRiProgramC.outClaimsCeded
        claimsAggregatorReinsuranceProgramC.inClaimsCeded = motorHull.subRiProgramC.outClaimsCeded
        claimsAggregatorReinsuranceProgramC.inClaimsCeded = personalAccident.subRiProgramC.outClaimsCeded
        claimsAggregatorReinsuranceProgramC.inClaimsCeded = property.subRiProgramC.outClaimsCeded

        claimsAggregatorReinsuranceProgramD.inClaimsGross = mtpl.subRiProgramD.outClaimsGross
        claimsAggregatorReinsuranceProgramD.inClaimsGross = motorHull.subRiProgramD.outClaimsGross
        claimsAggregatorReinsuranceProgramD.inClaimsGross = personalAccident.subRiProgramD.outClaimsGross
        claimsAggregatorReinsuranceProgramD.inClaimsGross = property.subRiProgramD.outClaimsGross
        claimsAggregatorReinsuranceProgramD.inClaimsCeded = mtpl.subRiProgramD.outClaimsCeded
        claimsAggregatorReinsuranceProgramD.inClaimsCeded = motorHull.subRiProgramD.outClaimsCeded
        claimsAggregatorReinsuranceProgramD.inClaimsCeded = personalAccident.subRiProgramD.outClaimsCeded
        claimsAggregatorReinsuranceProgramD.inClaimsCeded = property.subRiProgramD.outClaimsCeded
    }
}