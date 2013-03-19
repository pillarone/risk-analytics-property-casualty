package org.pillarone.riskanalytics.domain.pc.aggregators

import org.pillarone.riskanalytics.core.components.ComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.wiring.PortReplicatorCategory
import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.core.components.ComponentCategory

/**
 *  The claims aggregator sums up the gross and ceded ultimates and calculates the net value.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@ComponentCategory(categories = ['CLAIM','FILTER','AGGREGATOR'])
public class ClaimsFilterAggregator extends ComposedComponent {

    PacketList<Claim> inClaimsCeded = new PacketList(Claim);
    PacketList<Claim> inClaimsGross = new PacketList(Claim);
    PacketList<Claim> outClaimsCeded = new PacketList(Claim);
    PacketList<Claim> outClaimsGross = new PacketList(Claim);
    PacketList<Claim> outClaimsNet = new PacketList(Claim);

    ClaimsFilter subClaimsGrossFilter = new ClaimsFilter();
    ClaimsFilter subClaimsCededFilter = new ClaimsFilter();
    ClaimsAggregator subClaimsAggregator = new ClaimsAggregator();

    public void wire() {
        WiringUtils.use(WireCategory) {
            subClaimsAggregator.inClaimsGross = subClaimsGrossFilter.outClaims
            subClaimsAggregator.inClaimsCeded = subClaimsCededFilter.outClaims
        }
        WiringUtils.use(PortReplicatorCategory) {
            subClaimsGrossFilter.inClaims = this.inClaimsGross
            subClaimsCededFilter.inClaims = this.inClaimsCeded
            this.outClaimsGross = subClaimsAggregator.outClaimsGross
            this.outClaimsCeded = subClaimsAggregator.outClaimsCeded
            this.outClaimsNet = subClaimsAggregator.outClaimsNet
        }
    }
}