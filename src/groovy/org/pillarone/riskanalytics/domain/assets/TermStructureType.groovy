package org.pillarone.riskanalytics.domain.assets

import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
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
        return ModellingStrategyFactory.getModellingStrategy(this, parameters)
    }

    public String getConstructionString(Map parameters) {   //todo (cne) change this function and define a naming convention for modellingChoices (this is a copy/paste of reinsurance contracts)
        StringBuffer parameterString = new StringBuffer('[')
        parameters.each {k, v ->
            if (v.class.isEnum()) {
                parameterString << "\"$k\":${v.class.name}.$v,"
            } else {
                parameterString << "\"$k\":$v,"
            }
        }
        if (parameterString.size() == 1) {
            parameterString << ':'
        }
        parameterString << ']'
        "org.pillarone.riskanalytics.domain.assets.ModellingStrategyFactory.getModellingStrategy(${this.class.name}.${typeName.toUpperCase()}, ${parameterString})"
    }

}