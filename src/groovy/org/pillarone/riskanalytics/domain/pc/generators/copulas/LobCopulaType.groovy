package org.pillarone.riskanalytics.domain.pc.generators.copulas

import org.pillarone.riskanalytics.core.parameterization.ComboBoxMatrixMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@Deprecated
class LobCopulaType extends CopulaType {

    protected static Map types = [:]

    public static final LobCopulaType NORMAL = new LobCopulaType("normal", "NORMAL", ["dependencyMatrix": new ComboBoxMatrixMultiDimensionalParameter([[1d, 0d], [0d, 1d]], ["A", "B"], ISegmentMarker)])
    public static final LobCopulaType FRECHETUPPERBOUND = new LobCopulaType("frechet upper bound", "FRECHETUPPERBOUND", ["targets": new ComboBoxTableMultiDimensionalParameter(["A"], ['Targets'], ISegmentMarker)])
    public static final LobCopulaType INDEPENDENT = new LobCopulaType("independent", "INDEPENDENT", ["targets": new ComboBoxTableMultiDimensionalParameter(["A"], ['Targets'], ISegmentMarker)])
    public static final LobCopulaType T = new LobCopulaType("t", "T", ["dependencyMatrix": new ComboBoxMatrixMultiDimensionalParameter([[1d, 0d], [0d, 1d]], ["A", "B"], ISegmentMarker), "degreesOfFreedom": 10])
    public static final LobCopulaType GUMBEL = new LobCopulaType("gumbel", "GUMBEL", ["lambda": 10, "dimension": 2, "targets": new ComboBoxTableMultiDimensionalParameter(["A"], ['Targets'], ISegmentMarker)])

    public static final all = [NORMAL, FRECHETUPPERBOUND, INDEPENDENT, T, GUMBEL]

    private LobCopulaType(String typeName, Map parameters) {
        super(typeName, parameters)
        types[typeName] = this
    }

    private LobCopulaType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
        types[displayName] = this
    }

    public static LobCopulaType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return CopulaStrategyFactory.getCopulaStrategy(this, parameters)
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