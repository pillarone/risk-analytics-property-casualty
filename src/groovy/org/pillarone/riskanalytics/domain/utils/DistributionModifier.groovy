package org.pillarone.riskanalytics.domain.utils

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObject

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class DistributionModifier extends AbstractParameterObjectClassifier {


    public static final DistributionModifier NONE = new DistributionModifier(
            "none", "NONE", [:])
    public static final DistributionModifier TRUNCATED = new DistributionModifier(
            "truncated", "TRUNCATED", ["min": 0d, "max": 1E100])
    public static final DistributionModifier TRUNCATEDSHIFT = new DistributionModifier(
            "truncated shift", "TRUNCATEDSHIFT", ["min": 0d, "max": 1E100, "shift": 0d])
    public static final DistributionModifier CENSORED = new DistributionModifier(
            "censored", "CENSORED", ["min": 0d, "max": 1E100])
    public static final DistributionModifier CENSOREDSHIFT = new DistributionModifier(
            "censored shift", "CENSOREDSHIFT", ["min": 0d, "max": 1E100, "shift": 0d])
    public static final DistributionModifier SHIFT = new DistributionModifier(
            "shift", "SHIFT", ["shift": 0d])

    public static final all = [NONE, TRUNCATED, TRUNCATEDSHIFT, CENSORED, CENSOREDSHIFT, SHIFT]

    protected static Map types = [:]

    static {
        DistributionModifier.all.each {
            DistributionModifier.types[it.toString()] = it
        }
    }

    private DistributionModifier(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }

    public static DistributionModifier valueOf(String type) {
        types[type]
    }

    public String getConstructionString(Map parameters) {
        TreeMap sortedParameters = new TreeMap()
        sortedParameters.putAll(parameters)
        "org.pillarone.riskanalytics.domain.utils.DistributionModifierFactory.getModifier(${this.class.name}.${typeName.toUpperCase()}, $sortedParameters)"
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return DistributionModifierFactory.getModifier(this, parameters)
    }

}