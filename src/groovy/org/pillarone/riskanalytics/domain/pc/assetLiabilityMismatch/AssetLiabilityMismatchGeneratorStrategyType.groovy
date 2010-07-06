package org.pillarone.riskanalytics.domain.pc.assetLiabilityMismatch

import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier

/**
 * @author shartmann (at) munichre (dot) com
 */

public class AssetLiabilityMismatchGeneratorStrategyType extends AbstractParameterObjectClassifier{

    public static final AssetLiabilityMismatchGeneratorStrategyType RESULTABSOLUTE =
        new AssetLiabilityMismatchGeneratorStrategyType("result absolute", "RESULTABSOLUTE", [:])
    public static final AssetLiabilityMismatchGeneratorStrategyType RESULTRELATIVETOINITIALVOLUME = 
        new AssetLiabilityMismatchGeneratorStrategyType("result relative to initial volume", "RESULTRELATIVETOINITIALVOLUME", [:])
    public static final AssetLiabilityMismatchGeneratorStrategyType VOLUMERELATIVETOINITIALVOLUME = 
        new AssetLiabilityMismatchGeneratorStrategyType("volume relative to initial volume", "RELATIVETOINITIALVOLUME", [:])
    public static final AssetLiabilityMismatchGeneratorStrategyType VOLUMEABSOLUTE = 
        new AssetLiabilityMismatchGeneratorStrategyType("volume absolute", "VOLUMEABSOLUTE", [:])

    public static final all = [RESULTABSOLUTE, RESULTRELATIVETOINITIALVOLUME, VOLUMEABSOLUTE, VOLUMERELATIVETOINITIALVOLUME]

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
            case AssetLiabilityMismatchGeneratorStrategyType.RESULTABSOLUTE:
                almGenerator = new ResultAbsoluteAssetLiabilityMismatchGeneratorStrategy()
                break;
            case AssetLiabilityMismatchGeneratorStrategyType.RESULTRELATIVETOINITIALVOLUME:
                almGenerator = new ResultRelativeToInitialVolumeAssetLiabilityMismatchGeneratorStrategy()
                break;
            case AssetLiabilityMismatchGeneratorStrategyType.VOLUMEABSOLUTE:
                almGenerator = new VolumeAbsoluteAssetLiabilityMismatchGeneratorStrategy()
                break;
            case AssetLiabilityMismatchGeneratorStrategyType.VOLUMERELATIVETOINITIALVOLUME:
                almGenerator = new VolumeRelativeToInitialVolumeAssetLiabilityMismatchGeneratorStrategy()
                break;
        }
        return almGenerator;
    }
}
