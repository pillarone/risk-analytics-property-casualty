package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoPacketFactory

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class QuotaShareSlidingScaleContractStrategy extends QuotaShareContractStrategy {

    static final ReinsuranceContractType type = ReinsuranceContractType.QUOTASHARESLIDINGSCALE

    double claimLevel1
    double claimLevel2
    double rateLevel1
    double rateLevel2
    double rateLevel3
    private double totalClaimValue

    ReinsuranceContractType getType() {
        type
    }

    Map getParameters() {
        ["quotaShare": quotaShare,
         "commission": commission,
         "coveredByReinsurer": coveredByReinsurer,
         "claimLevel1": claimLevel1,
         "claimLevel2": claimLevel2,
         "rateLevel1": rateLevel1,
         "rateLevel2": rateLevel2,
         "rateLevel3": rateLevel3]
    }

    void initBookKeepingFigures(List<Claim> inClaims, List<UnderwritingInfo> coverUnderwritingInfo) {
        totalClaimValue = 0
        for (Claim claim: inClaims) {
            totalClaimValue += claim.ultimate
        }
    }

    double calculateCoveredLoss(Claim inClaim) {
        inClaim.ultimate * quotaShare * coveredByReinsurer
    }

    private double calculateCededPremium(double premium) {
        double cededPremiumOld = premium * quotaShare * coveredByReinsurer
        double claimRate = totalClaimValue / cededPremiumOld
        if (claimRate < claimLevel1) {
            return cededPremiumOld * (1 - rateLevel1)
        }
        else if ((claimRate >= claimLevel1) && (claimRate < claimLevel2)) {
            return cededPremiumOld * (1 - rateLevel2)
        }
        return cededPremiumOld * (1 - rateLevel3)
    }

    UnderwritingInfo calculateCoverUnderwritingInfo(UnderwritingInfo grossUnderwritingInfo) {
        UnderwritingInfo cededUnderwritingInfo = UnderwritingInfoPacketFactory.copy(grossUnderwritingInfo)
        cededUnderwritingInfo.originalUnderwritingInfo = grossUnderwritingInfo?.originalUnderwritingInfo ? grossUnderwritingInfo.originalUnderwritingInfo : grossUnderwritingInfo
        cededUnderwritingInfo.premiumWritten = calculateCededPremium(grossUnderwritingInfo.premiumWritten)
        cededUnderwritingInfo.premiumWrittenAsIf = calculateCededPremium(grossUnderwritingInfo.premiumWrittenAsIf)
        cededUnderwritingInfo.sumInsured *= quotaShare * coveredByReinsurer
        cededUnderwritingInfo.maxSumInsured *= quotaShare * coveredByReinsurer
        cededUnderwritingInfo.commission = cededUnderwritingInfo.premiumWritten * commission * coveredByReinsurer
        cededUnderwritingInfo
    }
}