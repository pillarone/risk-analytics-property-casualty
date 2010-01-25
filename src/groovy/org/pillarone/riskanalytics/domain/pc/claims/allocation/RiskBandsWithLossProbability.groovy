package org.pillarone.riskanalytics.domain.pc.claims.allocation

import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.constants.RiskBandAllocationBase
import org.pillarone.riskanalytics.domain.pc.allocators.AllocationTable
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoWithLossProbability

/**
 * @author: Michael-Noe (at) Web (dot) de
 */
class RiskBandsWithLossProbability extends RiskBands {
    static Map<RiskBandAllocationBase, String> singleAllocationBaseColumnName = [
        (RiskBandAllocationBase.PREMIUM): 'premium',
        (RiskBandAllocationBase.NUMBER_OF_POLICIES): 'number of policies/risks',
        (RiskBandAllocationBase.LOSS_PROBABILITY): 'loss probability',
        (RiskBandAllocationBase.CUSTOM): 'custom allocation number of single claims'
    ]

    static Map<RiskBandAllocationBase, String> attritionalAllocationBaseColumnName = [
        (RiskBandAllocationBase.PREMIUM): 'premium',
        (RiskBandAllocationBase.NUMBER_OF_POLICIES): 'number of policies/risks',
        (RiskBandAllocationBase.CUSTOM): 'custom allocation attritional claims'
    ]

    AbstractMultiDimensionalParameter parmUnderwritingInformation = new TableMultiDimensionalParameter([],
        ['maximum sum insured',
            'average sum insured',
            singleAllocationBaseColumnName.get(RiskBandAllocationBase.PREMIUM),
            singleAllocationBaseColumnName.get(RiskBandAllocationBase.NUMBER_OF_POLICIES),
            singleAllocationBaseColumnName.get(RiskBandAllocationBase.LOSS_PROBABILITY),
            singleAllocationBaseColumnName.get(RiskBandAllocationBase.CUSTOM),
            attritionalAllocationBaseColumnName.get(RiskBandAllocationBase.CUSTOM)]
    )

    RiskBandAllocationBase parmAllocationBaseAttritionalClaims = RiskBandAllocationBase.PREMIUM
    RiskBandAllocationBase parmAllocationBaseSingleClaims = RiskBandAllocationBase.NUMBER_OF_POLICIES

    PacketList<UnderwritingInfoWithLossProbability> outUnderwritingInfoWithLossProbability = new PacketList(UnderwritingInfoWithLossProbability)
    PacketList<AllocationTable> outSingleTargetDistribution = new PacketList(AllocationTable)
    PacketList<AllocationTable> outAttritionalTargetDistribution = new PacketList(AllocationTable)


    public void doCalculation() {
        // todo (sku): it is not necessary to execute this code for every iteration, once per period would
        //             be sufficient
        for (int i = 1; i < parmUnderwritingInformation.rowCount; i++) {
            UnderwritingInfoWithLossProbability underwritingInfoWLP = new UnderwritingInfoWithLossProbability()
            // todo : safer column selection
            // todo (mno): Look at the parameters which can be treated by the class RiskBands
            underwritingInfoWLP.premiumWritten = (Double) parmUnderwritingInformation.getValueAt(i, 2)
            underwritingInfoWLP.premiumWrittenAsIf = underwritingInfoWLP.premiumWritten
            underwritingInfoWLP.maxSumInsured = (Double) parmUnderwritingInformation.getValueAt(i, 0)
            underwritingInfoWLP.sumInsured = (Double) parmUnderwritingInformation.getValueAt(i, 1)
            underwritingInfoWLP.lossProbability = (Double) parmUnderwritingInformation.getValueAt(i, 4)
            underwritingInfoWLP.numberOfPolicies = (Double) parmUnderwritingInformation.getValueAt(i, 3)
            underwritingInfoWLP.origin = this
            outUnderwritingInfoWithLossProbability << underwritingInfoWLP
        }

        AbstractMultiDimensionalParameter singleAllocationTable = new TableMultiDimensionalParameter(
            [parmUnderwritingInformation.getColumnByName('maximum sum insured'),
                parmUnderwritingInformation.getColumnByName(singleAllocationBaseColumnName.get(parmAllocationBaseSingleClaims))],
            ['maximum sum insured', 'portion']
        )
        outSingleTargetDistribution << new AllocationTable(table: singleAllocationTable)

        AbstractMultiDimensionalParameter attritionalAllocationTable = new TableMultiDimensionalParameter(
            [parmUnderwritingInformation.getColumnByName('maximum sum insured'),
                parmUnderwritingInformation.getColumnByName(attritionalAllocationBaseColumnName.get(parmAllocationBaseAttritionalClaims))],
            ['maximum sum insured', 'portion']
        )
        outAttritionalTargetDistribution << new AllocationTable(table: attritionalAllocationTable)
    }

}