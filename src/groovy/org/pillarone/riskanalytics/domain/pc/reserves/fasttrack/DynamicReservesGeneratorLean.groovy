package org.pillarone.riskanalytics.domain.pc.reserves.fasttrack

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.packets.SingleValuePacket
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.DistributionType

/**
 * @author shartmann (at) munichre (dot) com
 */

public class DynamicReservesGeneratorLean extends DynamicComposedComponent {

    PacketList<Claim> inClaims = new PacketList<Claim>(Claim);

    PacketList<Claim> outClaimsDevelopment = new PacketList<Claim>(Claim);
    PacketList<SingleValuePacket> outInitialReserves = new PacketList<SingleValuePacket>(SingleValuePacket);

    // todo(sku): remove the following and related lines as soon as PMO-648 is resolved
    PacketList<ClaimDevelopmentLeanPacket> outClaimsLeanDevelopment = new PacketList<ClaimDevelopmentLeanPacket>(ClaimDevelopmentLeanPacket);


    public Component createDefaultSubComponent() {
        return new ReservesGeneratorLean(
                parmDistribution : DistributionType.getStrategy(DistributionType.CONSTANT,
                                    ["constant" : 0d]),
                parmModification : DistributionModifier.getStrategy(DistributionModifier.NONE, new HashMap()),
                parmPeriodPaymentPortion : 0d,
                parmInitialReserves : 0d,
                parmReservesModel : ReservesGeneratorStrategyType.getStrategy(
                                    ReservesGeneratorStrategyType.INITIAL_RESERVES,
                                    ['basedOnClaimsGenerators' : new ComboBoxTableMultiDimensionalParameter([],["Claims Generators"], PerilMarker)],
                ));
    }

    public void wire() {
        replicateInChannels this, 'inClaims'
        replicateOutChannels this, 'outClaimsDevelopment'
        replicateOutChannels this, 'outClaimsLeanDevelopment'
        replicateOutChannels this, 'outInitialReserves'
    }

}