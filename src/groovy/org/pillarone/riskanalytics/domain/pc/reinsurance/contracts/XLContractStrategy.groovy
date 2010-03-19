package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoPacketFactory

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
abstract class XLContractStrategy extends AbstractContractStrategy implements IReinsuranceContractStrategy, IParameterObject {

    /** Premium can be expressed as a fraction of a base quantity.                 */
    PremiumBase premiumBase = PremiumBase.ABSOLUTE

    /** Premium as a percentage of the premium base                 */
    double premium

    /** As a percentage of premium.                 */
    AbstractMultiDimensionalParameter reinstatementPremiums = new TableMultiDimensionalParameter([0d], ['Reinstatement Premium'])
    double attachmentPoint
    double limit
    double aggregateLimit
    Map<UnderwritingInfo, Double> grossPremiumSharesPerBand = [:]

    protected double availableAggregateLimit //  A field which may become relevant once the WORM will be available...
    protected double reinstatements

    public Map getParameters() {
        return ["premiumBase": premiumBase,
            "premium": premium,
            "reinstatementPremiums": reinstatementPremiums,
            "attachmentPoint": attachmentPoint,
            "limit": limit,
            "aggregateLimit": aggregateLimit,
            "coveredByReinsurer": coveredByReinsurer]
    }

    abstract ReinsuranceContractType getType()

    abstract double calculateCoveredLoss(Claim inClaim)

    protected double calculateUsedReinstatements() {
        return Math.min((aggregateLimit - availableAggregateLimit) / limit, reinstatements)
    }

    protected double calculateReinstatementPremiums() {
        double usedReinstatements = calculateUsedReinstatements()
        double reinstatementPremium = 0d
        for (int i = 1; i <= usedReinstatements; i++) {
            reinstatementPremium += getReinstatementPremiumFactor(i)
        }
        double partialReinstatement = usedReinstatements - Math.floor(usedReinstatements)
        reinstatementPremium += partialReinstatement * getReinstatementPremiumFactor(Math.ceil(usedReinstatements).toInteger())
        reinstatementPremium
    }

    protected double getReinstatementPremiumFactor(int reinstatement) {
        reinstatementPremiums.values[Math.min(reinstatement, reinstatementPremiums.valueRowCount - 1)] * coveredByReinsurer
    }

    void initBookkeepingFigures(List<Claim> inClaims, List<UnderwritingInfo> coverUnderwritingInfo) {
        availableAggregateLimit = aggregateLimit
        reinstatements = availableAggregateLimit / limit - 1
        double totalPremium = coverUnderwritingInfo.premiumWritten.sum()
        if (totalPremium == 0) {
            for (UnderwritingInfo underwritingInfo: coverUnderwritingInfo) {
                grossPremiumSharesPerBand.put(underwritingInfo, 0)
            }
        }
        else {
            for (UnderwritingInfo underwritingInfo: coverUnderwritingInfo) {
                grossPremiumSharesPerBand.put(underwritingInfo, underwritingInfo.premiumWritten / totalPremium)
            }
        }
    }

    // todo: Are the definition for the as-if premium reasonable?
    // todo(sku): try to move it in an upper class
    UnderwritingInfo calculateCoverUnderwritingInfo(UnderwritingInfo grossUnderwritingInfo, double initialReserves) {
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
                cededUnderwritingInfo.premiumWritten = premium * limit
                cededUnderwritingInfo.premiumWrittenAsIf = premium * limit
                break
            case PremiumBase.NUMBER_OF_POLICIES:
                throw new IllegalArgumentException("Defining the premium base as number of policies is not suppported.")
        }
        // Increases premium written and premium written as if with the reinstatement premium
        double factor = 1 + calculateReinstatementPremiums()
        cededUnderwritingInfo.premiumWritten *= factor
        cededUnderwritingInfo.premiumWrittenAsIf *= factor
        return cededUnderwritingInfo
    }

    public void resetMemberInstances() {
        grossPremiumSharesPerBand.clear()
    }
}