package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoPacketFactory
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.ILimitStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.LimitStrategyType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.AalAadLimitStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.AadLimitStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.AalLimitStrategy
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.EventLimitStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.EventAalLimitStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.NoneLimitStrategy

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class QuotaShareContractStrategy extends AbstractContractStrategy implements IReinsuranceContractStrategy, IParameterObject {

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
                return inClaim.ultimate * quotaShare * coveredByReinsurer
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
        }
    }

    private double calculateCoveredLossApplyingEventLimitAAL(Claim inClaim) {
        if (availableAnnualAggregateLimit > 0) {
            double cededValue = Math.min(inClaim.ultimate * quotaShare * coveredByReinsurer, availableAnnualAggregateLimit)
            if (inClaim.claimType.equals(ClaimType.EVENT) || inClaim.claimType.equals(ClaimType.AGGREGATED_EVENT)) {
                cededValue = Math.min(cededValue, eventLimit)
            }
            availableAnnualAggregateLimit -= cededValue
            return cededValue
        }
        return 0d
    }

    private double calculateCoveredLossApplyingEventLimit(Claim inClaim) {
        double cededValue = inClaim.ultimate * quotaShare * coveredByReinsurer
        if (inClaim.claimType.equals(ClaimType.EVENT) || inClaim.claimType.equals(ClaimType.AGGREGATED_EVENT)) {
            cededValue = Math.min(cededValue, eventLimit)
        }
        return cededValue
    }

    private double calculateCoveredLossApplyingAALAAD(Claim inClaim) {
        if (remainingAnnualAggregateDeductible > 0) {
            double cededClaim = Math.min(Math.max(inClaim.ultimate - remainingAnnualAggregateDeductible, 0) * quotaShare, availableAnnualAggregateLimit) * coveredByReinsurer
            remainingAnnualAggregateDeductible -= inClaim.ultimate
            availableAnnualAggregateLimit -= cededClaim
            return cededClaim
        }
        else {
            double cededClaim = Math.min(inClaim.ultimate * quotaShare, availableAnnualAggregateLimit) * coveredByReinsurer
            availableAnnualAggregateLimit -= cededClaim
            return cededClaim
        }
    }

    private double calculateCoveredLossApplyingAAL(Claim inClaim) {
        if (availableAnnualAggregateLimit > 0) {
            double cededClaim = Math.min(inClaim.ultimate * quotaShare, availableAnnualAggregateLimit) * coveredByReinsurer
            availableAnnualAggregateLimit -= cededClaim
            return cededClaim
        }
        else {
            return 0d
        }
    }

    private double calculateCoveredLossApplyingAAD(Claim inClaim) {
        if (remainingAnnualAggregateDeductible > 0) {
            remainingAnnualAggregateDeductible -= inClaim.ultimate
            if (remainingAnnualAggregateDeductible < 0) {
                return -remainingAnnualAggregateDeductible * quotaShare * coveredByReinsurer
            }
            return 0.0
        }
        else {
            double cededClaim = inClaim.ultimate * quotaShare * coveredByReinsurer
            return cededClaim
        }
    }

    UnderwritingInfo calculateCoverUnderwritingInfo(UnderwritingInfo grossUnderwritingInfo, double initialReserves) {
        UnderwritingInfo cededUnderwritingInfo = UnderwritingInfoPacketFactory.copy(grossUnderwritingInfo)
        cededUnderwritingInfo.originalUnderwritingInfo = grossUnderwritingInfo?.originalUnderwritingInfo ? grossUnderwritingInfo.originalUnderwritingInfo : grossUnderwritingInfo
        cededUnderwritingInfo.premiumWritten *= quotaShare * coveredByReinsurer
        cededUnderwritingInfo.premiumWrittenAsIf *= quotaShare * coveredByReinsurer
        cededUnderwritingInfo.sumInsured *= quotaShare * coveredByReinsurer
        cededUnderwritingInfo.maxSumInsured *= quotaShare * coveredByReinsurer
        cededUnderwritingInfo.commission = 0
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
        }
    }
}