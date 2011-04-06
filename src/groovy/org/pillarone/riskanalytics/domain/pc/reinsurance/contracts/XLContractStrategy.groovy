package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.claims.Claim

import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfoPacketFactory
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfo

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
abstract class XLContractStrategy extends AbstractContractStrategy implements IReinsuranceContractStrategy {

    /** Premium can be expressed as a fraction of a base quantity.                                  */
    PremiumBase premiumBase = PremiumBase.ABSOLUTE

    /** Premium as a percentage of the premium base                                  */
    double premium

    /** Strategy to allocate the ceded premium to the different lines of business                    */
    IPremiumAllocationStrategy premiumAllocation = PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, new HashMap());
    /** As a percentage of premium.                                  */
    AbstractMultiDimensionalParameter reinstatementPremiums = new TableMultiDimensionalParameter([0d], ['Reinstatement Premium'])
    double attachmentPoint
    double limit
    double aggregateDeductible
    double aggregateLimit

    private double totalCededPremium
    private double availableAggregateLimit
    /** before AAD & AAL */
    private double remainingAggregateDeductible
    private double reinstatements

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

    protected double calculateCededClaim(double ultimate) {
        if (availableAggregateLimit > 0) {
            double ceded = Math.min(Math.max(ultimate - attachmentPoint, 0), limit)
            double cededAfterAAD = Math.max(0, ceded - remainingAggregateDeductible)
            remainingAggregateDeductible = Math.max(0, remainingAggregateDeductible - ceded)
            double cededAfterAAL = availableAggregateLimit > cededAfterAAD ? cededAfterAAD : availableAggregateLimit
            availableAggregateLimit -= cededAfterAAL
            return cededAfterAAL
        }
        else {
            return 0d
        }
    }

    static double calculateUsedReinstatements(double aggregateLimit, double availableAggregateLimit,
                                              double aggregateDeductible, double limit, double reinstatements) {
        double usedReinstatements = limit == 0d ? 0d : (Double) (aggregateLimit - availableAggregateLimit) / limit
        return Math.min(usedReinstatements, reinstatements)
    }

    static double calculateReinstatementPremiums(double aggregateLimit, double availableAggregateLimit,
                                                 double aggregateDeductible, double limit, double reinstatements,
                                                 AbstractMultiDimensionalParameter reinstatementPremiums) {
        double usedReinstatements = calculateUsedReinstatements(aggregateLimit, availableAggregateLimit,
                aggregateDeductible, limit, reinstatements)
        double reinstatementPremium = 0d
        for (int i = 1; i <= usedReinstatements; i++) {
            reinstatementPremium += getReinstatementPremiumFactor(i, reinstatementPremiums)
        }
        double partialReinstatement = usedReinstatements - Math.floor(usedReinstatements)
        reinstatementPremium += partialReinstatement * getReinstatementPremiumFactor(
                Math.ceil(usedReinstatements).toInteger(), reinstatementPremiums)
        reinstatementPremium
    }

    static double getReinstatementPremiumFactor(int reinstatement, AbstractMultiDimensionalParameter reinstatementPremiums) {
        reinstatementPremiums.values[Math.min(reinstatement, reinstatementPremiums.valueRowCount - 1)]
    }

    void initBookkeepingFigures(List<Claim> inClaims, List<UnderwritingInfo> coverUnderwritingInfo) {
        availableAggregateLimit = aggregateLimit
        remainingAggregateDeductible = aggregateDeductible
        reinstatements = limit == 0d ? 0d : availableAggregateLimit / limit - 1

        switch (premiumBase) {
            case PremiumBase.ABSOLUTE:
                totalCededPremium = premium
                break
            case PremiumBase.GNPI:
                totalCededPremium = premium * coverUnderwritingInfo.premium.sum()
                break
            case PremiumBase.RATE_ON_LINE:
                totalCededPremium = premium * limit
                break
            case PremiumBase.NUMBER_OF_POLICIES:
                totalCededPremium = premium * coverUnderwritingInfo.numberOfPolicies.sum()
                break
        }
    }

    void initCededPremiumAllocation(List<Claim> cededClaims, List<UnderwritingInfo> grossUnderwritingInfos) {
        premiumAllocation.initSegmentShares(cededClaims, grossUnderwritingInfos)
    }

// todo(sku): try to move it in an upper class

    CededUnderwritingInfo calculateCoverUnderwritingInfo(UnderwritingInfo grossUnderwritingInfo, double initialReserves) {
        CededUnderwritingInfo cededUnderwritingInfo = CededUnderwritingInfoPacketFactory.copy(grossUnderwritingInfo)
        cededUnderwritingInfo.commission = 0d
        cededUnderwritingInfo.fixedCommission = 0d
        cededUnderwritingInfo.variableCommission = 0d
        // we do not know anything about sum insured here; guarantee that (max) sum insured of net and gross are equal
        cededUnderwritingInfo.sumInsured = 0d
        cededUnderwritingInfo.maxSumInsured = 0d
        cededUnderwritingInfo.premium = totalCededPremium * premiumAllocation.getShare(grossUnderwritingInfo)
        cededUnderwritingInfo.fixedPremium = cededUnderwritingInfo.premium
        cededUnderwritingInfo.variablePremium = cededUnderwritingInfo.premium * calculateReinstatementPremiums(aggregateLimit,
                availableAggregateLimit, aggregateDeductible, limit, reinstatements, reinstatementPremiums)
        cededUnderwritingInfo.premium = cededUnderwritingInfo.fixedPremium + cededUnderwritingInfo.variablePremium
        return cededUnderwritingInfo
    }

}
