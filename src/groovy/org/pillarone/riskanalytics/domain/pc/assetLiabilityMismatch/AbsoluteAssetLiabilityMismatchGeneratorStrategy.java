package org.pillarone.riskanalytics.domain.pc.assetLiabilityMismatch;

import org.pillarone.riskanalytics.core.parameterization.IParameterObject;

import java.util.Collections;
import java.util.Map;

/**
 * @author shartmann (at) munichre (dot) com
 */
public class AbsoluteAssetLiabilityMismatchGeneratorStrategy  implements IParameterObject, IAssetLiabilityMismatchGeneratorStrategy {

    public AbsoluteAssetLiabilityMismatchGeneratorStrategy() {
    }

    public Object getType() {
        return AssetLiabilityMismatchGeneratorStrategyType.ABSOLUTE;
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }
}
