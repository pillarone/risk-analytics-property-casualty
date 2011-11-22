package org.pillarone.riskanalytics.domain.pc.generators.claims

import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.generators.copulas.DependenceStream
import org.pillarone.riskanalytics.domain.pc.generators.copulas.EventDependenceStream
import org.pillarone.riskanalytics.domain.pc.generators.fac.FacShareAndRetention
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.DistributionType

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */

public class DynamicDevelopedFACClaimsGenerators extends DynamicComposedComponent {

    /** needs to be connected only if a none absolute base is selected    */
    PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo)
    /** needs to be connected only if the claims generator was selected as target in a copula    */
    PacketList<DependenceStream> inProbabilities = new PacketList<DependenceStream>(DependenceStream)
    /** needs to be connected only ...    */
    PacketList<EventDependenceStream> inEventSeverities = new PacketList<EventDependenceStream>(EventDependenceStream)

    PacketList<FacShareAndRetention> inDistributionsByUwInfo = new PacketList<FacShareAndRetention>(FacShareAndRetention)

    PacketList<Claim> outClaims = new PacketList(Claim.class);
    // todo(sku): remove the following and related lines as soon as PMO-648 is resolved
    PacketList<ClaimDevelopmentLeanPacket> outClaimsLeanDevelopment = new PacketList<ClaimDevelopmentLeanPacket>(ClaimDevelopmentLeanPacket)

    public DevelopedTypableFACClaimsGenerator createDefaultSubComponent() {
        DevelopedTypableFACClaimsGenerator newComponent = new DevelopedTypableFACClaimsGenerator(
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
        replicateInChannels this, 'inDistributionsByUwInfo'
        replicateOutChannels this, 'outClaims'
        replicateOutChannels this, 'outClaimsLeanDevelopment'
    }
}