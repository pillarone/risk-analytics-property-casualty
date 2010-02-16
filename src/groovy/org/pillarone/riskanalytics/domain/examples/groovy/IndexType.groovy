package org.pillarone.riskanalytics.domain.examples.groovy

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObject

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class IndexType extends AbstractParameterObjectClassifier {

    public static final IndexType ABSOLUTE = new IndexType("absolute", "ABSOLUTE", ["index": 1d])
    public static final IndexType RELATIVEPRIORPERIOD = new IndexType("relative prior period", "RELATIVEPRIORPERIOD",
        ["changeIndex": 0d])


    public static final all = [ABSOLUTE, RELATIVEPRIORPERIOD]

    protected static Map types = [:]
    static {
        IndexType.all.each {
            IndexType.types[it.toString()] = it
        }
    }

    private IndexType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }

    public static IndexType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return IndexType.getStrategy(this, parameters)
    }

    static IIndex getStrategy(IndexType type, Map parameters) {
        IIndex index
        switch (type) {
            case IndexType.ABSOLUTE:
                index = new AbsoluteIndexStrategy(index: parameters['index'])
                break
            case IndexType.RELATIVEPRIORPERIOD:
                index = new RelativeIndexStrategy(changeIndex: parameters['changeIndex'])
                break
        }
        return index
    }

    public String getConstructionString(Map parameters) {
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
        "org.pillarone.riskanalytics.domain.examples.groovy.IndexType.getStrategy(${this.class.name}.${typeName.toUpperCase()}, ${parameterString})"
    }
}