package org.pillarone.riskanalytics.domain.pc.claims

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.domain.pc.constants.RiskBandAllocationBaseLimited
import org.pillarone.riskanalytics.domain.utils.DistributionModified
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.RandomDistribution

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class RiskAllocatorType extends AbstractParameterObjectClassifier {

    public static final RiskAllocatorType RISKTOBAND = new RiskAllocatorType("risk to band", "RISKTOBAND", ["allocationBase": RiskBandAllocationBaseLimited.PREMIUM])
    public static final RiskAllocatorType SUMINSUREDGENERATOR = new RiskAllocatorType("sum insured generator", "SUMINSUREDGENERATOR", [
        distribution: DistributionType.getStrategy(DistributionType.NORMAL, ["mean": 0d, "stDev": 1d]),
        modification: DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
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
        return RiskAllocatorType.getStrategy(this, parameters)
    }

    static IRiskAllocatorStrategy getStrategy(RiskAllocatorType type, Map parameters) {
        IRiskAllocatorStrategy riskAllocator
        switch (type) {
            case RiskAllocatorType.NONE:
                riskAllocator = new TrivialRiskAllocatorStrategy()
                break
            case RiskAllocatorType.RISKTOBAND:
                riskAllocator = new RiskToBandAllocatorStrategy(allocationBase:
                    (RiskBandAllocationBaseLimited) parameters["allocationBase"])
                break
            case RiskAllocatorType.SUMINSUREDGENERATOR:
                riskAllocator = new SumInsuredGeneratorRiskAllocatorStrategy(
                    distribution: (RandomDistribution) parameters["distribution"],
                    modification: (DistributionModified) parameters["modification"],
                    bandMean: (double) parameters["bandMean"])
                break
        }
        return riskAllocator
    }
}