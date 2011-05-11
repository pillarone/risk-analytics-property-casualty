package org.pillarone.riskanalytics.domain.pc.reinsurance.programs

import org.pillarone.riskanalytics.core.components.ComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.wiring.PortReplicatorCategory
import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.claims.allocation.ClaimsAllocation
import org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskAllocator
import org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskAllocatorType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfo
import org.pillarone.riskanalytics.core.components.ComponentCategory

/**
 * @author martin.melchior (at) fhnw (dot) ch
 */
@ComponentCategory(categories = ['REINSURANCE'])
class SurplusProgram extends ComposedComponent {

    PacketList<Claim> inClaims = new PacketList(Claim)
    PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList(UnderwritingInfo)

    PacketList<Claim> outClaims = new PacketList(Claim)
    PacketList<Claim> outClaimsNet = new PacketList(Claim)

    PacketList<CededUnderwritingInfo> outUnderwritingInfo = new PacketList(CededUnderwritingInfo)
    PacketList<UnderwritingInfo> outUnderwritingInfoNet = new PacketList(UnderwritingInfo)

    // allocator and its configuration
    ClaimsAllocation subAttritionalAllocation = new ClaimsAllocation()
    RiskAllocator subAllocator = new RiskAllocator(
        parmRiskAllocatorStrategy: RiskAllocatorType.getStrategy(
            RiskAllocatorType.RISKTOBAND, [:]))
    ClaimsAllocation subLargeAllocation = new ClaimsAllocation()

    // reinsurance contract
    ReinsuranceContract subSurplus = new ReinsuranceContract(
        parmContractStrategy: ReinsuranceContractType.getStrategy(
            ReinsuranceContractType.SURPLUS, ["retention": 0, "lines": 0, "commission": 0.0,
                'defaultCededLossShare': 0d, 'coveredByReinsurer': 1d]))



    public void wire() {
        WiringUtils.use(WireCategory) {
            subAllocator.inTargetDistribution = subAttritionalAllocation.outAllocationTable
            subSurplus.inClaims = subAllocator.outClaims
        }

        WiringUtils.use(PortReplicatorCategory) {
            subAllocator.inUnderwritingInfo = this.inUnderwritingInfo
            subSurplus.inUnderwritingInfo = this.inUnderwritingInfo

            subAllocator.inClaims = this.inClaims

            this.outClaims = subSurplus.outCoveredClaims
            this.outClaimsNet = subSurplus.outUncoveredClaims
            this.outUnderwritingInfo = subSurplus.outCoverUnderwritingInfo
            this.outUnderwritingInfoNet = subSurplus.outNetAfterCoverUnderwritingInfo
        }
    }

    public void doCalculation() {
        super.doCalculation()
        subAttritionalAllocation.start()
        subLargeAllocation.start()
    }
}
