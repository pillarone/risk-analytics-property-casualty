package org.pillarone.riskanalytics.domain.pc.claims.allocation


import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.utils.IRandomNumberGenerator
import org.pillarone.riskanalytics.domain.utils.RandomDistribution
import org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.DistributionModified
import org.pillarone.riskanalytics.domain.utils.DistributionModifierFactory
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.allocators.AllocationTable
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.utils.RandomNumberGeneratorFactory
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.claims.ClaimWithExposure
import org.pillarone.riskanalytics.domain.pc.claims.ClaimUtilities
import org.pillarone.riskanalytics.domain.pc.underwriting.ExposureInfo
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier

/**
 * @deprecated newer version available in domain.pc.claims package
 *
 * @author jdittrich (at) munichre (dot) com
 * @author Michael-Noe (at) web (dot) de
 */

// TODO event claims not (yet) considered
// TODO discuss whether for the attritional losses it would be sufficient to first do an aggregation
// TODO discuss: the order of the outclaims is no longer the same as the claims!
@Deprecated
class SumInsuredGeneratorRiskAllocatorStrategy implements IRiskAllocatorStrategy, IParameterObject {

    IRandomNumberGenerator generator
    RandomDistribution distribution = RandomDistributionFactory.getDistribution(DistributionType.TRIANGULARDIST, ["a": 0d, "b": 1d, "m": 0.01])

    DistributionModified modification = DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:])

    double bandMean = 1d / 3d

    public IParameterObjectClassifier getType() {
        return RiskAllocatorType.SUMINSUREDGENERATOR
    }

    public Map getParameters() {
        ["distribution": distribution,
                "modification": modification,
                "bandMean": bandMean]
    }

    public PacketList<Claim> getAllocatedClaims(List<Claim> claims, List<AllocationTable> targetDistribution, List<UnderwritingInfo> underwritingInfos) {
        generator = RandomNumberGeneratorFactory.getGenerator(distribution, modification)
        /*Here, we get the Maxium Sum Insured (MSI) from the upper bound of the risk profiles*/
        double maxSumInsuredUWI = underwritingInfos.get(underwritingInfos.size() - 1).maxSumInsured
        PacketList<Claim> allocatedClaims = new PacketList(Claim)
        claims.each {Claim claim ->
            // Connect the Claims with the ExposureInformation
            switch (claim.claimType) {
                case ClaimType.SINGLE:     /*In this part only the single claims will be used*/
                    ClaimWithExposure claimExp = ClaimUtilities.getClaimWithExposure(claim)                             /*We generate a new ClaimWithExposure. The ClaimInformation, is given by the variable claim.*/
                    ExposureInfo expInfo = new ExposureInfo()                                                           /*The ExposreInfo will be taken, in the new Object: expInfo*/
                    expInfo.maxSumInsured = maxSumInsuredUWI                                                            /*The MSI is written, in the claimExp*/
                    expInfo.exposureDefinition = Exposure.ABSOLUTE                                                      /*The exposureDefinition is written in the claimExp*/
                    expInfo.sumInsured = claimExp.ultimate + generator.nextValue() * (maxSumInsuredUWI - claimExp.ultimate)   /*In this line, the MIL is generated, and written in the claimExp*/

                    claimExp.exposure = expInfo                                                                         /*Here, the claim will be connected with his exposreInfo*/
                    allocatedClaims << claimExp                                                                         /*Last but not least, the claim with exposre definition, is taken in the list outClaims*/
                    break
                case ClaimType.ATTRITIONAL:                                                                                 /*In this part the attritional claims will be used*/
                    ClaimWithExposure claimExp = ClaimUtilities.getClaimWithExposure(claim)                             /*We generate a new ClaimWithExposure. The ClaimInformation, is given by the variable claim.*/
                    ExposureInfo expInfo = new ExposureInfo()                                                           /*The ExposreInfo will be taken, in the new Object: expInfo*/
                    expInfo.exposureDefinition = Exposure.ABSOLUTE                                                      /*The exposureDefinition is written in the claimExp*/
                    /* We don't need the MIL, because it's an attritional claim*/
                    claimExp.exposure = expInfo                                                                         /*Here, the claim will be connected with his exposreInfo*/
                    allocatedClaims << claimExp                                                                         /*Last but not least, the claim with exposre definition, is taken in the list outClaims*/
                    break
                default:
                    ClaimWithExposure claimExp = ClaimUtilities.getClaimWithExposure(claim)
                    ExposureInfo expInfo = new ExposureInfo()
                    expInfo.exposureDefinition = Exposure.ABSOLUTE
                    claimExp.exposure = expInfo
                    allocatedClaims << claimExp

                    break
            }
        }
        allocatedClaims
    }
}