package org.pillarone.riskanalytics.domain.pc.reinsurance.commissions

import org.pillarone.riskanalytics.core.wiring.WireCategory as WC
import org.pillarone.riskanalytics.core.wiring.PortReplicatorCategory as PRC

import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.claims.Claim

/**
 *  This component contains an arbitrary number of Commission subcomponents.
 *  It is designed to enable one to assign commissions to UWInfo packets on the basis of a partitioning
 *  of the packets into discrete, disjoint subgroups, where the partitioning is based on contract (UWInfo origin).
 *  An included Commission can apply to none, all, or a specified set of contracts.
 *
 *  Constraint: by convention/design, only one Commission may apply to each incoming UnderwritingInfo packet.
 *  Sufficient condition: each Commission applies to a mutually exclusive set of contracts.
 *  This is a design assumption, whose violation gives undefined results, which the user must therefore enforce.
 *
 *  The packets from inUnderwritingInfo go to one of the outUwInfo -Modified or -Unmodified channels. (Each modified packet got
 *  exactly one Commission, whereas unmodified packets were checked by but not applicable to any of the Commissions.)
 *  If you want the full packet stream, just wire the two outChannels together.
 *
 *  @author shartmann (at) munichre (dot) com, ben.ginsberg (at) intuitive-collaboration (dot) com
 */
class DynamicCommission extends DynamicComposedComponent {

    PacketList<Claim> inClaims = new PacketList<Claim>(Claim.class);
    PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);

    PacketList<UnderwritingInfo> outUnderwritingInfoModified = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    PacketList<UnderwritingInfo> outUnderwritingInfoUnmodified = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);

    void wire() {
        replicateInChannels this, 'inClaims'  // side-effect: sorts componentList for reproducibility
        cascadeUnmodifiedComponents 'inUnderwritingInfo', 'outUnderwritingInfoModified', 'outUnderwritingInfoUnmodified'
    }

    Component createDefaultSubComponent() {
        return new Commission(parmCommissionStrategy :
                    CommissionStrategyType.getStrategy(CommissionStrategyType.NOCOMMISSION, [:]))
    }

    /**
     * Cascade (wire) internal subcomponents' unprocessed in- & outUnderwritingInfo channels in series;
     * replicate the first inChannel, the last outChannel, and each processed/modified outChannel.
     * The wiring order is defined by the upper class which sorts by name.
     * 
     * Each inChannel packet going through each component is a "Bernoulli trial", depending on whether it gets modified.
     * Unmodified out-packets are routed (wired) to the next component's inChannel (with another chance to get modified).
     * Modified packets are routed (replicated) to the container component's outChannel (with no further processing).
     *
     * Only (at most) one subcomponent may process (be applicable to) any given packet.
     *
     * This method assumes that componentList has been already sorted, e.g., by a prior call to replicateInChannels.
     */
    protected void cascadeUnmodifiedComponents(String inChannel, String outModified, String outUnmodified) {
        Component lastComponent = null
        // assume componentList is sorted for reproducibility
        for (Component component: componentList) {
            if (lastComponent == null) {
                // replicate container's inChannel to first subcomponent
                doWire PRC, component, inChannel, this, inChannel
            }
            else {
                // cascade unmodified outChannels (wire in series) to subsequent subcomponents' inChannels
                doWire WC, component, inChannel, lastComponent, outUnmodified
                // route all modified outChannels (in parallel) to container component
                doWire PRC, this, outModified, lastComponent, outModified
            }
            lastComponent = component
        }
        if (lastComponent != null) {
            // replicate both of the last subcomponent's outChannels to the container
            doWire PRC, this, outModified, lastComponent, outModified
            doWire PRC, this, outUnmodified, lastComponent, outUnmodified
        } else {
            // trivial case (no subcomponents): wire input to output
            doWire WC, this, outUnmodified, this, inChannel
        }
    }
}
