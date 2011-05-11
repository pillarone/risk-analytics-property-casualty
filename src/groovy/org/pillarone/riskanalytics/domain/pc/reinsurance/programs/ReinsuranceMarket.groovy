package org.pillarone.riskanalytics.domain.pc.reinsurance.programs

import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.domain.pc.claims.MarketClaimsMerger
import org.pillarone.riskanalytics.domain.pc.constants.IncludeType
import org.pillarone.riskanalytics.domain.pc.constants.ReinsuranceContractBase
import org.pillarone.riskanalytics.domain.pc.reinsurance.ReinsuranceResultWithCommissionPacket
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.MultiCompanyCoverAttributeReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.MultiCoverAttributeReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.underwriting.MarketUnderwritingInfoMerger
import org.pillarone.riskanalytics.domain.pc.creditrisk.ReinsurerDefault
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.CompanyCoverAttributeStrategyType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.XLContractStrategy
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.StopLossContractStrategy
import org.pillarone.riskanalytics.domain.pc.constants.StopLossContractBase
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.AggregateXLContractStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.GoldorakContractStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.AdverseDevelopmentCoverContractStrategy
import org.pillarone.riskanalytics.core.components.ComponentCategory

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@ComponentCategory(categories = ['REINSURANCE','MARKET'])
public class ReinsuranceMarket extends DynamicReinsuranceProgram {

    PacketList<ReinsurerDefault> inReinsurersDefault = new PacketList<ReinsurerDefault>(ReinsurerDefault)
    PacketList<ReinsuranceResultWithCommissionPacket> outContractFinancials = new PacketList<ReinsuranceResultWithCommissionPacket>(ReinsuranceResultWithCommissionPacket.class);

    public MultiCompanyCoverAttributeReinsuranceContract createDefaultSubComponent() {
        MultiCompanyCoverAttributeReinsuranceContract contract = new MultiCompanyCoverAttributeReinsuranceContract(
                parmInuringPriority: 0,
                parmContractStrategy: ReinsuranceContractType.getStrategy(ReinsuranceContractType.TRIVIAL, [:]),
                parmCover: CompanyCoverAttributeStrategyType.getStrategy(
                        CompanyCoverAttributeStrategyType.ALL, ['reserves': IncludeType.NOTINCLUDED]))
        return contract
    }

    void wire() {
        super.wire()
        replicateInChannels this, 'inReinsurersDefault'
        replicateOutChannels this, 'outContractFinancials'
    }

    protected void wireContractInClaims(ReinsuranceContract contract, MarketClaimsMerger claimsMerger) {
        if (((MultiCompanyCoverAttributeReinsuranceContract) contract).parmBasedOn.equals(ReinsuranceContractBase.NET)) {
            doWire WireCategory, contract, 'inClaims', claimsMerger, 'outClaimsNet'
        }
        else if (((MultiCompanyCoverAttributeReinsuranceContract) contract).parmBasedOn.equals(ReinsuranceContractBase.CEDED)) {
            doWire WireCategory, contract, 'inClaims', claimsMerger, 'outClaimsCeded'
        }
    }

    protected void wireContractInUnderwritingInfo(ReinsuranceContract contract, MarketUnderwritingInfoMerger underwritingInfoMerger) {
        if (((MultiCompanyCoverAttributeReinsuranceContract) contract).parmBasedOn.equals(ReinsuranceContractBase.NET)) {
            doWire WireCategory, contract, 'inUnderwritingInfo', underwritingInfoMerger, 'outUnderwritingInfoNet'
        }
        else if (((MultiCompanyCoverAttributeReinsuranceContract) contract).parmBasedOn.equals(ReinsuranceContractBase.CEDED)) {
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