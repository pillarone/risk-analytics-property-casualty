package org.pillarone.riskanalytics.domain.pc.claims.allocation;

import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.pc.allocators.AllocationTable;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 * @deprecated newer version available in domain.pc.claims package
 */
@Deprecated
public interface IRiskAllocatorStrategy {
    PacketList<Claim> getAllocatedClaims(List<Claim> claims, List<AllocationTable> targetDistribution, List<UnderwritingInfo> underwritingInfos);
}
