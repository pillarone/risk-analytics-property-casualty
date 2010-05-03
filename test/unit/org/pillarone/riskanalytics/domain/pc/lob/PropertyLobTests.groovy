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
        lob.wire()

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
                "reinstatementPremium": new TableMultiDimensionalParameter([0.5], ['Reinstatement Premium']),
                "coveredByReinsurer": 1d])
        lob.subRiProgram.subContract3.parmContractStrategy = ReinsuranceContractType.getStrategy(
                ReinsuranceContractType.STOPLOSS,
                ["stopLossContractBase": StopLossContractBase.ABSOLUTE,
                        "attachmentPoint": slAttachmentPoint,
                        "limit": slLimit,
                        "premium": 40,
                        "coveredByReinsurer": 1d])
        lob.subAllocator.parmRiskAllocatorStrategy = RiskAllocatorType.getStrategy(RiskAllocatorType.SUMINSUREDGENERATOR, [
            distribution: DistributionType.getStrategy(DistributionType.NORMAL, ["mean": 0d, "stDev": 1d]),
            modification: DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
            bandMean: 1d / 3d])
        List claimsGross = []
        def probeClaimsGross = [transmit: {-> claimsGross.addAll(lob.subClaimsGenerator.outClaims)}] as ITransmitter
        lob.subClaimsGenerator.allOutputTransmitter << probeClaimsGross

        List claimsNetWXL = []
        def probeClaimsNetWXL = [transmit: {-> claimsNetWXL.addAll(lob.subRiProgram.subContract1.outUncoveredClaims)}] as ITransmitter
        lob.subRiProgram.subContract1.allOutputTransmitter << probeClaimsNetWXL

        List claimsNetQS = []
        def probeClaimsNetQS = [transmit: {-> claimsNetQS.addAll(lob.subRiProgram.subContract2.outUncoveredClaims)}] as ITransmitter
        lob.subRiProgram.subContract2.allOutputTransmitter << probeClaimsNetQS

        List claimsNet = []
        def probeClaimsNet = [transmit: {-> claimsNet.addAll(lob.subRiProgram.outClaimsNet)}] as ITransmitter
        lob.subRiProgram.allOutputTransmitter << probeClaimsNet

        lob.validateParameterization()
        lob.start()

        for (int i = 0; i < claimsGross.size(); i++) {
            assertTrue claimsNetQS[i].ultimate == (claimsGross[i].ultimate * (1.0 - quotaShare))
        }
    }
}