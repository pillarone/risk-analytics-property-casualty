package org.pillarone.riskanalytics.domain.pc.assetLiabilityMismatch

import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier

/**
 * @author shartmann (at) munichre (dot) com
 */

public class AssetLiabilityMismatchGeneratorStrategyType extends AbstractParameterObjectClassifier{

    public static final AssetLiabilityMismatchGeneratorStrategyType ABSOLUTE =
        new AssetLiabilityMismatchGeneratorStrategyType("absolute", "ABSOLUTE", [:])

    public static final all = [ABSOLUTE]

    protected static Map types = [:]
    static {
        AssetLiabilityMismatchGeneratorStrategyType.all.each {
           AssetLiabilityMismatchGeneratorStrategyType.types[it.toString()] = it
        }
    }

    private AssetLiabilityMismatchGeneratorStrategyType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }



  public static AssetLiabilityMismatchGeneratorStrategyType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }

    static IAssetLiabilityMismatchGeneratorStrategy getStrategy(AssetLiabilityMismatchGeneratorStrategyType type, Map parameters) {
        IAssetLiabilityMismatchGeneratorStrategy almGenerator;
        switch (type) {
            case AssetLiabilityMismatchGeneratorStrategyType.ABSOLUTE:
//              almGenerator = new AbsoluteAssetLiabilityMismatchGeneratorStrategy(type: AssetLiabilityMismatchGeneratorStrategyType.ABSOLUTE, parameters: [:])
              almGenerator = new AbsoluteAssetLiabilityMismatchGeneratorStrategy()
              break;
        }
        return almGenerator;
    }
}
