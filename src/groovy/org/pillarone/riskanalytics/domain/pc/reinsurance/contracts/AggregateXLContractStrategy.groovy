package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoUtilities
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoPacketFactory

/**
 * @author Michael-Noe (at) Web (dot) de
 */
class AggregateXLContractStrategy extends AbstractContractStrategy implements IReinsuranceContractStrategy, IParameterObject {

    static final ReinsuranceContractType type = ReinsuranceContractType.AGGREGATEXL
    ClaimType claimClass = ClaimType.AGGREGATED_EVENT

    /** Premium can be expressed as a fraction of a base quantity.           */
    PremiumBase premiumBase = PremiumBase.ABSOLUTE

    /** Premium as a percentage of the premium base           */
    double premium

    /** attachment point is also expressed as a fraction of gnpi if premium base == GNPI           */
    double attachmentPoint

    /** attachment point is also expressed as a fraction of gnpi if premium base == GNPI        */
    double limit

    private double factor

    Map<UnderwritingInfo, Double> grossPremiumSharesPerBand = [:]

    ReinsuranceContractType getType() {
        type
    }

    Map getParameters() {
        ["premiumBase": premiumBase,
            "premium": premium,
            "attachmentPoint": attachmentPoint,
            "limit": limit,
            "coveredByReinsurer": coveredByReinsurer,
            "claimClass": claimClass]
    }

    public double allocateCededClaim(Claim inClaim) {
        double coveredLoss
        if (inClaim.claimType == claimClass) {
            coveredLoss = inClaim.ultimate * factor * coveredByReinsurer
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
            double gnpi = UnderwritingInfoUtilities.aggregate(coverUnderwritingInfo).premiumWritten
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
        double totalPremium = coverUnderwritingInfo.premiumWritten.sum()
        for (UnderwritingInfo underwritingInfo: coverUnderwritingInfo) {
            grossPremiumSharesPerBand.put(underwritingInfo, underwritingInfo.premiumWritten / totalPremium)
        }
    }


    public UnderwritingInfo calculateCoverUnderwritingInfo(UnderwritingInfo grossUnderwritingInfo, double initialReserves) {
        UnderwritingInfo cededUnderwritingInfo = UnderwritingInfoPacketFactory.copy(grossUnderwritingInfo)
        cededUnderwritingInfo.originalUnderwritingInfo = grossUnderwritingInfo?.originalUnderwritingInfo ? grossUnderwritingInfo.originalUnderwritingInfo : grossUnderwritingInfo
        cededUnderwritingInfo.commission = 0d
        switch (premiumBase) {
            case PremiumBase.ABSOLUTE:
                cededUnderwritingInfo.premiumWritten = premium * grossPremiumSharesPerBand.get(grossUnderwritingInfo)
                cededUnderwritingInfo.premiumWrittenAsIf = premium * grossPremiumSharesPerBand.get(grossUnderwritingInfo)
                break
            case PremiumBase.GNPI:
                cededUnderwritingInfo.premiumWritten = premium * grossUnderwritingInfo.premiumWritten
                cededUnderwritingInfo.premiumWrittenAsIf = premium * grossUnderwritingInfo.premiumWrittenAsIf
                break
            case PremiumBase.RATE_ON_LINE:
                throw new IllegalArgumentException("AggregateXLContractStrategy.PremiumBaseAsRoL")
            case PremiumBase.NUMBER_OF_POLICIES:
                throw new IllegalArgumentException("AggregateXLContractStrategy.PremiumBaseAsNoOfPolicies")
        }
        cededUnderwritingInfo
    }
}