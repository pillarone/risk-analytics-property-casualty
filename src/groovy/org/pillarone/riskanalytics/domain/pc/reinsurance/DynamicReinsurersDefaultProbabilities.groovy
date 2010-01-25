package org.pillarone.riskanalytics.domain.pc.reinsurance

import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.creditrisk.DefaultProbabilities
import org.pillarone.riskanalytics.domain.pc.creditrisk.ReinsurerDefault

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class DynamicReinsurersDefaultProbabilities extends DynamicComposedComponent {

    PacketList<DefaultProbabilities> inDefaultProbability = new PacketList(DefaultProbabilities.class);
    PacketList<ReinsurerDefault> outReinsurersDefault = new PacketList(ReinsurerDefault.class);

    public void wire() {
        replicateInChannels this, 'inDefaultProbability'
        replicateOutChannels this, 'outReinsurersDefault'
    }

    public ReinsurerDefaultProbability createDefaultSubComponent() {
        ReinsurerDefaultProbability reinsurerDefaultProbability = new ReinsurerDefaultProbability()
        return reinsurerDefaultProbability;
    }

    public String getGenericSubComponentName() {
        return "reinsurerDefaultProbability"
    }
}