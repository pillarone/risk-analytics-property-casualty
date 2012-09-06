package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.ILimitStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.LimitStrategyType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.AalAadLimitStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.AadLimitStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.AalLimitStrategy
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.EventLimitStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.EventAalLimitStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.NoneLimitStrategy
import org.apache.commons.lang.NotImplementedException
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfoPacketFactory
import org.pillarone.riskanalytics.core.simulation.InvalidParameterException

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class QuotaShareContractStrategy extends AbstractContractStrategy implements IReinsuranceContractStrategy {

    static final ReinsuranceContractType type = ReinsuranceContractType.QUOTASHARE

    double quotaShare = 0
    ILimitStrategy limit = LimitStrategyType.getNoLimit()

    private double availableAnnualAggregateLimit
    private double remainingAnnualAggregateDeductible
    private double eventLimit

    ReinsuranceContractType getType() {
        type
    }

    Map getParameters() {
        ["quotaShare": quotaShare,
            "limit": limit,
            "coveredByReinsurer": coveredByReinsurer]
    }

    double allocateCededClaim(Claim inClaim) {
        switch (limit) {
            case NoneLimitStrategy:
                return inClaim.ultimate * quotaShare
            case AalLimitStrategy:
                return calculateCoveredLossApplyingAAL(inClaim)
            case AadLimitStrategy:
                return calculateCoveredLossApplyingAAD(inClaim)
            case AalAadLimitStrategy:
                return calculateCoveredLossApplyingAALAAD(inClaim)
            case EventLimitStrategy:
                return calculateCoveredLossApplyingEventLimit(inClaim)
            case EventAalLimitStrategy:
                return calculateCoveredLossApplyingEventLimitAAL(inClaim)
            default:
                throw new InvalidParameterException("ILimitStrategy $limit not implemented")
        }
    }

    private double calculateCoveredLossApplyingEventLimitAAL(Claim inClaim) {
        if (availableAnnualAggregateLimit > 0) {
            double cededValue = Math.min(inClaim.ultimate * quotaShare, availableAnnualAggregateLimit)
            if (inClaim.claimType.equals(ClaimType.EVENT) || inClaim.claimType.equals(ClaimType.AGGREGATED_EVENT)) {
                cededValue = Math.min(cededValue, eventLimit)
            }
            availableAnnualAggregateLimit -= cededValue
            return cededValue
        }
        return 0d
    }

    private double calculateCoveredLossApplyingEventLimit(Claim inClaim) {
        double cededValue = inClaim.ultimate * quotaShare
        if (inClaim.claimType.equals(ClaimType.EVENT) || inClaim.claimType.equals(ClaimType.AGGREGATED_EVENT)) {
            cededValue = Math.min(cededValue, eventLimit)
        }
        return cededValue
    }

    private double calculateCoveredLossApplyingAALAAD(Claim inClaim) {
        double cededClaim
        if (remainingAnnualAggregateDeductible > 0) {
            // apply the quota share to no more than the available AAL, after subtracting any remaining AAD
            cededClaim = Math.min(availableAnnualAggregateLimit, Math.max(0, inClaim.ultimate - remainingAnnualAggregateDeductible) * quotaShare)
            remainingAnnualAggregateDeductible -= inClaim.ultimate
        }
        else {
            cededClaim = Math.min(availableAnnualAggregateLimit, inClaim.ultimate * quotaShare)
        }
        availableAnnualAggregateLimit -= cededClaim
        return cededClaim
    }

    private double calculateCoveredLossApplyingAAL(Claim inClaim) {
        if (availableAnnualAggregateLimit > 0) {
            double cededClaim = Math.min(inClaim.ultimate * quotaShare, availableAnnualAggregateLimit)
            availableAnnualAggregateLimit -= cededClaim
            return cededClaim
        }
        else {
            return 0d
        }
    }

    private double calculateCoveredLossApplyingAAD(Claim inClaim) {
        double grossAfterAAD
        if (remainingAnnualAggregateDeductible > 0) {
            grossAfterAAD = Math.max(inClaim.ultimate - remainingAnnualAggregateDeductible, 0)
            remainingAnnualAggregateDeductible -= inClaim.ultimate
        }
        else {
            grossAfterAAD = inClaim.ultimate
        }
        return grossAfterAAD * quotaShare
    }

    CededUnderwritingInfo calculateCoverUnderwritingInfo(UnderwritingInfo grossUnderwritingInfo, double initialReserves) {
        CededUnderwritingInfo cededUnderwritingInfo = CededUnderwritingInfoPacketFactory.copy(grossUnderwritingInfo)
        cededUnderwritingInfo.originalUnderwritingInfo = grossUnderwritingInfo?.originalUnderwritingInfo ? grossUnderwritingInfo.originalUnderwritingInfo : grossUnderwritingInfo
        cededUnderwritingInfo.premium *= quotaShare
        cededUnderwritingInfo.fixedPremium = cededUnderwritingInfo.premium
        cededUnderwritingInfo.sumInsured *= quotaShare
        cededUnderwritingInfo.maxSumInsured *= quotaShare 
        cededUnderwritingInfo.commission = 0
        cededUnderwritingInfo.fixedCommission = 0
        cededUnderwritingInfo.variableCommission = 0
        cededUnderwritingInfo.variablePremium = 0
        cededUnderwritingInfo
    }

    void initBookkeepingFigures(List<Claim> inClaims, List<UnderwritingInfo> coverUnderwritingInfo) {
        switch (limit) {
            case AalLimitStrategy:
                availableAnnualAggregateLimit = ((AalLimitStrategy) limit).getAAL()
                break
            case AadLimitStrategy:
                remainingAnnualAggregateDeductible = ((AadLimitStrategy) limit).getAAD()
                break
            case AalAadLimitStrategy:
                remainingAnnualAggregateDeductible = ((AalAadLimitStrategy) limit).getAAD()
                availableAnnualAggregateLimit = ((AalAadLimitStrategy) limit).getAAL()
                break
            case EventLimitStrategy:
                eventLimit = ((EventLimitStrategy) limit).getEventLimit()
                break
            case EventAalLimitStrategy:
                eventLimit = ((EventAalLimitStrategy) limit).getEventLimit()
                availableAnnualAggregateLimit = ((EventAalLimitStrategy) limit).getAAL()
                break
            case NoneLimitStrategy:
                break
            default:
                throw new InvalidParameterException("['QuotaShareContractStrategy.nonImplementedLimits','"+limit.getClass().getName()+"']");
        }
    }
}
