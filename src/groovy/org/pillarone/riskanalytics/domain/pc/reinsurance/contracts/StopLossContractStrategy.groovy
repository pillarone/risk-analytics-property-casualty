package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoUtilities
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoPacketFactory

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo: not yet efficient since the aggregate gross underwriting info is calculated twice
// (in the calculation of the book-keeping figures (implicitly) in the calculation of the ceded underwriting info

class StopLossContractStrategy extends AbstractContractStrategy implements IReinsuranceContractStrategy, IParameterObject {

    static final ReinsuranceContractType type = ReinsuranceContractType.STOPLOSS

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
            "coveredByReinsurer": coveredByReinsurer]
    }

    public double calculateCoveredLoss(Claim inClaim) {
        inClaim.ultimate * factor * coveredByReinsurer
    }

    public void initBookKeepingFigures(List<Claim> inClaims, List<UnderwritingInfo> coverUnderwritingInfo) {
        double aggregateGrossClaimAmount = 0.0
        for (Claim claim in inClaims) {
            aggregateGrossClaimAmount += claim.ultimate
        }
        double scaledAttachmentPoint = attachmentPoint
        double scaledLimit = limit
        if (premiumBase == PremiumBase.GNPI) {
            double gnpi = UnderwritingInfoUtilities.aggregate(coverUnderwritingInfo).premiumWritten
            scaledAttachmentPoint *= gnpi
            scaledLimit *= gnpi
        }

        double aggregateCededClaimAmount = Math.min(Math.max(aggregateGrossClaimAmount - scaledAttachmentPoint, 0), scaledLimit)
        factor = (aggregateGrossClaimAmount != 0) ? aggregateCededClaimAmount / aggregateGrossClaimAmount : 1d

        double totalPremium = coverUnderwritingInfo.premiumWritten.sum()
        if (totalPremium != 0) {
            for (UnderwritingInfo underwritingInfo: coverUnderwritingInfo) {
                grossPremiumSharesPerBand.put(underwritingInfo, underwritingInfo.premiumWritten / totalPremium)
            }
        }
        else {
            for (UnderwritingInfo underwritingInfo: coverUnderwritingInfo) {
                grossPremiumSharesPerBand.put(underwritingInfo, 0)
            }
        }
    }

    // todo: Are the definition for the as-if premium reasonable?
    UnderwritingInfo calculateCoverUnderwritingInfo(UnderwritingInfo grossUnderwritingInfo) {
        UnderwritingInfo cededUnderwritingInfo = UnderwritingInfoPacketFactory.copy(grossUnderwritingInfo)
        cededUnderwritingInfo.originalUnderwritingInfo = grossUnderwritingInfo?.originalUnderwritingInfo ? grossUnderwritingInfo.originalUnderwritingInfo : grossUnderwritingInfo
        cededUnderwritingInfo.commission = 0d
        switch (premiumBase) {
            case PremiumBase.ABSOLUTE:                                          //szu: this does not exist for stop-loss
                cededUnderwritingInfo.premiumWritten = premium * grossPremiumSharesPerBand.get(grossUnderwritingInfo)
                cededUnderwritingInfo.premiumWrittenAsIf = premium * grossPremiumSharesPerBand.get(grossUnderwritingInfo)
                break
            case PremiumBase.GNPI:
                cededUnderwritingInfo.premiumWritten = premium * grossUnderwritingInfo.premiumWritten
                cededUnderwritingInfo.premiumWrittenAsIf = premium * grossUnderwritingInfo.premiumWrittenAsIf
                break
            case PremiumBase.RATE_ON_LINE:
                throw new IllegalArgumentException("Defining the premium base as RoL is not suppported.")
            case PremiumBase.NUMBER_OF_POLICIES:
                throw new IllegalArgumentException("Defining the premium base as number of policies is not suppported.")
        }
        cededUnderwritingInfo
    }

    public void resetMemberInstances() {
        grossPremiumSharesPerBand.clear()
    }
}