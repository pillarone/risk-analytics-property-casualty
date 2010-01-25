package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoPacketFactory

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class QuotaShareMultiSlidingScaleContractStrategy extends QuotaShareContractStrategy {

    static final ReinsuranceContractType type = ReinsuranceContractType.QUOTASHAREMULTISLIDINGSCALE

    AbstractMultiDimensionalParameter levels = new TableMultiDimensionalParameter([[0d], [0d]],
            ['claim level', 'rate level'])

    private double totalClaimValue
    private TreeMap<Double, Double> claimRateLevels

    ReinsuranceContractType getType() {
        type
    }

    Map getParameters() {
        ["quotaShare": quotaShare,
                "commission": commission,
                "coveredByReinsurer": coveredByReinsurer,
                "levels": levels
        ]
    }

    // todo(sku): this function should prepare the parameters only once for each period
    private void initParameterizationPerPeriod() {
        claimRateLevels = new TreeMap<Double, Double>()
        for (int row = 1; row < levels.rowCount; row++) {
            claimRateLevels.put((Double) levels.getValueAt(row, 0), (Double) levels.getValueAt(row, 1))
        }
    }

    void initBookKeepingFigures(List<Claim> inClaims, List<UnderwritingInfo> coverUnderwritingInfo) {
        initParameterizationPerPeriod()
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
        double claimRatio = totalClaimValue / cededPremiumOld
        Double rateLevel = claimRateLevels.get(claimRatio)
        if (rateLevel == null) {
            rateLevel = getRateLevel(claimRatio)
        }
        return cededPremiumOld * (1 - rateLevel)
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

    /**
     *  This functionality is necessary as long as we support Java 5.
     *  In Java 6 claimRateLevels.lowerEntry(claimRatio).value would do the same
     */
    private Double getRateLevel(double claimRatio) {
        TreeMap lowerMap = (TreeMap) claimRateLevels.headMap(claimRatio)
        return (Double) lowerMap.get(lowerMap.lastKey()).value
    }
}