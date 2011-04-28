package org.pillarone.riskanalytics.domain.pc.claims

import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class TrivialRiskAllocatorStrategy extends AbstractParameterObject implements IRiskAllocatorStrategy {

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
