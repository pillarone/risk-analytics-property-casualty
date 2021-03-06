package models.profiling

import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.generators.claims.SingleClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.generators.frequency.FrequencyGenerator
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.global.GlobalParameters

class FrequencyClaimsGeneratorDecomposedModel extends StochasticModel {

    PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList(UnderwritingInfo)

    GlobalParameters globalParameters
    FrequencyGenerator frequencyGenerator
    SingleClaimsGenerator claimsGenerator

    PacketList<Claim> outClaims = new PacketList(Claim)

    public void initComponents() {
        globalParameters = new GlobalParameters()
        frequencyGenerator = new FrequencyGenerator()
        claimsGenerator = new SingleClaimsGenerator()
        allComponents << frequencyGenerator
        allComponents << claimsGenerator
        addStartComponent frequencyGenerator
    }

    public void wireComponents() {
        claimsGenerator.inClaimCount = frequencyGenerator.outFrequency
    }

}