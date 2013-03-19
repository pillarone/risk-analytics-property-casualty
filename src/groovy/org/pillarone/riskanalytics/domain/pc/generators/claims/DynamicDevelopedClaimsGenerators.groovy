package org.pillarone.riskanalytics.domain.pc.generators.claims

import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.generators.copulas.DependenceStream
import org.pillarone.riskanalytics.domain.pc.generators.copulas.EventDependenceStream
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.core.components.ComponentCategory
import org.pillarone.riskanalytics.core.wiring.WiringValidation
import org.pillarone.riskanalytics.domain.pc.generators.fac.FacShareAndRetention

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@ComponentCategory(categories = ['CLAIM','GENERATOR','ATTRITIONAL','SINGLE','NATCAT'])
public class DynamicDevelopedClaimsGenerators extends DynamicComposedComponent {

    /** needs to be connected only if a none absolute base is selected    */
    PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo)
    /** needs to be connected only if the claims generator was selected as target in a copula    */
    @WiringValidation(connections= [0, 1], packets= [1, 1])
    PacketList<DependenceStream> inProbabilities = new PacketList<DependenceStream>(DependenceStream.class);
    /** needs to be connected only ...    */
    PacketList<EventDependenceStream> inEventSeverities = new PacketList<EventDependenceStream>(EventDependenceStream)

    PacketList<Claim> outClaims = new PacketList(Claim.class);
    // todo(sku): remove the following and related lines as soon as PMO-648 is resolved
    PacketList<ClaimDevelopmentLeanPacket> outClaimsLeanDevelopment = new PacketList<ClaimDevelopmentLeanPacket>(ClaimDevelopmentLeanPacket)

    public DevelopedTypableClaimsGenerator createDefaultSubComponent() {
        DevelopedTypableClaimsGenerator newComponent = new DevelopedTypableClaimsGenerator(
                parmClaimsModel: ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.ATTRITIONAL, [
                        claimsSizeDistribution: DistributionType.getStrategy(DistributionType.CONSTANT, ['constant': 0d]),
                        claimsSizeModification: DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        claimsSizeBase: Exposure.ABSOLUTE]),
                parmPeriodPaymentPortion: 1d)
        return newComponent
    }

    public void wire() {
        replicateInChannels this, 'inUnderwritingInfo'
        replicateInChannels this, 'inProbabilities'
        replicateInChannels this, 'inEventSeverities'
        replicateOutChannels this, 'outClaims'
        replicateOutChannels this, 'outClaimsLeanDevelopment'
    }
}