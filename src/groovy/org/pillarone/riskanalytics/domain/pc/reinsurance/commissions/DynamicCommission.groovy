package org.pillarone.riskanalytics.domain.pc.reinsurance.commissions

import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.claims.Claim

/**
 * @author shartmann (at) munichre (dot) com
 */
class DynamicCommission extends DynamicComposedComponent{

    PacketList<Claim> inClaims = new PacketList<Claim>(Claim.class);
    PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);

    PacketList<UnderwritingInfo> outUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);

    void wire() {
        replicateInChannels this, 'inClaims'
        replicateInChannels this, 'inUnderwritingInfo'
        replicateOutChannels this, 'outUnderwritingInfo'
    }

    Component createDefaultSubComponent() {
        return new Commission(parmCommissionStrategy :
                    CommissionStrategyType.getStrategy(CommissionStrategyType.NOCOMMISSION, [:]))
    }
}
