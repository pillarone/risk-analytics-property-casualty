package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow

import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter

import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoPacketFactory
import org.pillarone.riskanalytics.domain.pc.reserves.cashflow.ClaimDevelopmentPacket

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
abstract class XLContractStrategy extends AbstractContractStrategy implements IReinsuranceContractStrategy {

    /** Premium can be expressed as a fraction of a base quantity.                 */
    PremiumBase premiumBase = PremiumBase.ABSOLUTE

    /** Premium as a percentage of the premium base                 */
    double premium

    /** As a percentage of premium.                 */
    AbstractMultiDimensionalParameter reinstatementPremiums = new TableMultiDimensionalParameter([0d], ['Reinstatement Premium'])
    double attachmentPoint
    double limit
    protected double annualLimit
    protected double termLimit
    Map<UnderwritingInfo, Double> grossPremiumSharesPerBand = [:]

    protected double availableAnnualLimit
    protected double availableTermLimit
    /** available for whole term */
    protected double reinstatements
    protected double priorPeriodUsedReinstatements
    private boolean firstCoveredPeriod

    public Map getParameters() {
        return ["premiumBase": premiumBase,
            "premium": premium,
            "reinstatementPremiums": reinstatementPremiums,
            "attachmentPoint": attachmentPoint,
            "limit": limit,
            "annualLimit": annualLimit,
            "termLimit": termLimit]
    }

    abstract ReinsuranceContractType getType()

    protected double calculateUsedReinstatements() {
        double totallyUsedReinstatements = Math.min((termLimit - availableTermLimit) / limit, reinstatements)  // todo(sku): discuss with mst if reinstatements are based on incurred or paid
        return totallyUsedReinstatements - priorPeriodUsedReinstatements
    }

    protected double calculateReinstatementPremiums(double coveredByReinsurer) {
        double usedReinstatements = calculateUsedReinstatements()
        double reinstatementPremium = 0d
        for (int i = 1; i <= usedReinstatements; i++) {
            reinstatementPremium += getReinstatementPremiumFactor(i)
        }
        double partialReinstatement = usedReinstatements - Math.floor(usedReinstatements)
        reinstatementPremium += partialReinstatement * getReinstatementPremiumFactor(Math.ceil(usedReinstatements).toInteger(), coveredByReinsurer)
        reinstatementPremium
    }

    protected double getReinstatementPremiumFactor(int reinstatement, double coveredByReinsurer) {
        reinstatementPremiums.values[Math.min(reinstatement, reinstatementPremiums.valueRowCount - 1)] * coveredByReinsurer
    }

    void initBookKeepingFiguresForIteration(List<Claim> grossClaims, List<UnderwritingInfo> grossUnderwritingInfos) {
        grossPremiumSharesPerBand.clear()
        firstCoveredPeriod = true
        availableTermLimit = termLimit
        availableAnnualLimit = annualLimit
        reinstatements = termLimit / limit - 1
    }

    void initBookKeepingFiguresOfPeriod(List<Claim> grossClaims, List<UnderwritingInfo> grossUnderwritingInfos, double coveredByReinsurer) {
        double totalPremium = grossUnderwritingInfos.premium.sum()
        for (UnderwritingInfo underwritingInfo: grossUnderwritingInfos) {
            grossPremiumSharesPerBand.put(underwritingInfo, underwritingInfo.premium / totalPremium)
        }
        priorPeriodUsedReinstatements = calculateUsedReinstatements()
        double usedAnnualLimitInPriorPeriod = annualLimit - availableAnnualLimit
        availableTermLimit -= usedAnnualLimitInPriorPeriod
    }

    void applyAnnualLimits() {
        availableAnnualLimit = Math.min(annualLimit, availableTermLimit)
    }

    protected double calculateCededIncurredValueAndUpdateCumulativeClaims(ClaimDevelopmentPacket cumulativeGross, ClaimDevelopmentPacket cumulativeCeded, double coveredByReinsurer) {
        if (availableAnnualLimit > 0) {
            double ceded = Math.min(Math.max(cumulativeGross.incurred - attachmentPoint, 0), limit) * coveredByReinsurer
            ceded = Math.min(ceded, availableAnnualLimit)
            availableAnnualLimit -= ceded
            cumulativeCeded.incurred += ceded
            return ceded
        }
        return 0
    }

    protected double calculateCededPaidValueAndUpdateCumulativeClaims(ClaimDevelopmentPacket cumulativeGross, ClaimDevelopmentPacket cumulativeCeded, double coveredByReinsurer) {
        if (cumulativeCeded.paid < cumulativeCeded.incurred) {
            double ceded = Math.min(Math.max(cumulativeGross.paid - attachmentPoint, 0), limit) * coveredByReinsurer
            // paid ceded may not exceed incurred ceded
            ceded = Math.min(ceded, cumulativeCeded.incurred) - cumulativeCeded.paid
            cumulativeCeded.paid += ceded
            return ceded
        }
        return 0
    }

    protected double calculateCoveredUltimate(Claim inClaim, double coveredByReinsurer) {
        if (availableAnnualLimit > 0) {
            double ceded = Math.min(Math.max(inClaim.ultimate - attachmentPoint, 0), limit) * coveredByReinsurer
            ceded = Math.min(ceded, availableAnnualLimit)
            availableAnnualLimit -= ceded
            return ceded
        }
        else {
            return 0d
        }
    }

    UnderwritingInfo calculateCoverUnderwritingInfo(UnderwritingInfo grossUnderwritingInfo, double coveredByReinsurer) {
        UnderwritingInfo cededUnderwritingInfo = UnderwritingInfoPacketFactory.copy(grossUnderwritingInfo)
        cededUnderwritingInfo.originalUnderwritingInfo = grossUnderwritingInfo?.originalUnderwritingInfo ? grossUnderwritingInfo.originalUnderwritingInfo : grossUnderwritingInfo
        cededUnderwritingInfo.commission = 0d
        switch (premiumBase) {
            case PremiumBase.ABSOLUTE:
                cededUnderwritingInfo.premium = premium * grossPremiumSharesPerBand.get(grossUnderwritingInfo)
                break
            case PremiumBase.GNPI:
                cededUnderwritingInfo.premium = premium * grossUnderwritingInfo.premium
                break
            case PremiumBase.RATE_ON_LINE:
                cededUnderwritingInfo.premium = premium * limit
                break
            case PremiumBase.NUMBER_OF_POLICIES:
                throw new IllegalArgumentException("XLContractStrategy.invalidPremiumBase")
        }
        // Increases premium written and premium written as if with the reinstatement premium
        double factor = firstCoveredPeriod ? 1 + calculateReinstatementPremiums(coveredByReinsurer) : calculateReinstatementPremiums(coveredByReinsurer)
        firstCoveredPeriod = false
        cededUnderwritingInfo.premium *= factor
        return cededUnderwritingInfo
    }

    boolean exhausted() {
        availableAnnualLimit == 0
    }
}