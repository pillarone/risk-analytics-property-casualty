package org.pillarone.riskanalytics.domain.pc.reserves.fasttrack

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.PeriodStore
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.core.packets.PacketList

/**
 * @author shartmann (at) munichre (dot) com
 */

class ReservesGeneratorLeanTests extends GroovyTestCase {

    void testUsageInitialReserves() {
        ReservesGeneratorLean reservesGeneratorLean = new ReservesGeneratorLean(
                parmDistribution: DistributionType.getStrategy(DistributionType.CONSTANT,
                        ["constant": 1.2d]),
                parmModification: DistributionModifier.getStrategy(DistributionModifier.NONE, new HashMap()),
                parmPeriodPaymentPortion: 0.3d,
                parmInitialReserves: 100d,
                parmReservesModel: ReservesGeneratorStrategyType.getStrategy(
                        ReservesGeneratorStrategyType.INITIAL_RESERVES,
                        [basedOnClaimsGenerators: new ComboBoxTableMultiDimensionalParameter(
                                [], ["Claims Generators"], IPerilMarker)]));
        reservesGeneratorLean.periodScope = new PeriodScope();
        reservesGeneratorLean.periodScope.currentPeriod = 0
        reservesGeneratorLean.periodStore = new PeriodStore(reservesGeneratorLean.periodScope)


        reservesGeneratorLean.doCalculation()
        assertEquals '# packets, period 0', 1, reservesGeneratorLean.outClaimsDevelopment.size()
        assertEquals 'incurred, period 0', 120d, reservesGeneratorLean.outClaimsDevelopment[0].ultimate
        assertEquals 'paid, period 0', 36d, reservesGeneratorLean.outClaimsDevelopment[0].paid
        assertEquals 'reserved, period 0', 84d, reservesGeneratorLean.outClaimsDevelopment[0].reserved

        reservesGeneratorLean.periodScope.currentPeriod++
        reservesGeneratorLean.reset()
        reservesGeneratorLean.doCalculation()
        assertEquals '# packets, period 1', 1, reservesGeneratorLean.outClaimsDevelopment.size()
        assertEquals 'incurred, period 1', 120d, reservesGeneratorLean.outClaimsDevelopment[0].ultimate
        assertEquals 'paid, period 1', 36d, reservesGeneratorLean.outClaimsDevelopment[0].paid
        assertEquals 'reserved, period 1', 84d, reservesGeneratorLean.outClaimsDevelopment[0].reserved
    }

    void testUsageAbsoluteReserve() {
        ReservesGeneratorLean reservesGeneratorLean = new ReservesGeneratorLean(
                parmDistribution: DistributionType.getStrategy(DistributionType.CONSTANT,
                        ["constant": 200d]),
                parmModification: DistributionModifier.getStrategy(DistributionModifier.NONE, new HashMap()),
                parmPeriodPaymentPortion: 0.3d,
                parmInitialReserves: 100d,
                parmReservesModel: ReservesGeneratorStrategyType.getStrategy(
                        ReservesGeneratorStrategyType.ABSOLUTE,
                        [basedOnClaimsGenerators: new ComboBoxTableMultiDimensionalParameter(
                                [], ["Claims Generators"], IPerilMarker)]));
        reservesGeneratorLean.periodScope = new PeriodScope();
        reservesGeneratorLean.periodScope.currentPeriod = 0
        reservesGeneratorLean.periodStore = new PeriodStore(reservesGeneratorLean.periodScope)

        reservesGeneratorLean.doCalculation()
        assertEquals '# packets, period 0', 1, reservesGeneratorLean.outClaimsDevelopment.size()
        assertEquals 'incurred', 200d, reservesGeneratorLean.outClaimsDevelopment[0].ultimate
        assertEquals 'paid', 60d, reservesGeneratorLean.outClaimsDevelopment[0].paid
        assertEquals 'reserved', 140d, reservesGeneratorLean.outClaimsDevelopment[0].reserved

        reservesGeneratorLean.periodScope.currentPeriod++
        reservesGeneratorLean.reset()
        reservesGeneratorLean.doCalculation()
        assertEquals '# packets, period 1', 1, reservesGeneratorLean.outClaimsDevelopment.size()
        assertEquals 'incurred, period 1', 200d, reservesGeneratorLean.outClaimsDevelopment[0].ultimate
        assertEquals 'paid, period 1', 0.3 * 200d, reservesGeneratorLean.outClaimsDevelopment[0].paid
        assertEquals 'reserved, period 1', 0.7 * 200d, reservesGeneratorLean.outClaimsDevelopment[0].reserved
    }

    void testUsagePriorPeriod() {
        ReservesGeneratorLean reservesGeneratorLean = new ReservesGeneratorLean(
                parmDistribution: DistributionType.getStrategy(DistributionType.CONSTANT,
                        ["constant": 1.0d]),
                parmModification: DistributionModifier.getStrategy(DistributionModifier.NONE, new HashMap()),
                parmPeriodPaymentPortion: 0.3d,
                parmInitialReserves: 100d,
                parmReservesModel: ReservesGeneratorStrategyType.getStrategy(
                        ReservesGeneratorStrategyType.PRIOR_PERIOD,
                        [basedOnClaimsGenerators: new ComboBoxTableMultiDimensionalParameter(
                                [], ["Claims Generators"], IPerilMarker)]));
        reservesGeneratorLean.periodScope = new PeriodScope();
        reservesGeneratorLean.periodScope.currentPeriod = 0
        reservesGeneratorLean.periodStore = new PeriodStore(reservesGeneratorLean.periodScope)

        reservesGeneratorLean.doCalculation()
        assertEquals '# packets, period 0', 1, reservesGeneratorLean.outClaimsDevelopment.size()
        assertEquals 'incurred', 100d, reservesGeneratorLean.outClaimsDevelopment[0].ultimate
        assertEquals 'paid', 30d, reservesGeneratorLean.outClaimsDevelopment[0].paid
        assertEquals 'reserved', 70d, reservesGeneratorLean.outClaimsDevelopment[0].reserved

        reservesGeneratorLean.periodScope.currentPeriod++
        reservesGeneratorLean.reset()
        reservesGeneratorLean.doCalculation()
        assertEquals '# packets, period 1', 1, reservesGeneratorLean.outClaimsDevelopment.size()
        assertEquals 'incurred, period 1', 70d, reservesGeneratorLean.outClaimsDevelopment[0].ultimate
        assertEquals 'paid, period 1', 0.3 * 70d, reservesGeneratorLean.outClaimsDevelopment[0].paid
        assertEquals 'reserved, period 1', 0.7 * 70d, reservesGeneratorLean.outClaimsDevelopment[0].reserved
    }

    void testUsagePriorPeriodWithIncomingPackets() {
        ReservesGeneratorLean reservesGeneratorLean = new ReservesGeneratorLean(
                parmDistribution: DistributionType.getStrategy(DistributionType.CONSTANT,
                        ["constant": 1.0d]),
                parmModification: DistributionModifier.getStrategy(DistributionModifier.NONE, new HashMap()),
                parmPeriodPaymentPortion: 0.3d,
                parmInitialReserves: 100d,
                parmReservesModel: ReservesGeneratorStrategyType.getStrategy(
                        ReservesGeneratorStrategyType.PRIOR_PERIOD,
                        [basedOnClaimsGenerators: new ComboBoxTableMultiDimensionalParameter(
                                [], ["Claims Generators"], IPerilMarker)]));
        reservesGeneratorLean.periodScope = new PeriodScope();
        reservesGeneratorLean.periodScope.currentPeriod = 0
        reservesGeneratorLean.periodStore = new PeriodStore(reservesGeneratorLean.periodScope)

        reservesGeneratorLean.inClaims << new ClaimDevelopmentLeanPacket(paid: 300, ultimate: 1000)
        reservesGeneratorLean.inClaims << new ClaimDevelopmentLeanPacket(paid: 300, ultimate: 2000)
        reservesGeneratorLean.doCalculation()
        assertEquals '# packets, period 0', 1, reservesGeneratorLean.outClaimsDevelopment.size()
        assertEquals 'incurred', 100d, reservesGeneratorLean.outClaimsDevelopment[0].ultimate
        assertEquals 'paid', 30d, reservesGeneratorLean.outClaimsDevelopment[0].paid
        assertEquals 'reserved', 70d, reservesGeneratorLean.outClaimsDevelopment[0].reserved

        reservesGeneratorLean.periodScope.currentPeriod++
        reservesGeneratorLean.reset()
        reservesGeneratorLean.inClaims << new ClaimDevelopmentLeanPacket(paid: 300, ultimate: 1000)
        reservesGeneratorLean.inClaims << new ClaimDevelopmentLeanPacket(paid: 300, ultimate: 2000)
        reservesGeneratorLean.doCalculation()
        assertEquals '# packets, period 1', 1, reservesGeneratorLean.outClaimsDevelopment.size()
        assertEquals 'incurred, period 1', 2470d, reservesGeneratorLean.outClaimsDevelopment[0].ultimate
        assertEquals 'paid, period 1', 0.3 * 2470d, reservesGeneratorLean.outClaimsDevelopment[0].paid
        assertEquals 'reserved, period 1', 0.7 * 2470d, reservesGeneratorLean.outClaimsDevelopment[0].reserved
    }

    void testUsageAbsoluteReserveWithIncomingPackets() {
        ReservesGeneratorLean reservesGeneratorLean = new ReservesGeneratorLean(
                parmDistribution: DistributionType.getStrategy(DistributionType.CONSTANT,
                        ["constant": 200d]),
                parmModification: DistributionModifier.getStrategy(DistributionModifier.NONE, new HashMap()),
                parmPeriodPaymentPortion: 0.3d,
                parmInitialReserves: 100d,
                parmReservesModel: ReservesGeneratorStrategyType.getStrategy(
                        ReservesGeneratorStrategyType.ABSOLUTE,
                        [basedOnClaimsGenerators: new ComboBoxTableMultiDimensionalParameter(
                                [], ["Claims Generators"], IPerilMarker)]));
        reservesGeneratorLean.periodScope = new PeriodScope();
        reservesGeneratorLean.periodScope.currentPeriod = 0
        reservesGeneratorLean.periodStore = new PeriodStore(reservesGeneratorLean.periodScope)

        reservesGeneratorLean.inClaims << new ClaimDevelopmentLeanPacket(paid: 300, ultimate: 1000)
        reservesGeneratorLean.inClaims << new ClaimDevelopmentLeanPacket(paid: 300, ultimate: 2000)
        reservesGeneratorLean.doCalculation()
        assertEquals '# packets, period 0', 1, reservesGeneratorLean.outClaimsDevelopment.size()
        assertEquals 'incurred', 200d, reservesGeneratorLean.outClaimsDevelopment[0].ultimate
        assertEquals 'paid', 60d, reservesGeneratorLean.outClaimsDevelopment[0].paid
        assertEquals 'reserved', 140d, reservesGeneratorLean.outClaimsDevelopment[0].reserved

        reservesGeneratorLean.periodScope.currentPeriod++
        reservesGeneratorLean.reset()
        reservesGeneratorLean.inClaims << new ClaimDevelopmentLeanPacket(paid: 300, ultimate: 1000)
        reservesGeneratorLean.inClaims << new ClaimDevelopmentLeanPacket(paid: 300, ultimate: 2000)
        reservesGeneratorLean.doCalculation()
        assertEquals '# packets, period 1', 1, reservesGeneratorLean.outClaimsDevelopment.size()
        assertEquals 'incurred, period 1', 200d, reservesGeneratorLean.outClaimsDevelopment[0].ultimate
        assertEquals 'paid, period 1', 0.3 * 200d, reservesGeneratorLean.outClaimsDevelopment[0].paid, 1E-8
        assertEquals 'reserved, period 1', 0.7 * 200d, reservesGeneratorLean.outClaimsDevelopment[0].reserved, 1E-8
    }

    void testUsageInitialReservesWithIncomingPackets() {
        ReservesGeneratorLean reservesGeneratorLean = new ReservesGeneratorLean(
                parmDistribution: DistributionType.getStrategy(DistributionType.CONSTANT,
                        ["constant": 1.0d]),
                parmModification: DistributionModifier.getStrategy(DistributionModifier.NONE, new HashMap()),
                parmPeriodPaymentPortion: 0.3d,
                parmInitialReserves: 100d,
                parmReservesModel: ReservesGeneratorStrategyType.getStrategy(
                        ReservesGeneratorStrategyType.INITIAL_RESERVES,
                        [basedOnClaimsGenerators: new ComboBoxTableMultiDimensionalParameter(
                                [], ["Claims Generators"], IPerilMarker)]));
        reservesGeneratorLean.periodScope = new PeriodScope();
        reservesGeneratorLean.periodScope.currentPeriod = 0
        reservesGeneratorLean.periodStore = new PeriodStore(reservesGeneratorLean.periodScope)

        reservesGeneratorLean.inClaims << new ClaimDevelopmentLeanPacket(paid: 300, ultimate: 1000)
        reservesGeneratorLean.inClaims << new ClaimDevelopmentLeanPacket(paid: 300, ultimate: 2000)
        reservesGeneratorLean.doCalculation()
        assertEquals '# packets, period 0', 1, reservesGeneratorLean.outClaimsDevelopment.size()
        assertEquals 'incurred', 100d, reservesGeneratorLean.outClaimsDevelopment[0].ultimate
        assertEquals 'paid', 30d, reservesGeneratorLean.outClaimsDevelopment[0].paid
        assertEquals 'reserved', 70d, reservesGeneratorLean.outClaimsDevelopment[0].reserved

        reservesGeneratorLean.periodScope.currentPeriod++
        reservesGeneratorLean.reset()
        reservesGeneratorLean.inClaims << new ClaimDevelopmentLeanPacket(paid: 300, ultimate: 1000)
        reservesGeneratorLean.inClaims << new ClaimDevelopmentLeanPacket(paid: 300, ultimate: 2000)
        reservesGeneratorLean.doCalculation()
        assertEquals '# packets, period 1', 1, reservesGeneratorLean.outClaimsDevelopment.size()
        assertEquals 'incurred, period 1', 100d, reservesGeneratorLean.outClaimsDevelopment[0].ultimate
        assertEquals 'paid, period 1', 0.3 * 100d, reservesGeneratorLean.outClaimsDevelopment[0].paid
        assertEquals 'reserved, period 1', 0.7 * 100d, reservesGeneratorLean.outClaimsDevelopment[0].reserved
    }

    void testUsagePriorPeriodWithIncomingFilteredPackets() {
        ReservesGeneratorLean reservesGeneratorLean = new ReservesGeneratorLean(
                parmDistribution: DistributionType.getStrategy(DistributionType.CONSTANT,
                        ["constant": 1.0d]),
                parmModification: DistributionModifier.getStrategy(DistributionModifier.NONE, new HashMap()),
                parmPeriodPaymentPortion: 0.3d,
                parmInitialReserves: 100d,
                parmReservesModel: ReservesGeneratorStrategyType.getStrategy(
                        ReservesGeneratorStrategyType.PRIOR_PERIOD, [basedOnClaimsGenerators:
                        new ComboBoxTableMultiDimensionalParameter(["claims generator"], ["Claims Generators"], IPerilMarker)
                        ]));
        reservesGeneratorLean.periodScope = new PeriodScope();
        reservesGeneratorLean.periodScope.currentPeriod = 0
        reservesGeneratorLean.periodStore = new PeriodStore(reservesGeneratorLean.periodScope)

        TestClaimsGenerator claimsGenerator1 = new TestClaimsGenerator(name: 'claimsGenerator')
        reservesGeneratorLean.parmReservesModel.basedOnClaimsGenerators.comboBoxValues = ['claims generator': claimsGenerator1]
        TestClaimsGenerator claimsGenerator2 = new TestClaimsGenerator(name: 'attrClaimsGenerator')

        PacketList<Claim> incomingClaims = new PacketList<Claim>(Claim)
        incomingClaims << getClaim(claimsGenerator1, 1000, 300) << getClaim(claimsGenerator2, 2000, 300)
        reservesGeneratorLean.filterInChannel(reservesGeneratorLean.inClaims, incomingClaims)
        reservesGeneratorLean.doCalculation()
        assertEquals '# packets, period 0', 1, reservesGeneratorLean.outClaimsDevelopment.size()
        assertEquals 'incurred', 100d, reservesGeneratorLean.outClaimsDevelopment[0].ultimate
        assertEquals 'paid', 30d, reservesGeneratorLean.outClaimsDevelopment[0].paid
        assertEquals 'reserved', 70d, reservesGeneratorLean.outClaimsDevelopment[0].reserved

        reservesGeneratorLean.periodScope.currentPeriod++
        reservesGeneratorLean.reset()

        incomingClaims = new PacketList<Claim>(Claim)
        incomingClaims << getClaim(claimsGenerator1, 1000, 300) << getClaim(claimsGenerator2, 2000, 300)
        reservesGeneratorLean.filterInChannel(reservesGeneratorLean.inClaims, incomingClaims)

        reservesGeneratorLean.doCalculation()
        assertEquals '# packets, period 1', 1, reservesGeneratorLean.outClaimsDevelopment.size()
        assertEquals 'incurred, period 1', 770d, reservesGeneratorLean.outClaimsDevelopment[0].ultimate
        assertEquals 'paid, period 1', 0.3 * 770d, reservesGeneratorLean.outClaimsDevelopment[0].paid
        assertEquals 'reserved, period 1', 0.7 * 770d, reservesGeneratorLean.outClaimsDevelopment[0].reserved
    }

    void testUsageAbsoluteReserveWithIncomingFilteredPackets() {
        ReservesGeneratorLean reservesGeneratorLean = new ReservesGeneratorLean(
                parmDistribution: DistributionType.getStrategy(DistributionType.CONSTANT,
                        ["constant": 200d]),
                parmModification: DistributionModifier.getStrategy(DistributionModifier.NONE, new HashMap()),
                parmPeriodPaymentPortion: 0.3d,
                parmInitialReserves: 100d,
                parmReservesModel: ReservesGeneratorStrategyType.getStrategy(
                        ReservesGeneratorStrategyType.ABSOLUTE, [basedOnClaimsGenerators:
                        new ComboBoxTableMultiDimensionalParameter(["claims generator"], ["Claims Generators"], IPerilMarker)
                        ]));
        reservesGeneratorLean.periodScope = new PeriodScope();
        reservesGeneratorLean.periodScope.currentPeriod = 0
        reservesGeneratorLean.periodStore = new PeriodStore(reservesGeneratorLean.periodScope)

        TestClaimsGenerator claimsGenerator1 = new TestClaimsGenerator(name: 'claimsGenerator')
        reservesGeneratorLean.parmReservesModel.basedOnClaimsGenerators.comboBoxValues = ['claims generator': claimsGenerator1]
        TestClaimsGenerator claimsGenerator2 = new TestClaimsGenerator(name: 'attrClaimsGenerator')

        reservesGeneratorLean.inClaims << getClaim(claimsGenerator1, 1000, 300)
        reservesGeneratorLean.inClaims << getClaim(claimsGenerator2, 2000, 300)
        reservesGeneratorLean.doCalculation()
        assertEquals '# packets, period 0', 1, reservesGeneratorLean.outClaimsDevelopment.size()
        assertEquals 'incurred', 200d, reservesGeneratorLean.outClaimsDevelopment[0].ultimate
        assertEquals 'paid', 60d, reservesGeneratorLean.outClaimsDevelopment[0].paid
        assertEquals 'reserved', 140d, reservesGeneratorLean.outClaimsDevelopment[0].reserved

        reservesGeneratorLean.periodScope.currentPeriod++
        reservesGeneratorLean.reset()
        reservesGeneratorLean.inClaims << getClaim(claimsGenerator1, 1000, 300)
        reservesGeneratorLean.inClaims << getClaim(claimsGenerator2, 2000, 300)
        reservesGeneratorLean.doCalculation()
        assertEquals '# packets, period 1', 1, reservesGeneratorLean.outClaimsDevelopment.size()
        assertEquals 'incurred, period 1', 200d, reservesGeneratorLean.outClaimsDevelopment[0].ultimate
        assertEquals 'paid, period 1', 0.3 * 200d, reservesGeneratorLean.outClaimsDevelopment[0].paid
        assertEquals 'reserved, period 1', 0.7 * 200d, reservesGeneratorLean.outClaimsDevelopment[0].reserved
    }

    private static ClaimDevelopmentLeanPacket getClaim(IPerilMarker peril, double ultimate, double paid) {
        ClaimDevelopmentLeanPacket claim = new ClaimDevelopmentLeanPacket(ultimate: ultimate, paid: paid)
        claim.addMarker(IPerilMarker, peril)
        claim
    }
}

class TestClaimsGenerator extends Component implements IPerilMarker {

    protected void doCalculation() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}
