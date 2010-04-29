package org.pillarone.riskanalytics.domain.pc.assetLiabilityMismatch;

import org.apache.commons.lang.ArrayUtils;
import org.pillarone.riskanalytics.core.components.PeriodStore;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.packets.SingleValuePacket;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.generators.GeneratorCachingComponent;
import org.pillarone.riskanalytics.domain.utils.*;

import java.util.Collections;

/**
 * @author shartmann (at) munichre (dot) com
 */
public class AssetLiabilityMismatchGenerator extends GeneratorCachingComponent implements IAssetLiabilityMismatchMarker {
    private PeriodScope periodScope;

    private PeriodStore periodStore;
    private static final String ASSET_LIABILITY_MISMATCH = "asset liability mismatch";

    private PacketList<SingleValuePacket> outInitialVolume = new PacketList<SingleValuePacket>(SingleValuePacket.class);
    //todo sha: claim should be replaced with ALM result: claim currently used as result (POSITIVE)
    private PacketList<Claim> outAlmResult = new PacketList<Claim>(Claim.class);

    private RandomDistribution parmDistribution = DistributionType.getStrategy(DistributionType.CONSTANT,
            ArrayUtils.toMap(new Object[][]{{"constant", 0d}}));
    private DistributionModified parmModification = DistributionModifier.getStrategy(DistributionModifier.NONE, Collections.emptyMap());
    private double parmInitialVolume = 0d;
    private IAssetLiabilityMismatchGeneratorStrategy parmAssetLiabilityMismatchModel =
            AssetLiabilityMismatchGeneratorStrategyType.getStrategy(
                AssetLiabilityMismatchGeneratorStrategyType.ABSOLUTE, Collections.emptyMap());

    protected void doCalculation() {
        Claim claim = new Claim();
        claim.setOrigin(this);
        setIncurred(claim);
        outAlmResult.add(claim);
    }


    private void setIncurred(Claim claim) {
        IRandomNumberGenerator generator = getCachedGenerator(getParmDistribution(), getParmModification());
        Double randomFactor = (Double) generator.nextValue();

        claim.setUltimate(randomFactor * parmInitialVolume);
        SingleValuePacket initialVolume = new SingleValuePacket();
        initialVolume.setValue(parmInitialVolume);
        outInitialVolume.add(initialVolume);
    }

    public double getParmInitialVolume() {
        return parmInitialVolume;
    }

    public void setParmInitialVolume(double parmInitialVolume) {
        this.parmInitialVolume = parmInitialVolume;
    }

    public PeriodScope getPeriodScope() {
        return periodScope;
    }

    public void setPeriodScope(PeriodScope periodScope) {
        this.periodScope = periodScope;
    }

    public PeriodStore getPeriodStore() {
        return periodStore;
    }

    public void setPeriodStore(PeriodStore periodStore) {
        this.periodStore = periodStore;
    }

    public IAssetLiabilityMismatchGeneratorStrategy getParmAssetLiabilityMismatchModel() {
        return parmAssetLiabilityMismatchModel;
    }

    public void setParmAssetLiabilityMismatchModel(IAssetLiabilityMismatchGeneratorStrategy parmAssetLiabilityMismatchModel) {
        this.parmAssetLiabilityMismatchModel = parmAssetLiabilityMismatchModel;
    }

    public RandomDistribution getParmDistribution() {
        return parmDistribution;
    }

    public void setParmDistribution(RandomDistribution parmDistribution) {
        this.parmDistribution = parmDistribution;
    }

    public DistributionModified getParmModification() {
        return parmModification;
    }

    public void setParmModification(DistributionModified parmModification) {
        this.parmModification = parmModification;
    }

    public PacketList<SingleValuePacket> getOutInitialVolume() {
        return outInitialVolume;
    }

    public void setOutInitialVolume(PacketList<SingleValuePacket> outInitialVolume) {
        this.outInitialVolume = outInitialVolume;
    }

    public PacketList<Claim> getOutAlmResult() {
        return outAlmResult;
    }

    public void setOutAlmResult(PacketList<Claim> outAlmResult) {
        this.outAlmResult = outAlmResult;
    }
}
