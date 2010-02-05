package org.pillarone.riskanalytics.domain.pc.assetLiabilityMismatch

import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.packets.SingleValuePacket
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.DistributionModifierFactory
import org.pillarone.riskanalytics.domain.utils.DistributionModifier

/**
 * @author shartmann (at) munichre (dot) com
 */

public class DynamicAssetLiabilityMismatchGenerator extends DynamicComposedComponent{

    PacketList<SingleValuePacket> outInitialVolume = new PacketList<SingleValuePacket>(SingleValuePacket);

    PacketList<Claim> outAlmResult = new PacketList<Claim>(Claim);

    protected void doCalculation() {
        for (AssetLiabilityMismatchGenerator generator: componentList) {
            generator.start()
        }
    }

    public Component createDefaultSubComponent() {
        return new AssetLiabilityMismatchGenerator(
                parmDistribution : RandomDistributionFactory.getDistribution(DistributionType.CONSTANT ,
                                    ["constant" : 0d]),
                parmModification : DistributionModifierFactory.getModifier(DistributionModifier.NONE, new HashMap()),
                parmAssetLiabilityMismatchModel : AssetLiabilityMismatchGeneratorStrategyType.getStrategy(
                                    AssetLiabilityMismatchGeneratorStrategyType.ABSOLUTE,
                                    null,
                ));
    }

    public void wire() {
        replicateOutChannels this, 'outAlmResult'
        replicateOutChannels this, 'outInitialVolume'
    }

}
