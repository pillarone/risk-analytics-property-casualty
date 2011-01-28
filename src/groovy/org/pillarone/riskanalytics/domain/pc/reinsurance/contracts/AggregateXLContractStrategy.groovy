package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoUtilities
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoPacketFactory
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfoPacketFactory

/**
 * @author Michael-Noe (at) Web (dot) de
 */
class AggregateXLContractStrategy extends AbstractContractStrategy implements IReinsuranceContractStrategy, IParameterObject {

    static final ReinsuranceContractType type = ReinsuranceContractType.AGGREGATEXL
    ClaimType claimClass = ClaimType.AGGREGATED_EVENT

    /** Premium can be expressed as a fraction of a base quantity.             */
    PremiumBase premiumBase = PremiumBase.ABSOLUTE

    /** Strategy to allocate the ceded premium to the different lines of business       */
    IPremiumAllocationStrategy premiumAllocation = PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, new HashMap());

    /** Premium as a percentage of the premium base             */
    double premium

    /** attachment point is also expressed as a fraction of gnpi if premium base == GNPI             */
    double attachmentPoint

    /** attachment point is also expressed as a fraction of gnpi if premium base == GNPI          */
    double limit

    private double factor

    double totalCededPremium

    ReinsuranceContractType getType() {
        type
    }

    Map getParameters() {
        ["premiumBase": premiumBase,
                "premium": premium,
                "premiumAllocation": premiumAllocation,
                "attachmentPoint": attachmentPoint,
                "limit": limit,
                "coveredByReinsurer": coveredByReinsurer,
                "claimClass": claimClass]
    }

    public double allocateCededClaim(Claim inClaim) {
        double coveredLoss
        if (inClaim.claimType == claimClass) {
            coveredLoss = inClaim.ultimate * factor
        }
        else {
            coveredLoss = 0d
        }
        return coveredLoss
    }

    public void initBookkeepingFigures(List<Claim> inClaims, List<UnderwritingInfo> coverUnderwritingInfo) {
        double aggregateGrossClaimAmount = 0.0
        for (Claim claim in inClaims) {
            if (claim.claimType == claimClass) {
                aggregateGrossClaimAmount += claim.ultimate
            }
        }
        double scaledAttachmentPoint = attachmentPoint
        double scaledLimit = limit
        if (premiumBase == PremiumBase.GNPI) {
            double gnpi = UnderwritingInfoUtilities.aggregate(coverUnderwritingInfo).premium
            scaledAttachmentPoint *= gnpi
            scaledLimit *= gnpi
        }

        double aggregateCededClaimAmount = (double) Math.min(Math.max(aggregateGrossClaimAmount - scaledAttachmentPoint, 0), scaledLimit)
        if (aggregateGrossClaimAmount != 0) {
            factor = ((double) aggregateCededClaimAmount) / ((double) aggregateGrossClaimAmount)
        }
        else {
            factor = 0d
        }

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

    public CededUnderwritingInfo calculateCoverUnderwritingInfo(UnderwritingInfo grossUnderwritingInfo, double initialReserves) {
        CededUnderwritingInfo cededUnderwritingInfo = CededUnderwritingInfoPacketFactory.copy(grossUnderwritingInfo)
        cededUnderwritingInfo.originalUnderwritingInfo = grossUnderwritingInfo?.originalUnderwritingInfo ? grossUnderwritingInfo.originalUnderwritingInfo : grossUnderwritingInfo
        cededUnderwritingInfo.commission = 0d
        cededUnderwritingInfo.fixedCommission = 0d
        cededUnderwritingInfo.variableCommission = 0d
        // we do not know anything about sum insured here; guarantee that (max) sum insured of net and gross are equal
        cededUnderwritingInfo.sumInsured = 0d
        cededUnderwritingInfo.maxSumInsured = 0d
        cededUnderwritingInfo.variablePremium = 0d
        cededUnderwritingInfo.premium = totalCededPremium * premiumAllocation.getShare(grossUnderwritingInfo)
        cededUnderwritingInfo.setFixedPremium(cededUnderwritingInfo.getPremium())
        cededUnderwritingInfo
    }
}