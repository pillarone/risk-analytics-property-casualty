package org.pillarone.riskanalytics.domain.pc.claims

import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.underwriting.ExposureInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.utils.*

/**
 * @author jdittrich (at) munichre (dot) com
 * @author Michael-Noe (at) web (dot) de
 */

// TODO event claims not (yet) considered
// TODO discuss whether for the attritional losses it would be sufficient to first do an aggregation
// TODO discuss: the order of the outclaims is no longer the same as the claims!
class SumInsuredGeneratorRiskAllocatorStrategy implements IRiskAllocatorStrategy, IParameterObject {

    IRandomNumberGenerator generator
    RandomDistribution distribution = DistributionType.getStrategy(DistributionType.TRIANGULARDIST, ["a": 0d, "b": 1d, "m": 0.01])

    DistributionModified modification = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])

    double bandMean = 1d / 3d

    public IParameterObjectClassifier getType() {
        return RiskAllocatorType.SUMINSUREDGENERATOR
    }

    public Map getParameters() {
        ["distribution": distribution,
                "modification": modification,
                "bandMean": bandMean]
    }

    /**
     * Sets exposure information for each claim.
     * Claims of type other than SINGLE have trivial exposure info values.
     */
    public PacketList<Claim> getAllocatedClaims(List<Claim> claims, List<UnderwritingInfo> underwritingInfos) {
        generator = RandomNumberGeneratorFactory.getGenerator(distribution, modification)
        // get Maximum Sum Insured (MSI) from upper bound of risk profiles
        double maxSumInsuredUWI = underwritingInfos[-1].maxSumInsured
        PacketList<Claim> allocatedClaims = new PacketList(Claim)
        // calculate/construct exposure for each claim
        for (Claim claim : claims) {
            if (claim.hasExposureInfo()) throw new IllegalArgumentException("Can't re-define claim's exposure")
            ExposureInfo exposure = new ExposureInfo()
            exposure.exposureDefinition = Exposure.ABSOLUTE
//            if (claim.claimType == ClaimType.SINGLE) {
                exposure.maxSumInsured = maxSumInsuredUWI
                exposure.sumInsured = claim.ultimate + generator.nextValue() * (maxSumInsuredUWI - claim.ultimate)
//            }
            claim.exposure = exposure
            allocatedClaims << claim
        }
        allocatedClaims
    }
}