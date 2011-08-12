package org.pillarone.riskanalytics.domain.pc.assetLiabilityMismatch

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.PeriodStore
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.DistributionType

/**
 * @author shartmann (at) munichre (dot) com
 */

class AssetLiabilityMismatchGeneratorTests extends GroovyTestCase {

  void testUsageAbsoluteReserve() {
    AssetLiabilityMismatchGenerator assetLiabilityMismatchGenerator = new AssetLiabilityMismatchGenerator(
            parmDistribution: DistributionType.getStrategy(DistributionType.CONSTANT,
                    ["constant": 1.1d]),
            parmModification: DistributionModifier.getStrategy(DistributionModifier.NONE, new HashMap()),
            parmInitialVolume: 110d,
            parmAssetLiabilityMismatchModel: AssetLiabilityMismatchGeneratorStrategyType.getStrategy(
                    AssetLiabilityMismatchGeneratorStrategyType.RESULTRELATIVETOINITIALVOLUME, Collections.emptyMap())
    )

    assetLiabilityMismatchGenerator.periodScope = new PeriodScope();
    assetLiabilityMismatchGenerator.periodScope.currentPeriod = 0
    assetLiabilityMismatchGenerator.periodStore = new PeriodStore(assetLiabilityMismatchGenerator.periodScope)

    assetLiabilityMismatchGenerator.doCalculation()
    assertEquals 'initial volume', 110d, assetLiabilityMismatchGenerator.outInitialVolume.get(0).value
    assertEquals '# packets, period 0', 1, assetLiabilityMismatchGenerator.outAlmResult.size()
    assertEquals 'alm, period 0', 121d, assetLiabilityMismatchGenerator.outAlmResult[0].ultimate, 1e-8
    
    assetLiabilityMismatchGenerator.periodScope.currentPeriod++
    assetLiabilityMismatchGenerator.reset()
    assetLiabilityMismatchGenerator.doCalculation()
    assetLiabilityMismatchGenerator.doCalculation()
    assertEquals 'initial volume', 110d, assetLiabilityMismatchGenerator.outInitialVolume.get(0).value, 1e-8
    assertEquals '# packets, period 1', 2, assetLiabilityMismatchGenerator.outAlmResult.size()
    assertEquals 'alm, period 1', 242d, assetLiabilityMismatchGenerator.outAlmResult[0].ultimate +
            assetLiabilityMismatchGenerator.outAlmResult[1].ultimate, 1e-8
  }
}

class TestAlmGenerator extends Component implements IPerilMarker {

  protected void doCalculation() {
    //To change body of implemented methods use File | Settings | File Templates.
  }

}
