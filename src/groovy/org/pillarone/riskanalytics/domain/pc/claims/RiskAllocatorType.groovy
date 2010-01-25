package org.pillarone.riskanalytics.domain.pc.claims

import org.pillarone.riskanalytics.domain.pc.constants.RiskBandAllocationBaseLimited
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
class RiskAllocatorType extends AbstractParameterObjectClassifier {

    public static final RiskAllocatorType RISKTOBAND = new RiskAllocatorType("risk to band", "RISKTOBAND", ["allocationBase": RiskBandAllocationBaseLimited.PREMIUM])
    public static final RiskAllocatorType SUMINSUREDGENERATOR = new RiskAllocatorType("sum insured generator", "SUMINSUREDGENERATOR", [
        distribution: RandomDistributionFactory.getDistribution(DistributionType.NORMAL, ["mean": 0d, "stDev": 1d]),
        modification: DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:]),
        bandMean: 1d / 3d])
    public static final RiskAllocatorType NONE = new RiskAllocatorType("none", "NONE", [:])

    public static final all = [RISKTOBAND, SUMINSUREDGENERATOR, NONE]

    protected static Map types = [:]
    static {
        RiskAllocatorType.all.each {
            RiskAllocatorType.types[it.toString()] = it
        }
    }

    private RiskAllocatorType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }

    public static RiskAllocatorType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return RiskAllocatorStrategyFactory.getAllocatorStrategy(this, parameters)
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
        return "org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorStrategyFactory.getAllocatorStrategy(${this.class.name}.${typeName.toUpperCase()}, ${parameterString})"
    }
}