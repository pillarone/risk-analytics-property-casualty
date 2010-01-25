package org.pillarone.riskanalytics.domain.assets.parameterization

import org.pillarone.riskanalytics.core.wiring.PortReplicatorCategory as PRC

import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.assets.BondParameters

/**
 * @author cyril (dot) neyme (at) kpmg (dot) fr
 */
public class DynamicBonds extends DynamicComposedComponent {

    PacketList<BondParameters> outBondParameters = new PacketList(BondParameters)

    protected void doCalculation() {
        for (Bond bond: componentList) {
            bond.start()
        }
    }

    public void wire() {
        for (Bond bond: componentList) {
            doWire PRC, this, 'outBondParameters', bond, 'outBondParameters'
        }
    }

    public Bond createDefaultSubComponent() {
        return new Bond();
    }

    /**
     * Helper method for wiring when sender or receiver are determined dynamically
     */
    // todo(sku): move to a utility or super class
    public static void doWire(category, receiver, inChannelName, sender, outChannelName) {
        category.doSetProperty(receiver, inChannelName, category.doGetProperty(sender, outChannelName))
    }

    public String getGenericSubComponentName() {
        return "bond"
    }


}