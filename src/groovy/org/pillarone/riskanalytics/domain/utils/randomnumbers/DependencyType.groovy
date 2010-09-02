package org.pillarone.riskanalytics.domain.utils.randomnumbers

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.util.MathUtils
import umontreal.iro.lecuyer.probdist.NormalDist
import umontreal.iro.lecuyer.randvar.NormalGen
import umontreal.iro.lecuyer.randvarmulti.MultinormalCholeskyGen

/**
 * @author ali.majidi (at) munichre (dot) com, stefan.kunz (at) intuitive-collaboration (dot) com
 */
class DependencyType extends AbstractParameterObjectClassifier {


    public static final DependencyType NORMAL = new DependencyType("Normal", "NORMAL", ["means": 0d, "sigmaMatrix": 0d])
    public static final DependencyType T = new DependencyType("t", "T", ["means": 0d, "sigmaMatrix": 0d, "degreesOfFreedom": 0])
    public static final all = [NORMAL, T]

    protected static Map types = [:]
    static {
        DependencyType.all.each {
            DependencyType.types[it.toString()] = it
        }
    }

    private DependencyType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }

    public static DependencyType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return DependencyType.getStrategy(this, parameters)
    }

    static IMultiRandomGenerator getStrategy(DependencyType type, Map parameters) {
        IMultiRandomGenerator generator
        switch (type) {
            case DependencyType.NORMAL:
                generator = new MultiRandomNumberGenerator(
                    generator: new MultinormalCholeskyGen(
                        new NormalGen(MathUtils.getRandomStreamBase(), new NormalDist(0d, 1d)),
                        (double[]) parameters["meanVector"],
                        (double[][]) parameters["sigmaMatrix"]))
                break
        }
        generator
    }
}