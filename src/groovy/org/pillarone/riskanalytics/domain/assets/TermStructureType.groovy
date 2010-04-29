package org.pillarone.riskanalytics.domain.assets

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier

/**
 * @author cyril (dot) neyme (at) kpmg (dot) fr
 */

public class TermStructureType extends AbstractParameterObjectClassifier {


    public static final TermStructureType CIR = new TermStructureType("Cox Ingersoll Ross", "CIR", ["meanReversionParameter": 0d,
        "riskAversionParameter": 0d, "longRunMean": 0d, "volatility": 0d, "initialInterestRate": 0d]);

    public static final TermStructureType CONSTANT = new TermStructureType("constant", "CONSTANT", ["rate": 0d]);

    //todo(cne) put all other models


    public static final all = [CIR, CONSTANT] // HULL_WHITE, BGM, NELSON_SPIEGEL]

    protected static Map types = [:]
    static {
        TermStructureType.all.each {
            TermStructureType.types[it.toString()] = it
        }
    }

    private TermStructureType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }

    public static TermStructureType valueOf(String type) {
        types[type]
    }


    public List<IParameterObjectClassifier> getClassifiers() {
        return all;
    }

    public IParameterObject getParameterObject(Map parameters) {
        return TermStructureType.getStrategy(this, parameters)
    }

    static IModellingStrategy getStrategy(TermStructureType type, Map parameters) {
        IModellingStrategy curve
        switch (type) {
            case TermStructureType.CIR:
                curve = new YieldCurveCIRStrategy(
                            (double) parameters["meanReversionParameter"],
                            (double) parameters["riskAversionParameter"],
                            (double) parameters["longRunMean"],
                            (double) parameters["volatility"],
                            (double) parameters["initialInterestRate"])
                break;
            case TermStructureType.CONSTANT:
                curve = new ConstantYieldCurveStrategy((double) parameters["rate"])
                break
            //...
        }
        return curve;
    }

}