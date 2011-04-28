package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfoPacketFactory
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfo

/**
 * @author martin.melchior (at) fhnw (dot) ch
 * @author Michael-Noe (at) Web (dot) de
 */
class SurplusContractStrategy extends AbstractContractStrategy implements IReinsuranceContractStrategy {

    static final ReinsuranceContractType type = ReinsuranceContractType.SURPLUS

    /* Line/Maximum $L$ */
    double retention
    /* number of lines $#L$ */
    double lines
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
            if (inClaim.ultimate > inClaim.exposure.sumInsured) {
                // handle a total loss according to https://issuetracking.intuitive-collaboration.com/jira/browse/PMO-1261
                return inClaim.ultimate * getFractionCeded(inClaim.ultimate)
            }
            else {
                return inClaim.ultimate * getFractionCeded(inClaim.exposure.sumInsured)
            }
        }
        else {
            return inClaim.ultimate * defaultCededLossShare
        }
    }

    public void calculateCoverUnderwritingInfo(List<UnderwritingInfo> grossInfo, List<UnderwritingInfo> cededInfo) {
        grossInfo.each {UnderwritingInfo gross ->  cededInfo << calculateCoverUnderwritingInfo(gross) }
    }

    double getFractionCeded(double sumInsured) {
        if (sumInsured > 0) {
            return Math.min(Math.max(sumInsured - retention, 0), lines * retention) / sumInsured
        }
        else {
            return 0
        }
    }

    // todo: Are the definition for the as-if premium reasonable?

    CededUnderwritingInfo calculateCoverUnderwritingInfo(UnderwritingInfo grossUnderwritingInfo, double initialReserves) {
        CededUnderwritingInfo cededUnderwritingInfo = CededUnderwritingInfoPacketFactory.copy(grossUnderwritingInfo)
        cededUnderwritingInfo.originalUnderwritingInfo = grossUnderwritingInfo?.originalUnderwritingInfo ? grossUnderwritingInfo.originalUnderwritingInfo : grossUnderwritingInfo
        double fractionCeded = getFractionCeded(cededUnderwritingInfo.sumInsured)
        cededUnderwritingInfo.premium *= fractionCeded
        cededUnderwritingInfo.sumInsured *= fractionCeded
        cededUnderwritingInfo.maxSumInsured *= fractionCeded
        cededUnderwritingInfo.commission = 0
        cededUnderwritingInfo.fixedCommission = 0
        cededUnderwritingInfo.variableCommission = 0
        cededUnderwritingInfo.fixedPremium = cededUnderwritingInfo.premium
        cededUnderwritingInfo.variablePremium = 0
        cededUnderwritingInfo
    }
}
