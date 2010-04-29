package org.pillarone.riskanalytics.domain.pc.underwriting

import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList

/**
 * <p>DynamicUnderwritingSegments stores any number of RiskBands components
 * (in the componentList of its parent class, DynamicComposedComponent)
 * and replicates their output (each risk band's properties in a distinct packet)
 * to its outUnderwritingInfo channel.</p>
 *
 * <p>The number of output packets will be the total number of risk bands (rows)
 * defined in all of the included RiskBands components. The origin property
 * of each packet will define the RiskBands component that the band belongs to,
 * and the originalUnderwritingInfo property will be unique to each packet.</p>
 *
 * <p>The parent class, DynamicComposedComponent, provides facilities to manage
 * the included components and their properties.</p>
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class DynamicUnderwritingSegments extends DynamicComposedComponent {

    PacketList<UnderwritingInfo> outUnderwritingInfo = new PacketList(UnderwritingInfo)

    protected void doCalculation() {
        for (RiskBands underwritingSegment: componentList) {
            underwritingSegment.start()
        }
    }

    public void wire() {
        replicateOutChannels this, 'outUnderwritingInfo'
    }

    public RiskBands createDefaultSubComponent() {
        return new RiskBands();
    }

    public String getGenericSubComponentName() {
        return "underwritingSegments"
    }
}