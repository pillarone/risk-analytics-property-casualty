package org.pillarone.riskanalytics.domain.pc.lob

import org.pillarone.riskanalytics.core.components.ComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.wiring.PortReplicatorCategory
import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.domain.pc.generators.claims.AttritionalSingleEQFloodStormClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.generators.copulas.DependenceStream
import org.pillarone.riskanalytics.domain.pc.reinsurance.programs.ReinsuranceProgram3SerialContracts
import org.pillarone.riskanalytics.domain.pc.severities.ProbabilityExtractor
import org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskBands
import org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskAllocator

/**
 *  This example line of business contains an underwriting, claims generator and a
 *  reinsurance program. The later with a fixed number of three serial contracts.
 *  Furthermore several aggregators are included for the collection and aggregation of packets.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class PropertyLobAttritionalDependencies extends ComposedComponent implements LobMarker {

    PacketList<DependenceStream> inProbabilities = new PacketList(DependenceStream)

    ProbabilityExtractor subAttritionalSeverityExtractor
    RiskBands subUnderwriting
    AttritionalSingleEQFloodStormClaimsGenerator subClaimsGenerator
    RiskAllocator subAllocator
    ReinsuranceProgram3SerialContracts subRiProgram


    PropertyLobAttritionalDependencies() {
        subAttritionalSeverityExtractor = new ProbabilityExtractor()
        subUnderwriting = new RiskBands()
        subClaimsGenerator = new AttritionalSingleEQFloodStormClaimsGenerator()
        subAllocator = new RiskAllocator()
        subRiProgram = new ReinsuranceProgram3SerialContracts()
    }

    public void doCalculation() {
        if (isReceiverWired(inProbabilities)) {
            super.doCalculation()
            subUnderwriting.start()
        }
        else {
            super.doCalculation()
        }
    }

    public void wire() {
        if (isReceiverWired(inProbabilities)) {
            WiringUtils.use(PortReplicatorCategory) {
                subAttritionalSeverityExtractor.inProbabilities = this.inProbabilities
            }
        }
        WiringUtils.use(WireCategory) {
            subClaimsGenerator.inProbabilities = subAttritionalSeverityExtractor.outProbabilities
            subClaimsGenerator.inUnderwritingInfo = subUnderwriting.outUnderwritingInfo
            subAllocator.inUnderwritingInfo = subUnderwriting.outUnderwritingInfo
            subAllocator.inTargetDistribution = subUnderwriting.outAttritionalTargetDistribution

            subRiProgram.inUnderwritingInfo = subAllocator.outUnderwritingInfo
            subAllocator.inClaims = subClaimsGenerator.outClaims
            subRiProgram.inClaims = subAllocator.outClaims
        }
    }
}