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

    public String getConstructionString(Map parameters) {
        StringBuffer parameterString = new StringBuffer('[')
        parameters.each {k, v ->
            if (v.class.isEnum()) {
                parameterString << "\"$k\":${v.class.name}.$v,"
            }
            else if (v instanceof IParameterObject) {
                parameterString << "\"$k\":${v.type.getConstructionString(v.parameters)},"
            }
            else {
                parameterString << "\"$k\":$v,"
            }
        }
        if (parameterString.size() == 1) {
            parameterString << ':'
        }
        parameterString << ']'
        return "org.pillarone.modelling.domain.pc.assetLiabilityMismatch.AssetLiabilityMismatchGeneratorStrategyType.getStrategy(${this.class.name}.${typeName.toUpperCase()}, ${parameterString})"
    }
}
