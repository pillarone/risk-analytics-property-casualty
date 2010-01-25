package org.pillarone.riskanalytics.domain.pc.reinsurance.programs

import org.apache.log4j.Logger
import org.pillarone.riskanalytics.core.components.ComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.wiring.PortReplicatorCategory
import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.domain.pc.aggregators.ClaimsMerger
import org.pillarone.riskanalytics.domain.pc.aggregators.UnderwritingInfoMerger
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractStrategyFactory
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo

/**
 *  This reinsurance program contains three fix serial wired contracts. The type
 *  of the contracts is defined with the parameterization. Each contract works on
 *  the net claims of the preceeding contracts.
 *  In order to provide the merged gross, ceded and net claims after all contracts
 *  a claims aggregator is used. Merging is done by claim.id.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ReinsuranceProgram3SerialContracts extends ComposedComponent {

    PacketList<Claim> inClaims = new PacketList(Claim)
    PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList(UnderwritingInfo)

    PacketList<Claim> outClaimsCeded = new PacketList(Claim)
    PacketList<Claim> outClaimsGross = new PacketList(Claim)
    PacketList<Claim> outClaimsNet = new PacketList(Claim)
    PacketList<UnderwritingInfo> outUnderwritingInfoCeded = new PacketList(UnderwritingInfo)
    PacketList<UnderwritingInfo> outUnderwritingInfoGross = new PacketList(UnderwritingInfo)
    PacketList<UnderwritingInfo> outUnderwritingInfoNet = new PacketList(UnderwritingInfo)


    ReinsuranceContract subContract1 = new ReinsuranceContract(parmContractStrategy: new ReinsuranceContractStrategyFactory().getContractStrategy(ReinsuranceContractType.TRIVIAL, [:]))
    ReinsuranceContract subContract2 = new ReinsuranceContract(parmContractStrategy: new ReinsuranceContractStrategyFactory().getContractStrategy(ReinsuranceContractType.TRIVIAL, [:]))
    ReinsuranceContract subContract3 = new ReinsuranceContract(parmContractStrategy: new ReinsuranceContractStrategyFactory().getContractStrategy(ReinsuranceContractType.TRIVIAL, [:]))
    ReinsuranceContract subContract4 = new ReinsuranceContract(parmContractStrategy: new ReinsuranceContractStrategyFactory().getContractStrategy(ReinsuranceContractType.TRIVIAL, [:]))
    ReinsuranceContract subContract5 = new ReinsuranceContract(parmContractStrategy: new ReinsuranceContractStrategyFactory().getContractStrategy(ReinsuranceContractType.TRIVIAL, [:]))

    ClaimsMerger subClaimsAggregator = new ClaimsMerger()
    UnderwritingInfoMerger subUnderwritingInfoMerger = new UnderwritingInfoMerger()

    public void wire() {
        WiringUtils.use(WireCategory) {
            subContract2.inClaims = subContract1.outUncoveredClaims
            subContract2.inUnderwritingInfo = subContract1.outNetAfterCoverUnderwritingInfo
            subContract3.inClaims = subContract2.outUncoveredClaims
            subContract3.inUnderwritingInfo = subContract2.outNetAfterCoverUnderwritingInfo
            subContract4.inClaims = subContract3.outUncoveredClaims
            subContract4.inUnderwritingInfo = subContract3.outNetAfterCoverUnderwritingInfo
            subContract5.inClaims = subContract4.outUncoveredClaims
            subContract5.inUnderwritingInfo = subContract4.outNetAfterCoverUnderwritingInfo
            subClaimsAggregator.inClaimsCeded = subContract1.outCoveredClaims
            subClaimsAggregator.inClaimsCeded = subContract2.outCoveredClaims
            subClaimsAggregator.inClaimsCeded = subContract3.outCoveredClaims
            subClaimsAggregator.inClaimsCeded = subContract4.outCoveredClaims
            subClaimsAggregator.inClaimsCeded = subContract5.outCoveredClaims

            subUnderwritingInfoMerger.inUnderwritingInfoCeded = subContract1.outCoverUnderwritingInfo
            subUnderwritingInfoMerger.inUnderwritingInfoCeded = subContract2.outCoverUnderwritingInfo
            subUnderwritingInfoMerger.inUnderwritingInfoCeded = subContract3.outCoverUnderwritingInfo
            subUnderwritingInfoMerger.inUnderwritingInfoCeded = subContract4.outCoverUnderwritingInfo
            subUnderwritingInfoMerger.inUnderwritingInfoCeded = subContract5.outCoverUnderwritingInfo
        }
        WiringUtils.use(PortReplicatorCategory) {
            Logger.getLogger(PortReplicatorCategory).info "start wiring ${this.getName()}"
            subContract1.inClaims = this.inClaims
            subClaimsAggregator.inClaimsGross = this.inClaims
            subContract1.inUnderwritingInfo = this.inUnderwritingInfo
            this.outClaimsCeded = subClaimsAggregator.outClaimsCeded
            this.outClaimsGross = subClaimsAggregator.outClaimsGross
            this.outClaimsNet = subClaimsAggregator.outClaimsNet

            subUnderwritingInfoMerger.inUnderwritingInfoGross = this.inUnderwritingInfo
            this.outUnderwritingInfoCeded = subUnderwritingInfoMerger.outUnderwritingInfoCeded
            this.outUnderwritingInfoGross = subUnderwritingInfoMerger.outUnderwritingInfoGross
            this.outUnderwritingInfoNet = subUnderwritingInfoMerger.outUnderwritingInfoNet
        }
    }
}