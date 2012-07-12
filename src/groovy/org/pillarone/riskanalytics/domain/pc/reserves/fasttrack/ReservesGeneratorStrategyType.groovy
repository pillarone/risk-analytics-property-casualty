package org.pillarone.riskanalytics.domain.pc.reserves.fasttrack

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker
import org.pillarone.riskanalytics.core.simulation.InvalidParameterException

/**
 * @author shartmann (at) munichre (dot) com
 */
public class ReservesGeneratorStrategyType extends AbstractParameterObjectClassifier {

    public static final ReservesGeneratorStrategyType ABSOLUTE = new ReservesGeneratorStrategyType("absolute", "ABSOLUTE", [
            basedOnClaimsGenerators : new ComboBoxTableMultiDimensionalParameter([], ["Claims Generators"], IPerilMarker)
    ])
    public static final ReservesGeneratorStrategyType INITIAL_RESERVES= new ReservesGeneratorStrategyType("initial reserves", "INITIAL_RESERVES", [
            basedOnClaimsGenerators : new ComboBoxTableMultiDimensionalParameter([], ["Claims Generators"], IPerilMarker)
    ])
    public static final ReservesGeneratorStrategyType PRIOR_PERIOD = new ReservesGeneratorStrategyType("prior period", "PRIOR_PERIOD", [
            basedOnClaimsGenerators : new ComboBoxTableMultiDimensionalParameter([], ["Claims Generators"], IPerilMarker)
    ])

    public static final all = [ABSOLUTE, INITIAL_RESERVES, PRIOR_PERIOD]

    protected static Map types = [:]
    static {
        ReservesGeneratorStrategyType.all.each {
            ReservesGeneratorStrategyType.types[it.toString()] = it
        }
    }

    private ReservesGeneratorStrategyType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }


    public static ReservesGeneratorStrategyType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }
    
    static IReservesGeneratorStrategy getStrategy(ReservesGeneratorStrategyType type, Map parameters) {
        IReservesGeneratorStrategy reserveGenerator;
        switch (type) {
            case ReservesGeneratorStrategyType.ABSOLUTE:
                reserveGenerator = new AbsoluteReservesGeneratorStrategy(
                        basedOnClaimsGenerators : (ComboBoxTableMultiDimensionalParameter) parameters.get("basedOnClaimsGenerators"))
                break;
            case ReservesGeneratorStrategyType.INITIAL_RESERVES:
                reserveGenerator = new InitialReservesGeneratorStrategy(
                         basedOnClaimsGenerators : (ComboBoxTableMultiDimensionalParameter) parameters.get("basedOnClaimsGenerators"))
                break;
            case ReservesGeneratorStrategyType.PRIOR_PERIOD:
                reserveGenerator = new PriorPeriodReservesGeneratorStrategy(
                        basedOnClaimsGenerators : (ComboBoxTableMultiDimensionalParameter) parameters.get("basedOnClaimsGenerators"))
                break;
            default:
                throw new InvalidParameterException("ReservesGeneratorStrategyType $type not implemented")
        }
        return reserveGenerator;
    }
}