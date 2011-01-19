package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoPacketFactory
import org.pillarone.riskanalytics.domain.pc.constants.LPTPremiumBase
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfoPacketFactory
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfo

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class LossPortfolioTransferContractStrategy extends AbstractContractStrategy implements IReinsuranceContractStrategy, IParameterObject {

    static final ReinsuranceContractType type = ReinsuranceContractType.LOSSPORTFOLIOTRANSFER

    double quotaShare

    private Map<UnderwritingInfo, Double> grossPremiumSharesPerBand = [:]

    /** Premium can be expressed as a fraction of a base quantity.           */
    LPTPremiumBase premiumBase = LPTPremiumBase.ABSOLUTE

    /** Premium as a percentage of the premium base           */
    double premium

    ReinsuranceContractType getType() {
        type
    }

    Map getParameters() {
        ["quotaShare": quotaShare,
         "premiumBase": premiumBase,
         "premium": premium,
         "coveredByReinsurer": coveredByReinsurer]
    }

    void initBookkeepingFigures(List<Claim> inClaims, List<UnderwritingInfo> coverUnderwritingInfo) {
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
    }

    double allocateCededClaim(Claim inClaim) {
        inClaim.ultimate * quotaShare * coveredByReinsurer
    }

    CededUnderwritingInfo calculateCoverUnderwritingInfo(UnderwritingInfo grossUnderwritingInfo, double initialReserves) {
        CededUnderwritingInfo cededUnderwritingInfo = CededUnderwritingInfoPacketFactory.copy(grossUnderwritingInfo)
        cededUnderwritingInfo.originalUnderwritingInfo = grossUnderwritingInfo?.originalUnderwritingInfo ? grossUnderwritingInfo.originalUnderwritingInfo : grossUnderwritingInfo
        cededUnderwritingInfo.sumInsured *= quotaShare * coveredByReinsurer
        cededUnderwritingInfo.maxSumInsured *= quotaShare * coveredByReinsurer
        cededUnderwritingInfo.commission = 0
        cededUnderwritingInfo.fixedCommission = 0d
        cededUnderwritingInfo.variableCommission = 0d
        cededUnderwritingInfo.variablePremium = 0d
        switch (premiumBase) {
            case LPTPremiumBase.ABSOLUTE:
                cededUnderwritingInfo.premium = premium * grossPremiumSharesPerBand.get(grossUnderwritingInfo)
                break
            case LPTPremiumBase.RELATIVE_TO_CEDED_RESERVES_VOLUME:
                cededUnderwritingInfo.premium =
                    initialReserves * quotaShare * coveredByReinsurer * grossPremiumSharesPerBand.get(grossUnderwritingInfo)
                break
            default:
                throw new IllegalArgumentException("['LossPortfolioTransferContractStrategy.invalidPremiumBaseType','"+premiumBase+"']")
        }
        cededUnderwritingInfo.fixedPremium = cededUnderwritingInfo.premium        
        cededUnderwritingInfo
    }
}