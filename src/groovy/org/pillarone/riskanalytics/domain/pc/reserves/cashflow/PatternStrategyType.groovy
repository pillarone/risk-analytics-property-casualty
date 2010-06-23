package org.pillarone.riskanalytics.domain.pc.reserves.cashflow

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.constants.SimulationPeriod

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */

public class PatternStrategyType extends AbstractParameterObjectClassifier {

    public static final PatternStrategyType NONE = new PatternStrategyType('none', 'NONE', [:])
    public static final PatternStrategyType INCREMENTAL = new PatternStrategyType("incremental", "INCREMENTAL", [
            incrementalPattern :  new TableMultiDimensionalParameter([1d], ['Increments']),
            calibrationPeriod : SimulationPeriod.ANNUALLY
    ])
    public static final PatternStrategyType CUMULATIVE = new PatternStrategyType("cumulative", "CUMULATIVE", [
            cumulativePattern :  new TableMultiDimensionalParameter([1d], ['Cumulative']),
            calibrationPeriod : SimulationPeriod.ANNUALLY
    ])

    public static final all = [NONE, INCREMENTAL, CUMULATIVE]

    protected static Map types = [:]
    static {
        PatternStrategyType.all.each {
            PatternStrategyType.types[it.toString()] = it
        }
    }

    private PatternStrategyType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }


    public static PatternStrategyType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }
    
    static IPatternStrategy getStrategy(PatternStrategyType type, Map parameters) {
        IPatternStrategy pattern;
        switch (type) {
            case PatternStrategyType.NONE:
                pattern = new NoPatternStrategy()
                break;
            case PatternStrategyType.INCREMENTAL:
                pattern = new IncrementalPatternStrategy(
                        incrementalPattern : (TableMultiDimensionalParameter) parameters['incrementalPattern'],
                        calibrationPeriod : (SimulationPeriod) parameters['calibrationPeriod']
                )
                break;
            case PatternStrategyType.CUMULATIVE:
                pattern = new CumulativePatternStrategy(
                        cumulativePattern : (TableMultiDimensionalParameter) parameters['cumulativePattern'],
                        calibrationPeriod : (SimulationPeriod) parameters['calibrationPeriod'])
                break;
        }
        return pattern;
    }
}