package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.example.component.TestComponent
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope
import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.assets.VoidTestModel
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.claims.TestLobComponent
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.generators.claims.TypableClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.generators.severities.Event
import org.pillarone.riskanalytics.domain.pc.lob.LobMarker
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.reserves.IReserveMarker
import org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.core.components.PeriodStore

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class MultiLineReinsuranceContractTests extends GroovyTestCase {

    static MultiLineReinsuranceContract getQSContract20Percent() {
        MultiLineReinsuranceContract contract = new MultiLineReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getStrategy(
                        ReinsuranceContractType.QUOTASHARE,
                        ["quotaShare": 0.2,
                         "coveredByReinsurer": 1d]),
                parmInuringPriority: 10,
                parmCoveredLines: new ComboBoxTableMultiDimensionalParameter(['fire'], ['Covered Segments'], LobMarker)
        )
        SimulationScope simulationScope = new SimulationScope(iterationScope: new IterationScope(periodScope: new PeriodScope()))
        simulationScope.model = new VoidTestModel()
        contract.simulationScope = simulationScope
        contract.periodStore = new PeriodStore(simulationScope.iterationScope.periodScope)
        return contract

    }

    // todo(sku): to be completed
    void testUsage() {
        Component component1 = new TestComponent()
        Component component2 = new TestComponent()
        Component component3 = new TestComponent()

        Claim claimFire100 = new Claim(origin: component1, claimType: ClaimType.ATTRITIONAL, value: 100d, fractionOfPeriod: 0.2)
        Claim claimHull60 = new Claim(origin: component2, claimType: ClaimType.SINGLE, value: 60d, fractionOfPeriod: 0.3)
        Claim claimLegal200 = new Claim(origin: component3, claimType: ClaimType.SINGLE, value: 200d, fractionOfPeriod: 0.1)

        UnderwritingInfo underwritingInfoFire = new UnderwritingInfo(origin: component1)
        UnderwritingInfo underwritingInfoHull = new UnderwritingInfo(origin: component2)

        MultiLineReinsuranceContract contract = getQSContract20Percent()

        contract.inClaims << claimFire100 << claimHull60 << claimLegal200
        contract.inUnderwritingInfo << underwritingInfoFire << underwritingInfoHull

        def probeContractUWI = new TestProbe(contract, "outCoverUnderwritingInfo")    // needed in order to trigger the calculation of net claims

    }

    void testUsageWithClaimDevelopmentLeanPackets() {
        MultiLineReinsuranceContract contract = MultiLineReinsuranceContractTests.getQSContract20Percent()
        TestComponent origin = new TestComponent()
        contract.simulationScope.model.allComponents << origin
        contract.parmCoveredLines.setSimulationModel contract.simulationScope.model
        TypableClaimsGenerator generator = new TypableClaimsGenerator()
        Event event1 = new Event()
        Event event2 = new Event()
        Claim originalClaim1 = getClaim(generator, null, 10000, 0.6, ClaimType.ATTRITIONAL)
        Claim originalClaim2 = getClaim(generator, null, 500, 0.5, ClaimType.ATTRITIONAL)
        ClaimDevelopmentLeanPacket claimDevelopment1 = getClaim(generator, null, 10, 6, 0.4, origin, event1, originalClaim1)
        ClaimDevelopmentLeanPacket claimDevelopment2 = getClaim(generator, null, 12, 8, 0.7, origin, event2, originalClaim2)

        def probeCoveredClaims = new TestProbe(contract, "outCoveredClaims")
        List coveredClaims = probeCoveredClaims.result

        PacketList<Claim> incomingClaims = new PacketList<Claim>(Claim)
        incomingClaims << claimDevelopment1 << claimDevelopment2
        contract.filterInChannels(contract.inClaims, incomingClaims)
        contract.start()

        assertEquals '# ceded claims packets', 2, coveredClaims.size()
        assertEquals 'ceded incurred 0', 2d, coveredClaims[0].incurred
        assertEquals 'ceded incurred 1', 2.4, coveredClaims[1].incurred, 1E-10
        assertEquals 'ceded paid 0', 1.2, coveredClaims[0].paid, 1E-10
        assertEquals 'ceded paid 1', 1.6, coveredClaims[1].paid, 1E-10
        assertEquals 'ceded reserved 0', 0.8, coveredClaims[0].reserved, 1E-10
        assertEquals 'ceded reserved 1', 0.8, coveredClaims[1].reserved, 1E-10
    }

    void testCoverLines() {
        MultiLineReinsuranceContract contract = MultiLineReinsuranceContractTests.getQSContract20Percent()
        TestComponent origin = new TestComponent()
        TypableClaimsGenerator generator = new TypableClaimsGenerator()
        TestLobComponent lobFire = new TestLobComponent(name: 'fire')
        TestLobComponent lobMotor = new TestLobComponent(name: 'motor')
        contract.simulationScope.model.allComponents << lobFire << lobMotor
        contract.parmCoveredLines.setSimulationModel contract.simulationScope.model
        Event event1 = new Event()
        Event event2 = new Event()
        Claim originalClaim1 = getClaim(generator, null, 10000, 0.6, ClaimType.ATTRITIONAL)
        Claim originalClaim2 = getClaim(generator, null, 500, 0.5, ClaimType.ATTRITIONAL)
        ClaimDevelopmentLeanPacket claimDevelopment1 = getClaim(generator, lobFire, 10, 6, 0.4, origin, event1, originalClaim1)
        ClaimDevelopmentLeanPacket claimDevelopment2 = getClaim(generator, lobMotor, 12, 8, 0.7, origin, event2, originalClaim2)

        PacketList<Claim> incomingClaims = new PacketList<Claim>(Claim)
        incomingClaims << claimDevelopment1 << claimDevelopment2
        contract.filterInChannels(contract.inClaims, incomingClaims)
        contract.doCalculation()
        assertEquals '# ceded claims packets', 1, contract.outCoveredClaims.size()
        assertEquals 'ceded incurred 0', 2d, contract.outCoveredClaims[0].incurred
        assertEquals 'ceded paid 0', 1.2, contract.outCoveredClaims[0].paid, 1E-10
        assertEquals 'ceded reserved 0', 0.8, contract.outCoveredClaims[0].reserved, 1E-10
        assertEquals 'origin of fire claim', originalClaim1, contract.outCoveredClaims[0].originalClaim
        contract.reset()


        contract.parmCoveredLines = new ComboBoxTableMultiDimensionalParameter(['motor'], ['Covered Segments'], LobMarker)
        contract.parmCoveredLines.setSimulationModel contract.simulationScope.model
        contract.simulationScope.iterationScope.periodScope.prepareNextPeriod()
        incomingClaims.clear()
        incomingClaims << claimDevelopment1 << claimDevelopment2
        contract.filterInChannels(contract.inClaims, incomingClaims)
        contract.doCalculation()
        assertEquals '# ceded claims packets', 1, contract.outCoveredClaims.size()
        assertEquals 'ceded incurred 0', 2.4d, contract.outCoveredClaims[0].incurred, 1E-10
        assertEquals 'ceded paid 0', 1.6, contract.outCoveredClaims[0].paid, 1E-10
        assertEquals 'ceded reserved 0', 0.8, contract.outCoveredClaims[0].reserved, 1E-10
        assertEquals 'origin of motor claim', originalClaim2, contract.outCoveredClaims[0].originalClaim
        contract.reset()
    }

    private ClaimDevelopmentLeanPacket getClaim(PerilMarker peril, LobMarker lob, double ultimate, double paid,
                                                double fractionOfPeriod, Component origin, Event event,
                                                Claim originalClaim) {
        ClaimDevelopmentLeanPacket claim = new ClaimDevelopmentLeanPacket(ultimate: ultimate, paid: paid,
                fractionOfPeriod: fractionOfPeriod, origin: origin, event: event, originalClaim: originalClaim)
        claim.addMarker(PerilMarker, peril)
        claim.addMarker(LobMarker, lob)
        claim
    }

    private Claim getClaim(PerilMarker peril, LobMarker lob, double ultimate, double fractionOfPeriod, ClaimType claimType) {
        Claim claim = new Claim(ultimate: ultimate, fractionOfPeriod: fractionOfPeriod, claimType: claimType)
        claim.addMarker(PerilMarker, peril)
        claim.addMarker(LobMarker, lob)
        claim
    }

    private Claim getClaim(PerilMarker peril, LobMarker lob, double ultimate) {
        Claim claim = new Claim(ultimate: ultimate)
        claim.addMarker(PerilMarker, peril)
        claim.addMarker(LobMarker, lob)
        claim
    }

    private Claim getClaim(IReserveMarker reserve, LobMarker lob, double ultimate) {
        Claim claim = new Claim(ultimate: ultimate)
        claim.addMarker(IReserveMarker, reserve)
        claim.addMarker(LobMarker, lob)
        claim
    }
}