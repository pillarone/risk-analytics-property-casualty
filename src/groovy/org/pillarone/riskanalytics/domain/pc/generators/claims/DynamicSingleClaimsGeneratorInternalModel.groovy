package org.pillarone.riskanalytics.domain.pc.generators.claims

import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.wiring.PortReplicatorCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.generators.frequency.Frequency
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo

/**
 * @author michael-noe (at) web (dot) de, stefan.kunz (at) intuitive-collaboration (dot) com
 */
class DynamicSingleClaimsGeneratorInternalModel extends DynamicComposedComponent {

    PacketList<Frequency> inFrequency = new PacketList(Frequency)
    PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList(UnderwritingInfo)
    PacketList<Claim> outClaims = new PacketList(Claim)

    public SingleClaimsGeneratorWithFrequencyExtractor createDefaultSubComponent() {
        SingleClaimsGeneratorWithFrequencyExtractor generator = new SingleClaimsGeneratorWithFrequencyExtractor(componentList.size())
        return generator
    }

    public void wire() {
        for (SingleClaimsGeneratorWithFrequencyExtractor generator: componentList) {
            WiringUtils.use(PortReplicatorCategory) {
                generator.inFrequency = this.inFrequency
                generator.inUnderwritingInfo = this.inUnderwritingInfo
                this.outClaims = generator.outClaims
            }
            generator.internalWiring()
        }
    }

    public SingleClaimsGeneratorWithFrequencyExtractor getSingleClaimsGenerator(int index) {
        (SingleClaimsGeneratorWithFrequencyExtractor) componentList[index]
    }
}