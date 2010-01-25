package org.pillarone.riskanalytics.domain.pc.claims;

import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IRiskAllocatorStrategy {
    PacketList<Claim> getAllocatedClaims(List<Claim> claims, List<UnderwritingInfo> underwritingInfos);
}