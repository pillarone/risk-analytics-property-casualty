package org.pillarone.riskanalytics.domain.pc.lob

import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.wiring.ITransmitter
import org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskAllocatorType
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.utils.ClaimSizeDistributionType
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.pc.constants.StopLossContractBase
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.PremiumAllocationType

class PropertyLobTests extends GroovyTestCase {

    void testWiring() {
        double attritionalClaim = 100
        double largeClaim = 20
        double numberOfLargeClaims = 10

        double quotaShare = 0.3
        double wxlAggregateLimit = 30
        double wxlAttachmentPoint = 21
        double wxlLimit = 20
        double wxlPremium = 50
        double reinstatementPremium = 0.5
        double slAttachmentPoint = 50
        double slLimit = 50

        PropertyLob lob = new PropertyLob()

        lob.internalWiring()
        SimulationScope simulationScope = new SimulationScope(iterationScope: new IterationScope(periodScope: new PeriodScope()))
        lob.subRiProgram.subContract1.simulationScope = simulationScope
        lob.subRiProgram.subContract2.simulationScope = simulationScope
        lob.subRiProgram.subContract3.simulationScope = simulationScope
        lob.subRiProgram.subContract4.simulationScope = simulationScope
        lob.subRiProgram.subContract5.simulationScope = simulationScope

        lob.subUnderwriting.parmUnderwritingInformation = new TableMultiDimensionalParameter(
            [[0], [0], [10000d], [0]],
            ['maximum sum insured', 'average sum insured', 'premium', 'number of policies/risks'],
        )
        lob.subClaimsGenerator.subSingleClaimsGenerator.subFrequencyGenerator.parmDistribution = DistributionType.getStrategy(ClaimSizeDistributionType.CONSTANT, ["constant": numberOfLargeClaims])
        lob.subClaimsGenerator.subSingleClaimsGenerator.subClaimsGenerator.parmDistribution = DistributionType.getStrategy(ClaimSizeDistributionType.CONSTANT, ["constant": largeClaim])
        lob.subClaimsGenerator.subEQGenerator.subFrequencyGenerator.parmDistribution = DistributionType.getStrategy(ClaimSizeDistributionType.CONSTANT, ["constant": numberOfLargeClaims])
        lob.subClaimsGenerator.subEQGenerator.subClaimsGenerator.parmDistribution = DistributionType.getStrategy(ClaimSizeDistributionType.CONSTANT, ["constant": largeClaim])
        lob.subClaimsGenerator.subFloodGenerator.subFrequencyGenerator.parmDistribution = DistributionType.getStrategy(ClaimSizeDistributionType.CONSTANT, ["constant": numberOfLargeClaims])
        lob.subClaimsGenerator.subFloodGenerator.subClaimsGenerator.parmDistribution = DistributionType.getStrategy(ClaimSizeDistributionType.CONSTANT, ["constant": largeClaim])
        lob.subClaimsGenerator.subStormGenerator.subFrequencyGenerator.parmDistribution = DistributionType.getStrategy(ClaimSizeDistributionType.CONSTANT, ["constant": numberOfLargeClaims])
        lob.subClaimsGenerator.subStormGenerator.subClaimsGenerator.parmDistribution = DistributionType.getStrategy(ClaimSizeDistributionType.CONSTANT, ["constant": largeClaim])

        lob.subRiProgram.subContract1.parmContractStrategy = ReinsuranceContractType.getStrategy(
            ReinsuranceContractType.QUOTASHARE,
            ["quotaShare": quotaShare,
                "coveredByReinsurer": 1d])
        lob.subRiProgram.subContract2.parmContractStrategy = ReinsuranceContractType.getStrategy(
            ReinsuranceContractType.WXL,
            ["attachmentPoint": wxlAttachmentPoint,
                "limit": wxlLimit,
                "aggregateLimit": wxlAggregateLimit,
                "premiumBase": PremiumBase.ABSOLUTE,
                "premium": wxlPremium,
                "reinstatementPremiums": new TableMultiDimensionalParameter([0.5], ['Reinstatement Premium']),
                "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),
                "coveredByReinsurer": 1d])
        lob.subRiProgram.subContract3.parmContractStrategy = ReinsuranceContractType.getStrategy(
                ReinsuranceContractType.STOPLOSS,
                ["stopLossContractBase": StopLossContractBase.ABSOLUTE,
                        "attachmentPoint": slAttachmentPoint,
                        "limit": slLimit,
                        "premium": 40,
                        "coveredByReinsurer": 1d,
                        "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:])])
        lob.subAllocator.parmRiskAllocatorStrategy = RiskAllocatorType.getStrategy(RiskAllocatorType.SUMINSUREDGENERATOR, [
            distribution: DistributionType.getStrategy(DistributionType.NORMAL, ["mean": 0d, "stDev": 1d]),
            modification: DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
            bandMean: 1d / 3d])
        List claimsGross = new TestProbe(lob.subClaimsGenerator, 'outClaims').result
        List claimsNetWXL = new TestProbe(lob.subRiProgram.subContract1, 'outUncoveredClaims').result
        List claimsNetQS = new TestProbe(lob.subRiProgram.subContract2, 'outUncoveredClaims').result
        List claimsNet = new TestProbe(lob.subRiProgram, 'outClaimsNet').result

        lob.validateParameterization()
        lob.start()
        
        assertTrue "collected gross claims", claimsGross.size() > 0

        // comparing individual elements not possible due to different order of gross and ceded claims
        assertTrue "matching sum", claimsNetQS.ultimate.sum() == (claimsGross.ultimate.sum() * (1.0 - quotaShare))
    }
}