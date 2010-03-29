package org.pillarone.riskanalytics.domain.pc.claims

import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class TrivialRiskAllocatorStrategy implements IRiskAllocatorStrategy, IParameterObject {

    public PacketList<Claim> getAllocatedClaims(List<Claim> claims, List<UnderwritingInfo> underwritingInfos) {
        return claims
    }

    public IParameterObjectClassifier getType() {
        return RiskAllocatorType.NONE
    }

    public Map getParameters() {
        return [:]
    }

}