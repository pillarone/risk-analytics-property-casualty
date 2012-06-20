package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.generators.severities.Event
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoUtilities
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfoPacketFactory
import org.pillarone.riskanalytics.core.simulation.InvalidParameterException

/**
 *  In a first step claims are merged per event. Merged claims are ceded and afterwards the ceded part is
 *  allocated proportionally to the individual claims.
 *
 * @author shartmann (at) munichre (dot) com
 */
class GoldorakContractStrategy extends AbstractContractStrategy implements IReinsuranceContractStrategy {
/** Premium can be expressed as a fraction of a base quantity.                     */
    PremiumBase premiumBase = PremiumBase.ABSOLUTE

    /** Premium as a percentage of the premium base                     */
    double premium

    /** As a percentage of premium.                     */
    AbstractMultiDimensionalParameter reinstatementPremiums = new TableMultiDimensionalParameter([0d], ['Reinstatement Premium'])
    double cxlAttachmentPoint
    double cxlLimit
    double cxlAggregateDeductible
    double cxlAggregateLimit
    double slAttachmentPoint
    double slLimit
    double goldorakSlThreshold


    private double scaledGoldorakSlThreshold
    private double factor
    private double cxlAvailableAggregateLimit
    private double aggregateGrossClaimAmount
    private Map<Event, Double> claimsValueMergedByEvent = [:]
    private Map<Event, Double> cededShareByEvent = [:]
    private Map<UnderwritingInfo, Double> grossPremiumSharesPerBand = [:]
    private double totalCededPremium

    ReinsuranceContractType getType() {
        ReinsuranceContractType.GOLDORAK
    }

    public Map getParameters() {
        return ["premiumBase": premiumBase,
                "premium": premium,
                "reinstatementPremiums": reinstatementPremiums,
                "cxlAttachmentPoint": cxlAttachmentPoint,
                "cxlLimit": cxlLimit,
                "cxlAggregateDeductible": cxlAggregateDeductible,
                "cxlAggregateLimit": cxlAggregateLimit,
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

                return inClaim.ultimate * cededShareByEvent.get(inClaim.event)
            }
            else {
                return 0d
            }
        }
        else {
            //SL
            return inClaim.ultimate * factor
        }
    }

    void initBookkeepingFigures(List<Claim> inClaims, List<UnderwritingInfo> coverUnderwritingInfo) {
        super.initBookkeepingFigures(inClaims, coverUnderwritingInfo)
        cxlAvailableAggregateLimit = cxlAggregateLimit
        double totalPremium = coverUnderwritingInfo.premium.sum()
        if (totalPremium == 0) {
            for (UnderwritingInfo underwritingInfo: coverUnderwritingInfo) {
                grossPremiumSharesPerBand.put(underwritingInfo, 0)
            }
        }
        else {
            for (UnderwritingInfo underwritingInfo: coverUnderwritingInfo) {
                grossPremiumSharesPerBand.put(underwritingInfo, underwritingInfo.premium / totalPremium)
            }
        }

        aggregateGrossClaimAmount = 0.0
        for (Claim claim in inClaims) {
            aggregateGrossClaimAmount += claim.ultimate
        }
        double scaledAttachmentPoint = cxlAttachmentPoint
        double scaledLimit = cxlLimit
        double scaledSlLimit = slLimit
        double scaledAggregateGrossClaimAmount = aggregateGrossClaimAmount
        scaledGoldorakSlThreshold = goldorakSlThreshold

        double gnpi = 1.0
        if (premiumBase == PremiumBase.GNPI) {
            gnpi = UnderwritingInfoUtilities.aggregate(coverUnderwritingInfo).premium
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
                    double scaledAvailableAggregateLimit = cxlAvailableAggregateLimit * gnpi
                    scaledCeded = Math.min(scaledAvailableAggregateLimit, scaledCeded)
                    double ceded = scaledCeded / gnpi
                    cxlAvailableAggregateLimit -= ceded
                    cededShareByEvent.put(claim.key, scaledCeded / claim.value)
                }
                else {
                    cededShareByEvent.put(claim.key, 0d)
                }

            }
        }

        // todo(jwa): cf. History: condition (if (aggregateGrossClaimAmount < scaledGoldorakSlThreshold)) was used, but with no effect, ask sha; probably distinguish between cxlLimit and slLimit
        switch (premiumBase) {
            case PremiumBase.ABSOLUTE:
                totalCededPremium = premium
                break
            case PremiumBase.GNPI:
                totalCededPremium = premium * coverUnderwritingInfo.premium.sum()
                break
            case PremiumBase.RATE_ON_LINE:
                totalCededPremium = premium * cxlLimit
                break
            case PremiumBase.NUMBER_OF_POLICIES:
                totalCededPremium = premium * coverUnderwritingInfo.numberOfPolicies.sum()
                break
            default:
                throw new InvalidParameterException("PremiumBase $premiumBase not implemented")
        }
    }

    CededUnderwritingInfo calculateCoverUnderwritingInfo(UnderwritingInfo grossUnderwritingInfo, double initialReserves) {
        CededUnderwritingInfo cededUnderwritingInfo = CededUnderwritingInfoPacketFactory.copy(grossUnderwritingInfo)
        cededUnderwritingInfo.originalUnderwritingInfo = grossUnderwritingInfo?.originalUnderwritingInfo ? grossUnderwritingInfo.originalUnderwritingInfo : grossUnderwritingInfo
        cededUnderwritingInfo.commission = 0d
        cededUnderwritingInfo.fixedCommission = 0d
        cededUnderwritingInfo.variableCommission = 0d
        cededUnderwritingInfo.premium = totalCededPremium * grossPremiumSharesPerBand(grossUnderwritingInfo)
        double reinstatements = cxlAvailableAggregateLimit / cxlLimit - 1
        cededUnderwritingInfo.fixedPremium = cededUnderwritingInfo.premium
        cededUnderwritingInfo.variablePremium = XLContractStrategy.calculateReinstatementPremiums(cxlAggregateLimit, cxlAvailableAggregateLimit,
                cxlAggregateDeductible, cxlLimit, reinstatements, reinstatementPremiums) * cededUnderwritingInfo.premium
        cededUnderwritingInfo.premium = cededUnderwritingInfo.fixedPremium + cededUnderwritingInfo.variablePremium
        return cededUnderwritingInfo
    }


    public void resetMemberInstances() {
        claimsValueMergedByEvent.clear()
        cededShareByEvent.clear()
    }
}
