package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow

import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoPacketFactory
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoUtilities
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.pc.reserves.cashflow.ClaimDevelopmentPacket

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
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
    double termLimit

    private double factorIncurred
    private double factorPaid
    private double availableScaledIncurredLimit
    private double availableScaledPaidLimit
    private double availableScaledIncurredTermLimit
    private double availableScaledPaidTermLimit
    private double scaledAttachmentPointIncurred
    private double scaledAttachmentPointPaid
    private double scaledLimit
    private double scaledTermLimit
    /**
     *  The keys are the original gross claims sorting from claims development component.
     *  These maps are necessary for a correct calculation of the reserves.
     */
    Map<ClaimDevelopmentPacket, Double> remainingCededReserves = new HashMap<ClaimDevelopmentPacket, Double>();

    Map<UnderwritingInfo, Double> grossPremiumSharesPerBand = [:]

//    private XLLimits availableLimit

    ReinsuranceContractType getType() {
        type
    }

    Map getParameters() {
        ["premiumBase": premiumBase,
            "premium": premium,
            "attachmentPoint": attachmentPoint,
            "limit": limit,
            "termLimit": termLimit]
    }

    void initBookKeepingFiguresForIteration(List<Claim> grossClaims, List<UnderwritingInfo> grossUnderwritingInfos) {
        grossPremiumSharesPerBand.clear()
        remainingCededReserves.clear()

        scaledAttachmentPointIncurred = attachmentPoint
        scaledLimit = limit
        scaledTermLimit = termLimit
        if (premiumBase == PremiumBase.GNPI) {
            double gnpi = UnderwritingInfoUtilities.aggregate(grossUnderwritingInfos).premiumWritten
            scaledAttachmentPointIncurred *= gnpi
            scaledLimit *= gnpi
            scaledTermLimit *= gnpi
        }
        double totalPremium = grossUnderwritingInfos.premiumWritten.sum()
        if (totalPremium != 0) {
            for (UnderwritingInfo underwritingInfo: grossUnderwritingInfos) {
                grossPremiumSharesPerBand.put(underwritingInfo, underwritingInfo.premiumWritten / totalPremium)
            }
        }
        else {
            for (UnderwritingInfo underwritingInfo: grossUnderwritingInfos) {
                grossPremiumSharesPerBand.put(underwritingInfo, 0)
            }
        }
        scaledAttachmentPointPaid = scaledAttachmentPointIncurred
        availableScaledIncurredLimit = scaledLimit
        availableScaledPaidLimit = scaledLimit
        availableScaledIncurredTermLimit = scaledTermLimit
        availableScaledPaidTermLimit = scaledTermLimit
    }

    void initBookKeepingFiguresOfPeriod(List<Claim> grossClaims, List<UnderwritingInfo> grossUnderwritingInfos, double coveredByReinsurer) {
        double aggregateGrossIncurred = 0
        double aggregateGrossPaid = 0
        if (grossClaims[0] instanceof ClaimDevelopmentPacket) {
            aggregateGrossIncurred = grossClaims.incurred.sum()
            aggregateGrossPaid = grossClaims.paid.sum()
        }
        else if (grossClaims[0] instanceof Claim) {
            aggregateGrossIncurred = grossClaims.ultimate.sum()
        }
        double aggregateCededIncurred = Math.min(Math.max(aggregateGrossIncurred - scaledAttachmentPointIncurred, 0), availableScaledIncurredLimit)
        scaledAttachmentPointIncurred = Math.max(scaledAttachmentPointIncurred - aggregateGrossIncurred, 0)
        factorIncurred = (aggregateGrossIncurred != 0) ? aggregateCededIncurred / aggregateGrossIncurred : 0d
        availableScaledIncurredTermLimit -= aggregateCededIncurred
        availableScaledIncurredLimit = Math.min(scaledLimit, availableScaledIncurredTermLimit)

        double aggregateCededPaid = Math.min(Math.max(aggregateGrossPaid - scaledAttachmentPointPaid, 0), availableScaledPaidLimit)
        scaledAttachmentPointPaid = Math.max(scaledAttachmentPointPaid - aggregateGrossPaid, 0)
        factorPaid = (aggregateGrossPaid != 0) ? aggregateCededPaid / aggregateGrossPaid : 0d
        availableScaledPaidTermLimit -= aggregateCededPaid
        availableScaledPaidLimit = Math.min(scaledLimit, availableScaledPaidTermLimit)
    }



    public void initBookKeepingFigures(List<Claim> inClaims, List<UnderwritingInfo> coverUnderwritingInfo) {
    }

    UnderwritingInfo calculateCoverUnderwritingInfo(UnderwritingInfo grossUnderwritingInfo, double coveredByReinsurer) {
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
                throw new IllegalArgumentException("Defining the premium base as RoL is not suppported.")
            case PremiumBase.NUMBER_OF_POLICIES:
                throw new IllegalArgumentException("Defining the premium base as number of policies is not suppported.")
        }
        cededUnderwritingInfo
    }

    public void resetMemberInstances() {
    }

    Claim calculateCededClaim(Claim grossClaim, double coveredByReinsurer) {
        Claim cededClaim = grossClaim.copy()
        cededClaim.scale(0)
        if (grossClaim instanceof ClaimDevelopmentPacket) {
            cededClaim.incurred = grossClaim.incurred * factorIncurred * coveredByReinsurer
            cededClaim.paid = grossClaim.paid * factorPaid * coveredByReinsurer
            if (grossClaim.incurred > 0) {
                cededClaim.reserved = cededClaim.incurred - cededClaim.paid
                remainingCededReserves.put(grossClaim, cededClaim.reserved)
            }
            else {
                if (remainingCededReserves.get(grossClaim.originalClaim)) {
                    cededClaim.reserved = remainingCededReserves.get(grossClaim.originalClaim) - cededClaim.paid
                }
                remainingCededReserves.put(grossClaim.originalClaim, cededClaim.reserved)
            }
        }
        else if (grossClaim instanceof Claim) {
            cededClaim.ultimate = grossClaim.ultimate * factorIncurred * coveredByReinsurer
        }
        return cededClaim
    }

    boolean exhausted() {
        return false;
    }
}