package org.pillarone.riskanalytics.domain.pc.assetLiabilityMismatch

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.packets.SingleValuePacket
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.DistributionType

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
                parmDistribution : DistributionType.getStrategy(DistributionType.CONSTANT ,
                                    ["constant" : 0d]),
                parmModification : DistributionModifier.getStrategy(DistributionModifier.NONE, new HashMap()),
                parmAssetLiabilityMismatchModel : AssetLiabilityMismatchGeneratorStrategyType.getStrategy(
                                    AssetLiabilityMismatchGeneratorStrategyType.RESULTRELATIVETOINITIALVOLUME,
                                    [:],
                ));
    }

    public void wire() {
        replicateOutChannels this, 'outAlmResult'
        replicateOutChannels this, 'outInitialVolume'
    }

}
