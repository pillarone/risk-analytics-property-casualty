package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoPacketFactory

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
abstract class XLContractStrategy extends AbstractContractStrategy implements IReinsuranceContractStrategyWithPremiumAllocation, IParameterObject {

    /** Premium can be expressed as a fraction of a base quantity.                   */
    PremiumBase premiumBase = PremiumBase.ABSOLUTE

    /** Premium as a percentage of the premium base                   */
    double premium

    /** Strategy to allocate the ceded premium to the different lines of business     */
    IPremiumAllocationStrategy premiumAllocation = PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, new HashMap());
    /** As a percentage of premium.                   */
    AbstractMultiDimensionalParameter reinstatementPremiums = new TableMultiDimensionalParameter([0d], ['Reinstatement Premium'])
    double attachmentPoint
    double limit
    double aggregateDeductible
    double aggregateLimit
    Map<UnderwritingInfo, Double> grossPremiumSharesPerBand = [:]

    protected double availableAggregateLimit
    protected double reinstatements
    /** The factor is calculated during initBookkeepingFigures() by applying the aggregateDeductible,
     *  ceded claims will be multiplied by it to apply a positive aggregateDeductible proportionally
     *  to every claim.   */
    protected double deductibleFactor = 1d

    public Map getParameters() {
        return ["premiumBase": premiumBase,
                "premium": premium,
                "premiumAllocation": premiumAllocation,
                "reinstatementPremiums": reinstatementPremiums,
                "attachmentPoint": attachmentPoint,
                "limit": limit,
                "aggregateDeductible": aggregateDeductible,
                "aggregateLimit": aggregateLimit,
                "coveredByReinsurer": coveredByReinsurer]
    }

    abstract ReinsuranceContractType getType()

    abstract double allocateCededClaim(Claim inClaim)

    static double calculateUsedReinstatements(double aggregateLimit, double availableAggregateLimit,
                                              double aggregateDeductible, double limit, double reinstatements) {
        double usedReinstatements = limit == 0d ? 0d : (aggregateLimit - availableAggregateLimit - aggregateDeductible) / limit
        return Math.min(usedReinstatements, reinstatements)
    }

    static double calculateReinstatementPremiums(double aggregateLimit, double availableAggregateLimit,
                                                 double aggregateDeductible, double limit, double reinstatements,
                                                 AbstractMultiDimensionalParameter reinstatementPremiums, double coveredByReinsurer) {
        double usedReinstatements = calculateUsedReinstatements(aggregateLimit, availableAggregateLimit,
                aggregateDeductible, limit, reinstatements)
        double reinstatementPremium = 0d
        for (int i = 1; i <= usedReinstatements; i++) {
            reinstatementPremium += getReinstatementPremiumFactor(i, reinstatementPremiums, coveredByReinsurer)
        }
        double partialReinstatement = usedReinstatements - Math.floor(usedReinstatements)
        reinstatementPremium += partialReinstatement * getReinstatementPremiumFactor(
                Math.ceil(usedReinstatements).toInteger(), reinstatementPremiums, coveredByReinsurer)
        reinstatementPremium
    }

    static double getReinstatementPremiumFactor(int reinstatement, AbstractMultiDimensionalParameter reinstatementPremiums,
                                                double coveredByReinsurer) {
        reinstatementPremiums.values[Math.min(reinstatement, reinstatementPremiums.valueRowCount - 1)] * coveredByReinsurer
    }

    void initBookkeepingFigures(List<Claim> inClaims, List<UnderwritingInfo> coverUnderwritingInfo) {
        availableAggregateLimit = aggregateLimit
        reinstatements = limit == 0d ? 0d : availableAggregateLimit / limit - 1
        double totalPremium = coverUnderwritingInfo.premiumWritten.sum()
        if (totalPremium == 0) {
            for (UnderwritingInfo underwritingInfo: coverUnderwritingInfo) {
                grossPremiumSharesPerBand.put(underwritingInfo, 0)
            }
        }
        else {
            for (UnderwritingInfo underwritingInfo: coverUnderwritingInfo) {
                grossPremiumSharesPerBand.put(underwritingInfo, underwritingInfo.premiumWritten / totalPremium)
            }
        }
    }

    protected void calculateDeductibleFactor(List<Claim> inClaims) {
        deductibleFactor = 1
        if (aggregateDeductible > 0) {
            double aggregateCededBeforeDeductible = 0
            for (Claim claim: inClaims) {
                aggregateCededBeforeDeductible += allocateCededClaim(claim)
            }
            double aggregateCededAfterDeductible = Math.max(0, aggregateCededBeforeDeductible - aggregateDeductible)
            if (aggregateCededBeforeDeductible > 0) {
                deductibleFactor = aggregateCededAfterDeductible / aggregateCededBeforeDeductible
            }
        }
    }

    // todo: Are the definition for the as-if premium reasonable?
    // todo(sku): try to move it in an upper class

    UnderwritingInfo calculateCoverUnderwritingInfo(UnderwritingInfo grossUnderwritingInfo, double initialReserves) {
        UnderwritingInfo cededUnderwritingInfo = UnderwritingInfoPacketFactory.copy(grossUnderwritingInfo)
        cededUnderwritingInfo.originalUnderwritingInfo = grossUnderwritingInfo?.originalUnderwritingInfo ? grossUnderwritingInfo.originalUnderwritingInfo : grossUnderwritingInfo
        cededUnderwritingInfo.commission = 0d
        switch (premiumBase) {
            case PremiumBase.ABSOLUTE:
                cededUnderwritingInfo.premiumWritten = premium * grossPremiumSharesPerBand.get(grossUnderwritingInfo) * coveredByReinsurer
                cededUnderwritingInfo.premiumWrittenAsIf = premium * grossPremiumSharesPerBand.get(grossUnderwritingInfo) * coveredByReinsurer
                break
            case PremiumBase.GNPI:
                cededUnderwritingInfo.premiumWritten = premium * grossUnderwritingInfo.premiumWritten * coveredByReinsurer
                cededUnderwritingInfo.premiumWrittenAsIf = premium * grossUnderwritingInfo.premiumWrittenAsIf * coveredByReinsurer
                break
            case PremiumBase.RATE_ON_LINE:
                cededUnderwritingInfo.premiumWritten = premium * limit * grossPremiumSharesPerBand.get(grossUnderwritingInfo) * coveredByReinsurer
                cededUnderwritingInfo.premiumWrittenAsIf = premium * limit * grossPremiumSharesPerBand.get(grossUnderwritingInfo) * coveredByReinsurer
                break
            case PremiumBase.NUMBER_OF_POLICIES:
                throw new IllegalArgumentException("XLContractStrategy.invalidPremiumBase")
        }
        // Increases premium written and premium written as if with the reinstatement premium
        double factor = 1 + calculateReinstatementPremiums(aggregateLimit, availableAggregateLimit, aggregateDeductible,
                limit, reinstatements, reinstatementPremiums, coveredByReinsurer)
        cededUnderwritingInfo.premiumWritten *= factor
        cededUnderwritingInfo.premiumWrittenAsIf *= factor
        return cededUnderwritingInfo
    }

    public void resetMemberInstances() {
        grossPremiumSharesPerBand.clear()
    }
}