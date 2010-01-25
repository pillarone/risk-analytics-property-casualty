package org.pillarone.riskanalytics.domain.pc.generators.claims

import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.generators.copulas.DependenceStream
import org.pillarone.riskanalytics.domain.pc.generators.copulas.EventDependenceStream
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.DistributionModifierFactory
import org.pillarone.riskanalytics.domain.utils.DistributionModifier

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 *
 * Dynamische Komponente zum Anlegen von DynamicClaimsGenerators mittels UI.
 * Die Segmente werden über DynamicComposedComponent gespreichert.
 * Zulässige Subkomponente: TypeableClaimsGenerator, Generatorstrategie muss IClaimsGeneratorStrategy implementieren
 *
 */

public class DynamicClaimsGenerators extends DynamicComposedComponent {

    /** needs to be connected only if a none absolute base is selected    */
    PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList(UnderwritingInfo.class);
    /** needs to be connected only if the claims generator was selected as target in a copula    */
    PacketList<DependenceStream> inProbabilities = new PacketList(DependenceStream.class);
    /** needs to be connected only ...    */
    PacketList<EventDependenceStream> inEventSeverities = new PacketList(EventDependenceStream.class);

    PacketList<Claim> outClaims = new PacketList(Claim.class);

    public TypableClaimsGenerator createDefaultSubComponent() {
        TypableClaimsGenerator newComponent = new TypableClaimsGenerator(
                parmClaimsModel: ClaimsGeneratorStrategyFactory.getStrategy(ClaimsGeneratorType.ATTRITIONAL, [
                        claimsSizeDistribution: RandomDistributionFactory.getDistribution(DistributionType.CONSTANT, ['constant': 0d]),
                        claimsSizeModification: DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:]),
                        claimsSizeBase: Exposure.ABSOLUTE]))
        return newComponent
    }

    public void wire() {
        replicateInChannels this, 'inUnderwritingInfo'
        replicateInChannels this, 'inProbabilities'
        replicateInChannels this, 'inEventSeverities'
        replicateOutChannels this, 'outClaims'
    }
}