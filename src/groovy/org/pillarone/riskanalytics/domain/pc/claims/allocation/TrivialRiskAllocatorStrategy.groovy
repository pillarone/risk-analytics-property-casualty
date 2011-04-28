package org.pillarone.riskanalytics.domain.pc.claims.allocation

import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.domain.pc.allocators.AllocationTable
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject

/**
 * @deprecated newer version available in domain.pc.claims package
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@Deprecated
class TrivialRiskAllocatorStrategy extends AbstractParameterObject implements IRiskAllocatorStrategy {

    public PacketList<UnderwritingInfo> updateSumInsured(List<UnderwritingInfo> underwritingInfos) {
        return underwritingInfos
    }

    public PacketList<Claim> getAllocatedClaims(List<Claim> claims, List<AllocationTable> targetDistribution, List<UnderwritingInfo> underwritingInfos) {
        return claims
    }

    public IParameterObjectClassifier getType() {
        return RiskAllocatorType.NONE
    }

    public Map getParameters() {
        return [:]
    }

}
