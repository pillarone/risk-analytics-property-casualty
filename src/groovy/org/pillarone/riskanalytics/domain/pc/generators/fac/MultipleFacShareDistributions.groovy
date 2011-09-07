package org.pillarone.riskanalytics.domain.pc.generators.fac

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class MultipleFacShareDistributions extends DynamicComposedComponent {

    PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo)
    PacketList<FacShareAndRetention> outDistributionsByUwInfo = new PacketList<FacShareAndRetention>(FacShareAndRetention)

    @Override
    void wire() {
        replicateInChannels this, 'inUnderwritingInfo'
        replicateOutChannels this, 'outDistributionsByUwInfo'
    }

    @Override
    Component createDefaultSubComponent() {
        return new FacShareDistributions()
    }
}
