package org.pillarone.riskanalytics.domain.pc.claims.allocation

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.allocators.AllocationTable

/**
 * @deprecated newer version available in domain.pc.claims package 
 *
 * @author martin.melchior (at) fhnw (dot) ch
 */

// TODO event claims not (yet) considered
// TODO discuss whether for the attritional losses it would be sufficient to first do an aggregation
// TODO discuss: the order of the outclaims is no longer the same as the claims!
@Deprecated
class RiskAllocator extends Component {
    /** Defines the kind of allocation and parameterization       */
    IRiskAllocatorStrategy parmRiskAllocatorStrategy = RiskAllocatorStrategyFactory.getAllocatorStrategy(RiskAllocatorType.NONE, [:])

    /** The claims that should be associated with underlying exposure.           */
    PacketList<Claim> inClaims = new PacketList(Claim)

    /** Will contain the claims, filtered for the specified type, complemented with the associated exposure info.           */
    PacketList<Claim> outClaims = new PacketList(Claim)

    /**
     * The list of ExposureInfo packets define the risk profile. At the moment, no checks are done nor a merging of bands is done.
     * Hence, we assume that the list provided consists of of packets with (si,maxSi)-pairs that are 'non-overlapping'
     */
    PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList(UnderwritingInfo)
    PacketList<UnderwritingInfo> outUnderwritingInfo = new PacketList(UnderwritingInfo)

    /** The target allocation - either expressed as a number of claims per exposure pocket (in case of individual claims)
     * or as teh aggregate claims per exposure pocket (attritional claims).          */
    PacketList<AllocationTable> inTargetDistribution = new PacketList(AllocationTable)

    public void doCalculation() {
        outUnderwritingInfo.addAll inUnderwritingInfo
        outClaims.addAll parmRiskAllocatorStrategy.getAllocatedClaims(inClaims, inTargetDistribution, inUnderwritingInfo)
    }
}