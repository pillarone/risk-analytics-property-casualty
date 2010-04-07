package org.pillarone.riskanalytics.domain.pc.lob

import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory
import org.pillarone.riskanalytics.domain.utils.ClaimSizeDistributionType
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskAllocatorStrategyFactory
import org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskAllocatorType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.QuotaShareContractStrategyTests
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.WXLContractStrategyTests
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.StopLossContractStrategyTests
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.DistributionModifierFactory

class ExampleLobTests extends GroovyTestCase {

    void testWiring() {
        double attritionalClaim = 100
        double largeClaim = 20
        double numberOfLargeClaims = 10

        ExampleLob lob = new ExampleLob()
        lob.internalWiring()

        lob.subUnderwriting.parmUnderwritingInformation = new TableMultiDimensionalParameter(
                [[1000d, 10000d, 100000d], [800d, 8000d, 80000d], [50000d, 70000d, 90000d], [1000, 200, 30]],
                ['maximum sum insured', 'average sum insured', 'premium', 'number of policies/risks'],
                )

        lob.subClaimsGenerator.subAttritionalClaimsGenerator.parmDistribution = RandomDistributionFactory.getDistribution(ClaimSizeDistributionType.CONSTANT, ["constant": attritionalClaim])
        lob.subClaimsGenerator.subSingleClaimsGenerator.subFrequencyGenerator.parmDistribution = RandomDistributionFactory.getDistribution(DistributionType.CONSTANT, ["constant": numberOfLargeClaims])
        lob.subClaimsGenerator.subSingleClaimsGenerator.subClaimsGenerator.parmDistribution = RandomDistributionFactory.getDistribution(ClaimSizeDistributionType.CONSTANT, ["constant": largeClaim])
        lob.subRiProgram.subContract1.parmContractStrategy = QuotaShareContractStrategyTests.getQuotaShareContract(0.5).parmContractStrategy
        lob.subRiProgram.subContract2.parmContractStrategy = WXLContractStrategyTests.getContract0().parmContractStrategy
        lob.subRiProgram.subContract3.parmContractStrategy = StopLossContractStrategyTests.getContractSL0().parmContractStrategy
        lob.subAllocator.parmRiskAllocatorStrategy=RiskAllocatorStrategyFactory.getAllocatorStrategy(RiskAllocatorType.SUMINSUREDGENERATOR, [
                distribution: RandomDistributionFactory.getDistribution(DistributionType.NORMAL, ["mean": 0d, "stDev": 1d]),
                modification: DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:]),
                bandMean: 1d/3d])
        lob.validateParameterization()

        List frequency = new TestProbe(lob.subClaimsGenerator.subSingleClaimsGenerator.subFrequencyGenerator, "outFrequency").result
        List claimsGross = new TestProbe(lob.subClaimsGenerator, "outClaims").result
        List claimsCededQS = new TestProbe(lob.subRiProgram.subContract1, "outCoveredClaims").result
        List claimsCeded = new TestProbe(lob.subRiProgram, "outClaimsCeded").result
        List claimsNet = new TestProbe(lob.subRiProgram, "outClaimsNet").result

        assertTrue frequency.isEmpty()
        assertTrue claimsGross.isEmpty()
        assertTrue claimsCededQS.isEmpty()

        lob.start()

        assertEquals "frequency size", 1, frequency.size()
        double numberOfClaims = frequency[0].value + 1        // +1 attritional
        assertEquals "claimsGross.size", numberOfClaims, claimsGross.size()
        assertEquals "claimsCededQS.size", numberOfClaims, claimsCededQS.size()
        assertEquals "number of ceded claims", numberOfClaims, claimsCeded.size()
        assertEquals "number of net claims", numberOfClaims, claimsNet.size()
    }
}