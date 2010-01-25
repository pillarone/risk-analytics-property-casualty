package org.pillarone.riskanalytics.domain.pc.reserves.cashflow

import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class DynamicClaimDevelopment extends DynamicComposedComponent {

    PacketList<Claim> inClaims = new PacketList<Claim>(Claim);

    PacketList<Claim> outClaims = new PacketList<Claim>(Claim);
    PacketList<ClaimDevelopmentPacket> outClaimsDevelopment = new PacketList<ClaimDevelopmentPacket>(ClaimDevelopmentPacket);                             // todo(sku): remove as soon as PMO-648 is resolved
    PacketList<ClaimDevelopmentWithIBNRPacket> outClaimsDevelopmentWithIBNR = new PacketList<ClaimDevelopmentWithIBNRPacket>(ClaimDevelopmentWithIBNRPacket);     // todo(sku): remove as soon as PMO-648 is resolved


    public ClaimDevelopment createDefaultSubComponent() {
        ClaimDevelopment newComponent = new ClaimDevelopment(
                parmCoveredPerils : new ComboBoxTableMultiDimensionalParameter([], ['peril'], PerilMarker),
                parmPayoutPattern : PatternStrategyType.getStrategy(PatternStrategyType.NONE, [:]),
                parmReportedPattern : PatternStrategyType.getStrategy(PatternStrategyType.NONE, [:]),
                parmHistoricClaims : HistoricClaimsStrategyType.getStrategy(HistoricClaimsStrategyType.NONE, Collections.emptyMap()))
        return newComponent
    }

    public void wire() {
        replicateInChannels this, 'inClaims'
        replicateOutChannels this, 'outClaims'
        replicateOutChannels this, 'outClaimsDevelopment'
        replicateOutChannels this, 'outClaimsDevelopmentWithIBNR'
    }
}
