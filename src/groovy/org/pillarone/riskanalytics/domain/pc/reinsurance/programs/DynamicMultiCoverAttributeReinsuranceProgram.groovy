package org.pillarone.riskanalytics.domain.pc.reinsurance.programs

import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.domain.pc.claims.MarketClaimsMerger
import org.pillarone.riskanalytics.domain.pc.constants.IncludeType
import org.pillarone.riskanalytics.domain.pc.constants.ReinsuranceContractBase
import org.pillarone.riskanalytics.domain.pc.reinsurance.ReinsuranceResultWithCommissionPacket
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.MultiCoverAttributeReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.CoverAttributeStrategyType
import org.pillarone.riskanalytics.domain.pc.underwriting.MarketUnderwritingInfoMerger
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.XLContractStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.StopLossContractStrategy
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.domain.pc.constants.StopLossContractBase
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.AggregateXLContractStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.GoldorakContractStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.AdverseDevelopmentCoverContractStrategy

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class DynamicMultiCoverAttributeReinsuranceProgram extends DynamicReinsuranceProgram {

    PacketList<ReinsuranceResultWithCommissionPacket> outContractFinancials = new PacketList<ReinsuranceResultWithCommissionPacket>(ReinsuranceResultWithCommissionPacket.class);

    public MultiCoverAttributeReinsuranceContract createDefaultSubComponent() {
        MultiCoverAttributeReinsuranceContract contract = new MultiCoverAttributeReinsuranceContract(
                parmInuringPriority: 0,
                parmContractStrategy: ReinsuranceContractType.getStrategy(ReinsuranceContractType.TRIVIAL, [:]),
                parmCover: CoverAttributeStrategyType.getStrategy(
                        CoverAttributeStrategyType.ALL, ['reserves': IncludeType.NOTINCLUDED]))
        return contract
    }

    public String getGenericSubComponentName() {
        'riContract'
    }

    void wire() {
        super.wire()
        replicateOutChannels this, 'outContractFinancials'
    }

    protected void wireContractInClaims(ReinsuranceContract contract, MarketClaimsMerger claimsMerger) {
        if (((MultiCoverAttributeReinsuranceContract) contract).parmBasedOn.equals(ReinsuranceContractBase.NET)) {
            doWire WireCategory, contract, 'inClaims', claimsMerger, 'outClaimsNet'
        }
        else if (((MultiCoverAttributeReinsuranceContract) contract).parmBasedOn.equals(ReinsuranceContractBase.CEDED)) {
            doWire WireCategory, contract, 'inClaims', claimsMerger, 'outClaimsCeded'
        }
    }

    protected void wireContractInUnderwritingInfo(ReinsuranceContract contract, MarketUnderwritingInfoMerger underwritingInfoMerger) {
        if (((MultiCoverAttributeReinsuranceContract) contract).parmBasedOn.equals(ReinsuranceContractBase.NET)) {
            doWire WireCategory, contract, 'inUnderwritingInfo', underwritingInfoMerger, 'outUnderwritingInfoNet'
        }
        else if (((MultiCoverAttributeReinsuranceContract) contract).parmBasedOn.equals(ReinsuranceContractBase.CEDED)) {
            doWire WireCategory, contract, 'inUnderwritingInfo', underwritingInfoMerger, 'outUnderwritingInfoCededInGrossPackets'
        }
    }

    protected void wireContractInUnderwritingInfo(ReinsuranceContract contract, MarketUnderwritingInfoMerger uwInfoMerger, MarketUnderwritingInfoMerger gnpiUwInfoMerger) {
        if (((MultiCoverAttributeReinsuranceContract) contract).parmBasedOn.equals(ReinsuranceContractBase.NET)) {
            if (contract.parmContractStrategy instanceof XLContractStrategy && ((XLContractStrategy) contract.parmContractStrategy).premiumBase.equals(PremiumBase.GNPI)) {
                doWire WireCategory, contract, 'inUnderwritingInfo', gnpiUwInfoMerger, 'outUnderwritingInfoNet'
            }
            else if (contract.parmContractStrategy instanceof AggregateXLContractStrategy && ((AggregateXLContractStrategy) contract.parmContractStrategy).premiumBase.equals(PremiumBase.GNPI)) {
                doWire WireCategory, contract, 'inUnderwritingInfo', gnpiUwInfoMerger, 'outUnderwritingInfoNet'
            }
            else if (contract.parmContractStrategy instanceof GoldorakContractStrategy && ((GoldorakContractStrategy) contract.parmContractStrategy).premiumBase.equals(PremiumBase.GNPI)) {
                doWire WireCategory, contract, 'inUnderwritingInfo', gnpiUwInfoMerger, 'outUnderwritingInfoNet'
            }
            else if (contract.parmContractStrategy instanceof StopLossContractStrategy && ((StopLossContractStrategy) contract.parmContractStrategy).stopLossContractBase.equals(StopLossContractBase.GNPI)) {
                doWire WireCategory, contract, 'inUnderwritingInfo', gnpiUwInfoMerger, 'outUnderwritingInfoNet'
            }
            else if (contract.parmContractStrategy instanceof AdverseDevelopmentCoverContractStrategy && ((AdverseDevelopmentCoverContractStrategy) contract.parmContractStrategy).stopLossContractBase.equals(StopLossContractBase.GNPI)) {
                doWire WireCategory, contract, 'inUnderwritingInfo', gnpiUwInfoMerger, 'outUnderwritingInfoNet'
            }
            else {doWire WireCategory, contract, 'inUnderwritingInfo', uwInfoMerger, 'outUnderwritingInfoNet'}
        }
        else if (((MultiCoverAttributeReinsuranceContract) contract).parmBasedOn.equals(ReinsuranceContractBase.CEDED)) {
            doWire WireCategory, contract, 'inUnderwritingInfo', uwInfoMerger, 'outUnderwritingInfoCededInGrossPackets'
        }
    }
}