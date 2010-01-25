package org.pillarone.riskanalytics.domain.pc.aggregators

import org.pillarone.riskanalytics.core.components.ComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.wiring.PortReplicatorCategory
import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingResult

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class UnderwritingResultAllocated extends ComposedComponent {

    PacketList<Claim> inClaims = new PacketList(Claim)
    PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList(UnderwritingInfo)

    PacketList<Claim> outClaims = new PacketList(Claim)
    PacketList<UnderwritingInfo> outUnderwritingInfo = new PacketList(UnderwritingInfo)
    PacketList<UnderwritingResult> outUnderwritingResult = new PacketList(UnderwritingResult)

    ClaimsFilter subClaimsFilter
    UnderwritingInfoFilter subUnderwritingInfoFilter
    UnderwritingResultCalculator subUnderwritingInfoResult

    UnderwritingResultAllocated() {
        subClaimsFilter = new ClaimsFilter()
        subUnderwritingInfoFilter = new UnderwritingInfoFilter()
        subUnderwritingInfoResult = new UnderwritingResultCalculator()
    }

    public void wire() {
        WiringUtils.use(PortReplicatorCategory) {
            subClaimsFilter.inClaims = this.inClaims
            subUnderwritingInfoFilter.inUnderwritingInfo = this.inUnderwritingInfo
            this.outUnderwritingResult = subUnderwritingInfoResult.outUnderwritingResult
        }
        WiringUtils.use(WireCategory) {
            subUnderwritingInfoResult.inClaims = subClaimsFilter.outClaims
            subUnderwritingInfoResult.inUnderwritingInfo = subUnderwritingInfoFilter.outUnderwritingInfo
        }
    }
}