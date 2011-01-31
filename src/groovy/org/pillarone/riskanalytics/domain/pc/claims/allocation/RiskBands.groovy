package org.pillarone.riskanalytics.domain.pc.claims.allocation

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.allocators.AllocationTable
import org.pillarone.riskanalytics.domain.pc.constants.RiskBandAllocationBase
import org.pillarone.riskanalytics.domain.pc.underwriting.IUnderwritingInfoMarker
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoPacketFactory

/**
 *
 * @deprecated newer version available in domain.pc.underwriting
 * @author martin.melchior (at) fhnw (dot) ch, stefan.kunz (at) intuitive-collaboration (dot) com
 */
@Deprecated
class RiskBands extends Component implements IUnderwritingInfoMarker {

    static Map<RiskBandAllocationBase, String> singleAllocationBaseColumnName = [
            (RiskBandAllocationBase.PREMIUM): 'premium',
            (RiskBandAllocationBase.NUMBER_OF_POLICIES): 'number of policies/risks',
            (RiskBandAllocationBase.CUSTOM): 'custom allocation number of single claims'
    ]

    static Map<RiskBandAllocationBase, String> attritionalAllocationBaseColumnName = [
            (RiskBandAllocationBase.PREMIUM): 'premium',
            (RiskBandAllocationBase.NUMBER_OF_POLICIES): 'number of policies/risks',
            (RiskBandAllocationBase.CUSTOM): 'custom allocation attritional claims'
    ]

    AbstractMultiDimensionalParameter parmUnderwritingInformation = new TableMultiDimensionalParameter([[0d], [0d], [0d], [0d], [0d], [0d]],
            ['maximum sum insured',
                    'average sum insured',
                    singleAllocationBaseColumnName.get(RiskBandAllocationBase.PREMIUM),
                    singleAllocationBaseColumnName.get(RiskBandAllocationBase.NUMBER_OF_POLICIES),
                    singleAllocationBaseColumnName.get(RiskBandAllocationBase.CUSTOM),
                    attritionalAllocationBaseColumnName.get(RiskBandAllocationBase.CUSTOM)]
    )

    RiskBandAllocationBase parmAllocationBaseAttritionalClaims = RiskBandAllocationBase.PREMIUM
    RiskBandAllocationBase parmAllocationBaseSingleClaims = RiskBandAllocationBase.NUMBER_OF_POLICIES

    PacketList<UnderwritingInfo> outUnderwritingInfo = new PacketList(UnderwritingInfo)
    PacketList<AllocationTable> outSingleTargetDistribution = new PacketList(AllocationTable)
    PacketList<AllocationTable> outAttritionalTargetDistribution = new PacketList(AllocationTable)


    public void doCalculation() {
        // todo (sku): it is not necessary to execute this code for every iteration, once per period would
        //             be sufficient
        for (int i = 1; i < parmUnderwritingInformation.rowCount; i++) {
            UnderwritingInfo underwritingInfo = UnderwritingInfoPacketFactory.createPacket()
            // todo : safer column selection
            underwritingInfo.premium = parmUnderwritingInformation.getValueAt(i, 2)
            underwritingInfo.maxSumInsured = parmUnderwritingInformation.getValueAt(i, 0)
            underwritingInfo.sumInsured = parmUnderwritingInformation.getValueAt(i, 1)
            underwritingInfo.numberOfPolicies = parmUnderwritingInformation.getValueAt(i, 3)
            underwritingInfo.origin = this
            underwritingInfo.originalUnderwritingInfo = underwritingInfo
            outUnderwritingInfo << underwritingInfo
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