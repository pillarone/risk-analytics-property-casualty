package org.pillarone.riskanalytics.domain.pc.generators.copulas

import org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.ComboBoxMatrixMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier

/**
 *  Difference to CopulaType is that a PerilMarker instead of LobMarker is used.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class PerilCopulaType extends CopulaType {


    public static final PerilCopulaType NORMAL = new PerilCopulaType("normal", "NORMAL", ["dependencyMatrix": new ComboBoxMatrixMultiDimensionalParameter([[1d, 0d], [0d, 1d]], ["A", "B"], PerilMarker)])
    public static final PerilCopulaType FRECHETUPPERBOUND = new PerilCopulaType("frechet upper bound", "FRECHETUPPERBOUND", ["targets": new ComboBoxTableMultiDimensionalParameter(["A"], ['Targets'], PerilMarker)])
    public static final PerilCopulaType INDEPENDENT = new PerilCopulaType("independent", "INDEPENDENT", ["targets": new ComboBoxTableMultiDimensionalParameter(["A"], ['Targets'], PerilMarker)])
    public static final PerilCopulaType T = new PerilCopulaType("t", "T", ["dependencyMatrix": new ComboBoxMatrixMultiDimensionalParameter([[1d, 0d], [0d, 1d]], ["A", "B"], PerilMarker), "degreesOfFreedom": 10])
    public static final PerilCopulaType GUMBEL = new PerilCopulaType("gumbel", "GUMBEL", ["lambda": 10, "dimension": 2, "targets": new ComboBoxTableMultiDimensionalParameter(["A"], ['Targets'], PerilMarker)])

    public static final all = [NORMAL, FRECHETUPPERBOUND, INDEPENDENT, T, GUMBEL]

    protected static Map types = [:]
    static {
        PerilCopulaType.all.each {
            PerilCopulaType.types[it.toString()] = it
        }
    }

    private PerilCopulaType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return CopulaStrategyFactory.getCopulaStrategy(this, parameters)
    }

    public static PerilCopulaType valueOf(String type) {
        types[type]
    }

    public String getConstructionString(Map parameters) {
        StringBuffer parameterString = new StringBuffer('[')
        parameters.each {k, v ->
            if (v.class.isEnum()) {
                parameterString << "\"$k\":${v.class.name}.$v,"
            }
            else {
                parameterString << "\"$k\":$v,"
            }
        }
        if (parameterString.size() == 1) {
            parameterString << ':'
        }
        parameterString << ']'
        "org.pillarone.riskanalytics.domain.pc.generators.copulas.CopulaStrategyFactory.getCopulaStrategy(${this.class.name}.${typeName.toUpperCase()}, ${parameterString})"
    }
}