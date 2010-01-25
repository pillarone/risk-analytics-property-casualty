package org.pillarone.riskanalytics.domain.pc.generators.claims

import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase
import org.pillarone.riskanalytics.domain.pc.constants.FrequencySeverityClaimType
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.DistributionModifierFactory
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimsGeneratorType extends AbstractParameterObjectClassifier {

    public static final ClaimsGeneratorType NONE = new ClaimsGeneratorType("none", "NONE", [:])
    public static final ClaimsGeneratorType ATTRITIONAL = new ClaimsGeneratorType("attritional", "ATTRITIONAL", [
            claimsSizeBase: Exposure.ABSOLUTE,
            claimsSizeDistribution: RandomDistributionFactory.getDistribution(DistributionType.CONSTANT, ["constant": 0d]),
            claimsSizeModification: DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:])])
    public static final ClaimsGeneratorType EXTERNAL_SEVERITY = new ClaimsGeneratorType("external severity", "EXTERNAL_SEVERITY", [
            claimsSizeBase: Exposure.ABSOLUTE,
            claimsSizeDistribution: RandomDistributionFactory.getDistribution(DistributionType.CONSTANT, ["constant": 0d]),
            produceClaim: FrequencySeverityClaimType.AGGREGATED_EVENT])
    public static final ClaimsGeneratorType FREQUENCY_AVERAGE_ATTRITIONAL = new ClaimsGeneratorType("frequency average attritional", "FREQUENCY_AVERAGE_ATTRITIONAL", [
            frequencyBase: FrequencyBase.ABSOLUTE,
            frequencyDistribution: RandomDistributionFactory.getDistribution(DistributionType.CONSTANT, ["constant": 0d]),
            frequencyModification: DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:]),
            claimsSizeBase: Exposure.ABSOLUTE,
            claimsSizeDistribution: RandomDistributionFactory.getDistribution(DistributionType.CONSTANT, ["constant": 0d]),
            claimsSizeModification: DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:])])
    public static final ClaimsGeneratorType FREQUENCY_SEVERITY = new ClaimsGeneratorType("frequency severity", "FREQUENCY_SEVERITY", [
            frequencyBase: FrequencyBase.ABSOLUTE,
            frequencyDistribution: RandomDistributionFactory.getDistribution(DistributionType.CONSTANT, ["constant": 0d]),
            frequencyModification: DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:]),
            claimsSizeBase: Exposure.ABSOLUTE,
            claimsSizeDistribution: RandomDistributionFactory.getDistribution(DistributionType.CONSTANT, ["constant": 0d]),
            claimsSizeModification: DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:]),
            produceClaim: FrequencySeverityClaimType.SINGLE])


    public static final all = [NONE, ATTRITIONAL, FREQUENCY_AVERAGE_ATTRITIONAL, FREQUENCY_SEVERITY, EXTERNAL_SEVERITY]

    protected static Map types = [:]
    static {
        ClaimsGeneratorType.all.each {
            ClaimsGeneratorType.types[it.toString()] = it
        }
    }

    private ClaimsGeneratorType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }

    public static ClaimsGeneratorType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return ClaimsGeneratorStrategyFactory.getStrategy(this, parameters)
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
        return "org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorStrategyFactory.getStrategy(${this.class.name}.${typeName.toUpperCase()}, ${parameterString})"
    }
}