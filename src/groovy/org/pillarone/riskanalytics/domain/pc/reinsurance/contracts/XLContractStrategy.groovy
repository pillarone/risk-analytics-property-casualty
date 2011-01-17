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
abstract class XLContractStrategy extends AbstractContractStrategy implements IReinsuranceContractStrategy, IParameterObject {

    /** Premium can be expressed as a fraction of a base quantity.                         */
    PremiumBase premiumBase = PremiumBase.ABSOLUTE

    /** Premium as a percentage of the premium base                         */
    double premium

    /** Strategy to allocate the ceded premium to the different lines of business           */
    IPremiumAllocationStrategy premiumAllocation = PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, new HashMap());
    /** As a percentage of premium.                         */
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
     *  to every claim.         */
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

    void initCededPremiumAllocation(List<Claim> cededClaims, List<UnderwritingInfo> grossUnderwritingInfos) {
        premiumAllocation.initSegmentShares cededClaims, grossUnderwritingInfos
    }

    // todo(sku): try to move it in an upper class

    UnderwritingInfo calculateCoverUnderwritingInfo(UnderwritingInfo grossUnderwritingInfo, double initialReserves) {
        UnderwritingInfo cededUnderwritingInfo = UnderwritingInfoPacketFactory.copy(grossUnderwritingInfo)
        cededUnderwritingInfo.commission = 0d
        cededUnderwritingInfo.fixedCommission = 0d
        cededUnderwritingInfo.variableCommission = 0d
        switch (premiumBase) {
            case PremiumBase.ABSOLUTE:
                cededUnderwritingInfo.premium = premium * premiumAllocation.getShare(grossUnderwritingInfo)
                break
            case PremiumBase.GNPI:
                cededUnderwritingInfo.premium = premium * grossUnderwritingInfo.premium // todo (jwa): premiumAllocation as is done in StopLoss??
                break
            case PremiumBase.RATE_ON_LINE:
                cededUnderwritingInfo.premium = premium * limit * premiumAllocation.getShare(grossUnderwritingInfo)
                break
            case PremiumBase.NUMBER_OF_POLICIES:
                throw new IllegalArgumentException("XLContractStrategy.invalidPremiumBase")
        }
        cededUnderwritingInfo.fixedPremium = cededUnderwritingInfo.premium
        cededUnderwritingInfo.variablePremium = cededUnderwritingInfo.premium * calculateReinstatementPremiums(aggregateLimit,
                availableAggregateLimit, aggregateDeductible, limit, reinstatements, reinstatementPremiums, coveredByReinsurer) * coveredByReinsurer
        cededUnderwritingInfo.premium = cededUnderwritingInfo.fixedPremium + cededUnderwritingInfo.variablePremium
        return cededUnderwritingInfo
    }

    public void resetMemberInstances() {
        grossPremiumSharesPerBand.clear()
    }
}