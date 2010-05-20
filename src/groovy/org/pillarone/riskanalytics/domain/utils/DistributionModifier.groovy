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
    public static final DistributionModifier LEFTTRUNCATEDRIGHTCENSORED = new DistributionModifier(
            "left truncated, right censored", "LEFTTRUNCATEDRIGHTCENSORED", ["min": -1E100, "max": 1E100])
    public static final DistributionModifier CENSOREDSHIFT = new DistributionModifier(
            "censored shift", "CENSOREDSHIFT", ["min": 0d, "max": 1E100, "shift": 0d])
    public static final DistributionModifier SHIFT = new DistributionModifier(
            "shift", "SHIFT", ["shift": 0d])

    public static final all = [NONE, TRUNCATED, TRUNCATEDSHIFT, CENSORED, CENSOREDSHIFT, SHIFT, LEFTTRUNCATEDRIGHTCENSORED]

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

    public List<IParameterObjectClassifier> getClassifiers() {
        all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return DistributionModifier.getStrategy(this, parameters)
    }

    static DistributionModified getStrategy(DistributionModifier modifier, Map parameters) {
        DistributionModified distributionModified
        switch (modifier) {
            case DistributionModifier.NONE:
                distributionModified = new DistributionModified(type: DistributionModifier.NONE, parameters: [:])
                break
            case DistributionModifier.CENSORED:
                distributionModified = new DistributionModified(type: DistributionModifier.CENSORED, parameters: parameters)
                break
            case DistributionModifier.CENSOREDSHIFT:
                distributionModified = new DistributionModified(type: DistributionModifier.CENSOREDSHIFT, parameters: parameters)
                break
            case DistributionModifier.TRUNCATED:
                distributionModified = new DistributionModified(type: DistributionModifier.TRUNCATED, parameters: parameters)
                break
            case DistributionModifier.LEFTTRUNCATEDRIGHTCENSORED:
                distributionModified = new DistributionModified(type: DistributionModifier.LEFTTRUNCATEDRIGHTCENSORED, parameters: parameters)
                break
            case DistributionModifier.TRUNCATEDSHIFT:
                distributionModified = new DistributionModified(type: DistributionModifier.TRUNCATEDSHIFT, parameters: parameters)
                break
            case DistributionModifier.SHIFT:
                distributionModified = new DistributionModified(type: DistributionModifier.SHIFT, parameters: parameters)
                break
        }
        return distributionModified
    }
}