package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.claims.Claim

import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoUtilities
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket
import org.pillarone.riskanalytics.domain.pc.reserves.cashflow.ClaimDevelopmentPacket
import org.pillarone.riskanalytics.domain.pc.constants.StopLossContractBase
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfoPacketFactory
import org.pillarone.riskanalytics.core.simulation.InvalidParameterException
import java.security.InvalidParameterException

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo: not yet efficient, since the aggregate gross underwriting info is calculated twice: first, when
// calculating the bookkeeping figures, then again (implicitly) calculating the ceded underwriting info.

class AdverseDevelopmentCoverContractStrategy extends AbstractContractStrategy implements IReinsuranceContractStrategyWithClaimsDevelopment {

    static final ReinsuranceContractType type = ReinsuranceContractType.ADVERSEDEVELOPMENTCOVER

    /** Premium, attachment point and limit can be expressed as a fraction of a base quantity.   */
    StopLossContractBase stopLossContractBase = StopLossContractBase.ABSOLUTE

    /** Premium as a percentage of the premium base   */
    double premium

    /** attachment point is also expressed as a fraction of gnpi if premium base == GNPI   */
    double attachmentPoint

    /** attachment point is also expressed as a fraction of gnpi if premium base == GNPI   */
    double limit

    private double incurredAllocationFactor
    private double paidAllocationFactor

    Map<UnderwritingInfo, Double> grossPremiumSharesPerBand = [:]

    ReinsuranceContractType getType() {
        type
    }

    Map getParameters() {
        ["stopLossContractBase": stopLossContractBase,
                "premium": premium,
                "attachmentPoint": attachmentPoint,
                "limit": limit,
                "coveredByReinsurer": coveredByReinsurer]
    }

    public double allocateCededClaim(Claim inClaim) {
        inClaim.ultimate * incurredAllocationFactor
    }

    public double allocateCededPaid(ClaimDevelopmentPacket inClaim) {
        inClaim.paid * paidAllocationFactor
    }

    public double allocateCededPaid(ClaimDevelopmentLeanPacket inClaim) {
        inClaim.paid * paidAllocationFactor
    }

    public void initBookkeepingFigures(List<Claim> inClaims, List<UnderwritingInfo> coverUnderwritingInfo) {
        // calculate aggregate ultimate/incurred & paid (by primary insurer)
        double aggregateGrossClaim = inClaims.ultimate.sum()
        // problem: not all incoming claims packets may have a paid property; solution: sum those found
        // assume nothing was paid for Claim packets (which lack the paid property)
        double aggregateGrossClaimPaid = inClaims.collect {
            it instanceof ClaimDevelopmentLeanPacket ? ((ClaimDevelopmentLeanPacket) it).paid :
                it instanceof ClaimDevelopmentPacket ? ((ClaimDevelopmentPacket) it).paid :
                    0
        }.sum()

        double scaledAttachmentPoint = attachmentPoint
        double scaledLimit = limit
        if (stopLossContractBase == StopLossContractBase.GNPI) {
            double gnpi = UnderwritingInfoUtilities.aggregate(coverUnderwritingInfo).premium
            scaledAttachmentPoint *= gnpi
            scaledLimit *= gnpi
        }

        // calculate aggregate ultimate & paid by this reinsurer (for this contract)
        double aggregateCededClaim = Math.min(Math.max(aggregateGrossClaim - scaledAttachmentPoint, 0), scaledLimit)
        double aggregateCededClaimPaid = Math.min(Math.max(aggregateGrossClaimPaid - scaledAttachmentPoint, 0), scaledLimit)

        // calculate allocation factors (used by allocateCededClaim & allocateCededPaid)
        incurredAllocationFactor = (aggregateGrossClaim != 0) ? aggregateCededClaim / aggregateGrossClaim : 1d
        paidAllocationFactor = (aggregateGrossClaimPaid != 0) ? aggregateCededClaimPaid / aggregateGrossClaimPaid : 1d

        double totalPremium = coverUnderwritingInfo.empty ? 0 : coverUnderwritingInfo.premium.sum()
        if (totalPremium != 0) {
            for (UnderwritingInfo underwritingInfo: coverUnderwritingInfo) {
                grossPremiumSharesPerBand.put(underwritingInfo, underwritingInfo.premium / totalPremium)
            }
        }
        else {
            for (UnderwritingInfo underwritingInfo: coverUnderwritingInfo) {
                grossPremiumSharesPerBand.put(underwritingInfo, 0)
            }
        }
    }

    CededUnderwritingInfo calculateCoverUnderwritingInfo(UnderwritingInfo grossUnderwritingInfo, double initialReserves) {
        CededUnderwritingInfo cededUnderwritingInfo = CededUnderwritingInfoPacketFactory.copy(grossUnderwritingInfo)
        cededUnderwritingInfo.originalUnderwritingInfo = grossUnderwritingInfo?.originalUnderwritingInfo ? grossUnderwritingInfo.originalUnderwritingInfo : grossUnderwritingInfo
        cededUnderwritingInfo.commission = 0d
        cededUnderwritingInfo.fixedCommission = 0d
        cededUnderwritingInfo.variableCommission = 0d
        // we do not know anything about sum insured here; guarantee that (max) sum insured of net and gross are equal
        cededUnderwritingInfo.sumInsured = 0d
        cededUnderwritingInfo.maxSumInsured = 0d
        cededUnderwritingInfo.variablePremium = 0d
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
        cededUnderwritingInfo.setFixedPremium(cededUnderwritingInfo.getPremium())
        cededUnderwritingInfo
    }

    public void resetMemberInstances() {
        grossPremiumSharesPerBand.clear()
    }
}
