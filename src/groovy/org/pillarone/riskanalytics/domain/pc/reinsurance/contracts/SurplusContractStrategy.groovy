package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoPacketFactory

/**
 * @author martin.melchior (at) fhnw (dot) ch
 * @author Michael-Noe (at) Web (dot) de
 */
class SurplusContractStrategy extends AbstractContractStrategy implements IReinsuranceContractStrategy, IParameterObject {

    static final ReinsuranceContractType type = ReinsuranceContractType.SURPLUS

    /* Line/Maximum $L$ */
    double retention
    /* number of lines $#L$ */
    int lines
    /* surplus share for claims without sum insured information */
    double defaultCededLossShare

    ReinsuranceContractType getType() {
        type
    }

    public Map getParameters() {
        return ["retention": retention,
            "lines": lines,
            "defaultCededLossShare": defaultCededLossShare,
            "coveredByReinsurer": coveredByReinsurer]
    }

    public double allocateCededClaim(Claim inClaim) {
        if (inClaim.hasExposureInfo()) {
            return inClaim.ultimate * coveredByReinsurer * getFractionCeded(inClaim.exposure.sumInsured)
        }
        else {
            return inClaim.ultimate * coveredByReinsurer * defaultCededLossShare
        }
    }

    public void calculateCoverUnderwritingInfo(List<UnderwritingInfo> grossInfo, List<UnderwritingInfo> cededInfo) {
        grossInfo.each {UnderwritingInfo gross ->  cededInfo << calculateCoverUnderwritingInfo(gross) }
    }

    double getFractionCeded(double sumInsured) {
        if (sumInsured > 0) {
            return Math.min(Math.max(sumInsured - retention, 0), lines * retention) / sumInsured
        } else {
            return 0
        }
    }

    // todo: Are the definition for the as-if premium reasonable?
    UnderwritingInfo calculateCoverUnderwritingInfo(UnderwritingInfo grossUnderwritingInfo, double initialReserves) {
        UnderwritingInfo cededUnderwritingInfo = UnderwritingInfoPacketFactory.copy(grossUnderwritingInfo)
        cededUnderwritingInfo.originalUnderwritingInfo = grossUnderwritingInfo?.originalUnderwritingInfo ? grossUnderwritingInfo.originalUnderwritingInfo : grossUnderwritingInfo
        double fractionCeded = getFractionCeded(cededUnderwritingInfo.sumInsured)
        cededUnderwritingInfo.premiumWritten *= fractionCeded * coveredByReinsurer
        cededUnderwritingInfo.premiumWrittenAsIf *= fractionCeded * coveredByReinsurer
        cededUnderwritingInfo.sumInsured *= fractionCeded * coveredByReinsurer
        cededUnderwritingInfo.maxSumInsured *= fractionCeded * coveredByReinsurer
        cededUnderwritingInfo.commission = 0
        cededUnderwritingInfo
    }
}