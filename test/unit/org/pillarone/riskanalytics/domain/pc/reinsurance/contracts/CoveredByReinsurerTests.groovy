package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.CoverAttributeStrategyType
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope
import org.pillarone.riskanalytics.domain.assets.VoidTestModel
import org.pillarone.riskanalytics.domain.pc.generators.claims.TypableClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.lob.ConfigurableLobWithReserves
import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.pc.constants.IncludeType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.CompanyCoverAttributeStrategyType
import org.pillarone.riskanalytics.domain.pc.company.Company
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.constraints.CompanyPortion
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.components.PeriodStore
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class CoveredByReinsurerTests extends GroovyTestCase {

    static MultiCoverAttributeReinsuranceContract getQuotaShare0() {
        MultiCoverAttributeReinsuranceContract contract = new MultiCoverAttributeReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getStrategy(
                        ReinsuranceContractType.QUOTASHARE,
                        ["quotaShare": 0.2, "coveredByReinsurer":0.1]),
                parmInuringPriority: 10,
                parmCover: CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.ALL,["reserves": IncludeType.NOTINCLUDED]))

        SimulationScope simulationScope = new SimulationScope(iterationScope: new IterationScope(periodScope: new PeriodScope()))
        simulationScope.model = new VoidTestModel()
        contract.simulationScope = simulationScope
        contract.periodStore = new PeriodStore(simulationScope.getIterationScope().getPeriodScope())
        return contract
    }

    static MultiCompanyCoverAttributeReinsuranceContract getQuotaShare1() {
        MultiCompanyCoverAttributeReinsuranceContract contract = new MultiCompanyCoverAttributeReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getStrategy(
                        ReinsuranceContractType.QUOTASHARE,
                        ["quotaShare": 0.2, "coveredByReinsurer":0.1]),
                parmInuringPriority: 10,
                parmCover: CompanyCoverAttributeStrategyType.getStrategy(CompanyCoverAttributeStrategyType.ALL,["reserves": IncludeType.NOTINCLUDED]))

        SimulationScope simulationScope = new SimulationScope(iterationScope: new IterationScope(periodScope: new PeriodScope()))
        simulationScope.model = new VoidTestModel()
        contract.simulationScope = simulationScope
        contract.periodStore = new PeriodStore(simulationScope.getIterationScope().getPeriodScope())
        return contract

    }

    TypableClaimsGenerator fire = new TypableClaimsGenerator()
    ConfigurableLobWithReserves fireLob = new ConfigurableLobWithReserves();
    Company earthRe = new Company(name:'earth re')

    private Claim getClaim(double ultimate, IPerilMarker peril, ISegmentMarker lob) {
        Claim claim = new Claim(ultimate: ultimate)
        claim.addMarker(IPerilMarker, peril)
        claim.addMarker(ISegmentMarker, lob)
        claim
    }

    void testMultiCoverReinsuranceContract(){

        MultiCoverAttributeReinsuranceContract contract = getQuotaShare0();

        contract.inClaims << getClaim(80, fire, fireLob) << getClaim(100, fire, fireLob)
        contract.inUnderwritingInfo << new UnderwritingInfo(premium: 60, lineOfBusiness: fireLob) << new UnderwritingInfo(premium: 100, lineOfBusiness: fireLob)

        def claimsCeded = new TestProbe(contract, 'outCoveredClaims')
        def claimsNet = new TestProbe(contract, 'outUncoveredClaims')
        def underwritingInfoCeded = new TestProbe(contract, 'outCoverUnderwritingInfo')
        def underwritingInfoNet = new TestProbe(contract, 'outNetAfterCoverUnderwritingInfo')

        contract.doCalculation()

        assertEquals "claim ceded",1.6, contract.outCoveredClaims[0].ultimate
        assertEquals "claim ceded",2.0, contract.outCoveredClaims[1].ultimate
        assertEquals "claim net",80-1.6, contract.outUncoveredClaims[0].ultimate
        assertEquals "claim net",98, contract.outUncoveredClaims[1].ultimate
        assertEquals "underwriting info ceded",1.2, contract.outCoverUnderwritingInfo[0].premium,1E-14
        assertEquals "underwriting info ceded",2.0, contract.outCoverUnderwritingInfo[1].premium,1E-14
        assertEquals "underwriting info net",60-1.2, contract.outNetAfterCoverUnderwritingInfo[0].premium,1E-14
        assertEquals "underwriting info net",98, contract.outNetAfterCoverUnderwritingInfo[1].premium,1E-14
    }

    void testMultiCompanyCoverReinsuranceContract(){

        MultiCompanyCoverAttributeReinsuranceContract contract = getQuotaShare1();
        contract.parmReinsurers = new ConstrainedMultiDimensionalParameter([['earth re'],[0.5]],['Reinsurer','Covered Portion'],
                ConstraintsFactory.getConstraints(CompanyPortion.IDENTIFIER))

        def claimsCeded = new TestProbe(contract, 'outCoveredClaims')
        def claimsNet = new TestProbe(contract, 'outUncoveredClaims')
        def underwritingInfoCeded = new TestProbe(contract, 'outCoverUnderwritingInfo')
        def underwritingInfoNet = new TestProbe(contract, 'outNetAfterCoverUnderwritingInfo')

        PacketList<Claim> incomingClaims = new PacketList<Claim>(Claim)
        incomingClaims << getClaim(80, fire, fireLob) << getClaim(100, fire, fireLob)
        PacketList<UnderwritingInfo> incomingUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo)
        incomingUnderwritingInfo << new UnderwritingInfo(premium: 60, lineOfBusiness: fireLob) << new UnderwritingInfo(premium: 100, lineOfBusiness: fireLob)
        contract.filterInChannel(contract.inClaims, incomingClaims)
        contract.filterInChannel(contract.inUnderwritingInfo, incomingUnderwritingInfo)
        contract.doCalculation()

        assertEquals "claim ceded",0.5*1.6, contract.outCoveredClaims[0].ultimate
        assertEquals "claim ceded",0.5*2.0, contract.outCoveredClaims[1].ultimate
        assertEquals "claim net",80-0.5*1.6, contract.outUncoveredClaims[0].ultimate
        assertEquals "claim net",99, contract.outUncoveredClaims[1].ultimate
        assertEquals "underwriting info ceded",0.5*1.2, contract.outCoverUnderwritingInfo[0].premium,1E-14
        assertEquals "underwriting info ceded",0.5*2.0, contract.outCoverUnderwritingInfo[1].premium,1E-14
        assertEquals "underwriting info net",60-0.5*1.2, contract.outNetAfterCoverUnderwritingInfo[0].premium,1E-14
        assertEquals "underwriting info net",99, contract.outNetAfterCoverUnderwritingInfo[1].premium,1E-14
    }
}
