package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoPacketFactory
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfoPacketFactory
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfo

/**
 * @author martin.melchior (at) fhnw (dot) ch
 * @author Michael-Noe (at) Web (dot) de
 */
class ReverseSurplusContractStrategy extends SurplusContractStrategy implements IReinsuranceContractStrategy, IParameterObject {

    static final ReinsuranceContractType type = ReinsuranceContractType.SURPLUSREVERSE

    ReinsuranceContractType getType() {
        type
    }

    public double allocateCededClaim(Claim inClaim) {
        if (inClaim.hasExposureInfo()) {
            return inClaim.ultimate * (1 - getFractionCeded(inClaim.exposure.sumInsured))
        }
        else {
            return inClaim.ultimate * (1 - defaultCededLossShare)
        }
    }

    CededUnderwritingInfo calculateCoverUnderwritingInfo(UnderwritingInfo grossUnderwritingInfo, double initialReserves) {
        CededUnderwritingInfo cededUnderwritingInfo = CededUnderwritingInfoPacketFactory.copy(grossUnderwritingInfo)
        cededUnderwritingInfo.originalUnderwritingInfo = grossUnderwritingInfo?.originalUnderwritingInfo ? grossUnderwritingInfo.originalUnderwritingInfo : grossUnderwritingInfo
        double fractionCeded = 1 - getFractionCeded(cededUnderwritingInfo.sumInsured)
        cededUnderwritingInfo.premium *= fractionCeded
        cededUnderwritingInfo.sumInsured *= fractionCeded
        cededUnderwritingInfo.maxSumInsured *= fractionCeded
        cededUnderwritingInfo.commission = 0
        cededUnderwritingInfo.fixedCommission = 0d
        cededUnderwritingInfo.variableCommission = 0d
        cededUnderwritingInfo.variablePremium = 0d
        cededUnderwritingInfo.fixedPremium = cededUnderwritingInfo.premium

        cededUnderwritingInfo
    }
}