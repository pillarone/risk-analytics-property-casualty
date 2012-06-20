package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow

import org.pillarone.riskanalytics.domain.pc.constants.StopLossContractBase
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoPacketFactory
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoUtilities
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.reserves.cashflow.ClaimDevelopmentPacket
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfoPacketFactory
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfo
import org.pillarone.riskanalytics.core.simulation.InvalidParameterException

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class StopLossContractStrategy extends AbstractContractStrategy implements IReinsuranceContractStrategy {

    static final ReinsuranceContractType type = ReinsuranceContractType.STOPLOSS

    /** Premium, limit and attachmentPoint can be expressed as a fraction of a base quantity.           */
    StopLossContractBase stopLossContractBase = StopLossContractBase.ABSOLUTE

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
    private double gnpi

    /** aggregate over a whole iteration */
    double aggregateGrossPaidOverIteration = 0
    /** aggregate over a whole iteration */
    double aggregateCededPaidOverIteration = 0

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
        ["stopLossContractBase": stopLossContractBase,
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
        if (stopLossContractBase == StopLossContractBase.GNPI) {
            gnpi = UnderwritingInfoUtilities.aggregate(grossUnderwritingInfos).premium
            scaledAttachmentPointIncurred *= gnpi
            scaledLimit *= gnpi
            scaledTermLimit *= gnpi
        }
        double totalPremium = grossUnderwritingInfos.premium.sum()
        if (totalPremium != 0) {
            for (UnderwritingInfo underwritingInfo: grossUnderwritingInfos) {
                grossPremiumSharesPerBand.put(underwritingInfo, underwritingInfo.premium / totalPremium)
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

        aggregateGrossPaidOverIteration = 0
        aggregateCededPaidOverIteration = 0
    }

    void initBookKeepingFiguresOfPeriod(List<Claim> grossClaims, List<UnderwritingInfo> grossUnderwritingInfos, double coveredByReinsurer) {
        double aggregateGrossIncurred = 0
        double aggregateGrossPaid = 0
        if (grossClaims[0] instanceof ClaimDevelopmentPacket) {
            aggregateGrossIncurred = grossClaims.incurred.sum()
            aggregateGrossPaid = grossClaims.paid.sum()
            aggregateGrossPaidOverIteration += aggregateGrossPaid
        }
        else if (grossClaims[0] instanceof Claim) {
            aggregateGrossIncurred = grossClaims.ultimate.sum()
        }
        double aggregateCededIncurred = Math.min(Math.max(aggregateGrossIncurred - scaledAttachmentPointIncurred, 0), availableScaledIncurredLimit)
        scaledAttachmentPointIncurred = Math.max(scaledAttachmentPointIncurred - aggregateGrossIncurred, 0)
        factorIncurred = (aggregateGrossIncurred != 0) ? aggregateCededIncurred / aggregateGrossIncurred : 0d
        availableScaledIncurredTermLimit -= aggregateCededIncurred
//        availableScaledIncurredLimit = Math.min(scaledLimit, availableScaledIncurredTermLimit)

        double aggregateCededPaid = Math.min(Math.max(aggregateGrossPaidOverIteration - scaledAttachmentPointPaid, 0), availableScaledPaidLimit)
        aggregateCededPaid -= aggregateCededPaidOverIteration
        aggregateCededPaidOverIteration += aggregateCededPaid
        scaledAttachmentPointPaid = Math.max(scaledAttachmentPointPaid - aggregateGrossPaidOverIteration, 0)
        factorPaid = (aggregateGrossPaid != 0) ? aggregateCededPaid / aggregateGrossPaid : 0d
        availableScaledPaidTermLimit -= aggregateCededPaid
//        availableScaledPaidLimit = Math.min(scaledLimit, availableScaledPaidTermLimit)
    }

    void applyAnnualLimits() {
        availableScaledIncurredLimit = Math.min(scaledLimit, availableScaledIncurredTermLimit)
        availableScaledPaidLimit = Math.min(scaledLimit, availableScaledPaidTermLimit)
        scaledAttachmentPointIncurred = attachmentPoint
        if (stopLossContractBase == StopLossContractBase.GNPI) {
            scaledAttachmentPointIncurred *= gnpi
        }
        scaledAttachmentPointPaid = scaledAttachmentPointIncurred
    }

    public void initBookKeepingFigures(List<Claim> inClaims, List<UnderwritingInfo> coverUnderwritingInfo) {
    }

    CededUnderwritingInfo calculateCoverUnderwritingInfo(UnderwritingInfo grossUnderwritingInfo, double coveredByReinsurer) {
        CededUnderwritingInfo cededUnderwritingInfo = CededUnderwritingInfoPacketFactory.copy(grossUnderwritingInfo)
        cededUnderwritingInfo.originalUnderwritingInfo = grossUnderwritingInfo?.originalUnderwritingInfo ? grossUnderwritingInfo.originalUnderwritingInfo : grossUnderwritingInfo
        cededUnderwritingInfo.commission = 0d
        switch (stopLossContractBase) {
            case StopLossContractBase.ABSOLUTE:
                cededUnderwritingInfo.premium = premium * grossPremiumSharesPerBand.get(grossUnderwritingInfo)
                break
            case StopLossContractBase.GNPI:
                cededUnderwritingInfo.premium = premium * grossUnderwritingInfo.premium
                break
            default:
                throw new InvalidParameterException("StopLossContractBase $stopLossContractBase not implemented")
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
            availableScaledIncurredLimit -= cededClaim.incurred
            cededClaim.paid = grossClaim.paid * factorPaid * coveredByReinsurer
            availableScaledPaidLimit -= cededClaim.paid
            if (grossClaim.incurred > 0) {
                cededClaim.reserved = Math.abs(cededClaim.incurred - cededClaim.paid)
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
            availableScaledIncurredLimit -= cededClaim.ultimate
        }
        return cededClaim
    }

    boolean exhausted() {
        return false;
    }
}
