package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.constants.StopLossContractBase
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoUtilities
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoPacketFactory
import org.pillarone.riskanalytics.domain.pc.lob.LobMarker
import org.pillarone.riskanalytics.domain.pc.claims.ClaimFilterUtilities
import org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo: not yet efficient since the aggregate gross underwriting info is calculated twice
// (in the calculation of the book-keeping figures (implicitly) in the calculation of the ceded underwriting info

class StopLossContractStrategy extends AbstractContractStrategy implements IReinsuranceContractStrategy, IParameterObject {

    static final ReinsuranceContractType type = ReinsuranceContractType.STOPLOSS

    /** Premium, limit and attachmentPoint can be expressed as a fraction of a base quantity. */
    StopLossContractBase stopLossContractBase = StopLossContractBase.ABSOLUTE

    /** Premium as a percentage of the premium base */
    double premium

    /** Allocation of the premium to the affected lines of business      */
    IPremiumAllocationStrategy premiumAllocation = PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, new HashMap());

    /** attachment point is also expressed as a fraction of gnpi if premium base == GNPI               */
    double attachmentPoint

    /** attachment point is also expressed as a fraction of gnpi if premium base == GNPI            */
    double limit

    private double factor

    private double totalCededPremium

    ReinsuranceContractType getType() {
        type
    }

    Map getParameters() {
        ["stopLossContractBase": stopLossContractBase,
                "premium": premium,
                "attachmentPoint": attachmentPoint,
                "premiumAllocation": premiumAllocation,
                "limit": limit,
                "coveredByReinsurer": coveredByReinsurer]
    }

    public double allocateCededClaim(Claim inClaim) {
        inClaim.ultimate * factor * coveredByReinsurer
    }

    public void initBookkeepingFigures(List<Claim> inClaims, List<UnderwritingInfo> coverUnderwritingInfo) {
        double aggregateGrossClaimAmount = 0.0
        for (Claim claim in inClaims) {
            aggregateGrossClaimAmount += claim.ultimate
        }
        double scaledAttachmentPoint = attachmentPoint
        double scaledLimit = limit
        if (stopLossContractBase == StopLossContractBase.GNPI) {
            double gnpi = UnderwritingInfoUtilities.aggregate(coverUnderwritingInfo).premiumWritten
            scaledAttachmentPoint *= gnpi
            scaledLimit *= gnpi
            totalCededPremium = coverUnderwritingInfo.premiumWritten.sum() * premium
        }
        else if (stopLossContractBase == StopLossContractBase.ABSOLUTE) {
            totalCededPremium = premium
        }

        double aggregateCededClaimAmount = Math.min(Math.max(aggregateGrossClaimAmount - scaledAttachmentPoint, 0), scaledLimit)
        factor = (aggregateGrossClaimAmount != 0) ? aggregateCededClaimAmount / aggregateGrossClaimAmount : 1d
    }

    void initCededPremiumAllocation(List<Claim> cededClaims, List<UnderwritingInfo> grossUnderwritingInfos) {
        premiumAllocation.initSegmentShares cededClaims, grossUnderwritingInfos
    }

    // todo: Are the definition for the as-if premium reasonable?
    UnderwritingInfo calculateCoverUnderwritingInfo(UnderwritingInfo grossUnderwritingInfo, double initialReserves) {
        UnderwritingInfo cededUnderwritingInfo = UnderwritingInfoPacketFactory.copy(grossUnderwritingInfo)
        cededUnderwritingInfo.originalUnderwritingInfo = grossUnderwritingInfo?.originalUnderwritingInfo ? grossUnderwritingInfo.originalUnderwritingInfo : grossUnderwritingInfo
        cededUnderwritingInfo.commission = 0d
        double factor = premiumAllocation.getShare(grossUnderwritingInfo) * coveredByReinsurer
        cededUnderwritingInfo.premiumWritten = totalCededPremium * factor
        cededUnderwritingInfo.premiumWrittenAsIf = totalCededPremium * factor
        cededUnderwritingInfo
    }
}
