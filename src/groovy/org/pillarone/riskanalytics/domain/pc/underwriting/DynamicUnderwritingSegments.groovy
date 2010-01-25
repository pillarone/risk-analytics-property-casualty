package org.pillarone.riskanalytics.domain.pc.underwriting

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 *
 * Dynamische Komponente zum Anlegen von UnderwritingSegments mittels UI.
 * Die Segmente werden über DynamicComposedComponent gespreichert.
 * Zulässige Subkomponente: RiskBands
 * 
 */
public class DynamicUnderwritingSegments extends DynamicComposedComponent {

    static Log LOG = LogFactory.getLog(PremiumWrittenExposureBaseStrategy.class);

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