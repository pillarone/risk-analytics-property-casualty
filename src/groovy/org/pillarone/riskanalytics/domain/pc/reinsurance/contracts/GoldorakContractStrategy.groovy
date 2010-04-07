package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.generators.severities.Event
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoUtilities
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoPacketFactory

/**
 *  In a first step claims are merged per event. Merged claims are ceded and afterwards the ceded part is
 *  allocated proportionally to the individual claims.
 *
 * @author shartmann (at) munichre (dot) com
 */
class GoldorakContractStrategy extends XLContractStrategy implements IReinsuranceContractStrategy, IParameterObject {

    double slAttachmentPoint
    double slLimit
    double goldorakSlThreshold


    private double scaledGoldorakSlThreshold
    private double factor
    private double aggregateGrossClaimAmount
    private Map<Event, Double> claimsValueMergedByEvent = [:]
    private Map<Event, Double> cededShareByEvent = [:]

    ReinsuranceContractType getType() {
        ReinsuranceContractType.GOLDORAK
    }

    public Map getParameters() {
        return ["premiumBase": premiumBase,
                "premium": premium,
                "reinstatementPremiums": reinstatementPremiums,
                "attachmentPoint": attachmentPoint,
                "limit": limit,
                "aggregateLimit": aggregateLimit,
                "coveredByReinsurer": coveredByReinsurer,
                "slAttachmentPoint": slAttachmentPoint,
                "slLimit": slLimit,
                "goldorakSlThreshold": goldorakSlThreshold]
    }

    double allocateCededClaim(Claim inClaim) {
        // todo (sku): work on clear definitions of ClaimType.EVENT and ClaimType.AGGREGATE_EVENT
        if (aggregateGrossClaimAmount < scaledGoldorakSlThreshold) {
            //CXL
            if (inClaim.claimType.equals(ClaimType.EVENT) || inClaim.claimType.equals(ClaimType.AGGREGATED_EVENT)) {

                return inClaim.ultimate * cededShareByEvent.get(inClaim.event) * coveredByReinsurer
            }
            else {
                return 0d
            }
        }
        else {
            //SL
            return inClaim.ultimate * factor * coveredByReinsurer
        }
    }

    void initBookkeepingFigures(List<Claim> inClaims, List<UnderwritingInfo> coverUnderwritingInfo) {
        super.initBookkeepingFigures(inClaims, coverUnderwritingInfo)

        aggregateGrossClaimAmount = 0.0
        for (Claim claim in inClaims) {
            aggregateGrossClaimAmount += claim.ultimate
        }
        double scaledAttachmentPoint = attachmentPoint
        double scaledLimit = limit
        double scaledSlLimit = slLimit
        double scaledAggregateGrossClaimAmount = aggregateGrossClaimAmount
        scaledGoldorakSlThreshold = goldorakSlThreshold

        double gnpi = 1.0
        if (premiumBase == PremiumBase.GNPI) {
            gnpi = UnderwritingInfoUtilities.aggregate(coverUnderwritingInfo).premiumWritten
            scaledAttachmentPoint *= gnpi
            scaledLimit *= gnpi
            scaledSlLimit *= gnpi
            scaledGoldorakSlThreshold *= gnpi
        }


        double scaledAggregateCededClaimAmount = Math.min(Math.max(scaledAggregateGrossClaimAmount - scaledAttachmentPoint, 0), scaledSlLimit)
        factor = (scaledAggregateGrossClaimAmount != 0) ? scaledAggregateCededClaimAmount / scaledAggregateGrossClaimAmount : 1d

        // merge claims by event id
        claimsValueMergedByEvent.clear()

        if (scaledAggregateGrossClaimAmount < scaledGoldorakSlThreshold) {
            //CXL case
            for (Claim claim: inClaims) {
                // todo (sku): work on clear definitions of ClaimType.EVENT and ClaimType.AGGREGATE_EVENT
                if (claim.claimType == ClaimType.EVENT || claim.claimType == ClaimType.AGGREGATED_EVENT) {
                    double value = claimsValueMergedByEvent.get(claim.event)
                    if (value != null) {
                        claimsValueMergedByEvent.put(claim.event, value + claim.ultimate)
                    }
                    else {
                        claimsValueMergedByEvent.put(claim.event, claim.ultimate)
                    }
                }
            }

            for (MapEntry claim: claimsValueMergedByEvent.entrySet()) {
                if ((claim.value > 0) && (gnpi > 0)) {
                    double scaledCeded = Math.min(Math.max(claim.value - scaledAttachmentPoint, 0), scaledLimit)
                    double scaledAvailableAggregateLimit = availableAggregateLimit * gnpi
                    scaledCeded = Math.min(scaledAvailableAggregateLimit, scaledCeded)
                    double ceded = scaledCeded / gnpi
                    availableAggregateLimit -= ceded
                    cededShareByEvent.put(claim.key, scaledCeded / claim.value)
                }
                else {
                    cededShareByEvent.put(claim.key, 0d)
                }

            }
        }
    }

    UnderwritingInfo calculateCoverUnderwritingInfo(UnderwritingInfo grossUnderwritingInfo, double initialReserves) {
        UnderwritingInfo cededUnderwritingInfo = UnderwritingInfoPacketFactory.copy(grossUnderwritingInfo)
        cededUnderwritingInfo.originalUnderwritingInfo = grossUnderwritingInfo?.originalUnderwritingInfo ? grossUnderwritingInfo.originalUnderwritingInfo : grossUnderwritingInfo
        cededUnderwritingInfo.commission = 0d
        if (aggregateGrossClaimAmount < scaledGoldorakSlThreshold) {
            // CXL case
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
                    cededUnderwritingInfo.premiumWritten = premium * limit
                    cededUnderwritingInfo.premiumWrittenAsIf = premium * limit
                    break
                case PremiumBase.NUMBER_OF_POLICIES:
                    throw new IllegalArgumentException("Defining the premium base as number of policies is not suppported.")
            }
        }
        else {
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
                    cededUnderwritingInfo.premiumWritten = premium * limit //must be CXL limit here
                    cededUnderwritingInfo.premiumWrittenAsIf = premium * limit //must be CXL limit here
                    break
                case PremiumBase.NUMBER_OF_POLICIES:
                    throw new IllegalArgumentException("Defining the premium base as number of policies is not suppported.")
            }

        }
        // Increases premium written and premium written as if with the reinstatement premium
        double factor = 1 + calculateReinstatementPremiums()
        cededUnderwritingInfo.premiumWritten *= factor
        cededUnderwritingInfo.premiumWrittenAsIf *= factor
        return cededUnderwritingInfo
    }


    public void resetMemberInstances() {
        claimsValueMergedByEvent.clear()
        cededShareByEvent.clear()
    }
}