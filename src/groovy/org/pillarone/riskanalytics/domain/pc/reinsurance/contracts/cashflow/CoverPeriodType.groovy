package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow

import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class CoverPeriodType extends AbstractParameterObjectClassifier {

    public static final CoverPeriodType PERIOD = new CoverPeriodType("period", "PERIOD",
        ["start": new DateTime(2010,1,1,0,0,0,0), "end": new DateTime(2010,12,31,0,0,0,0)])
    public static final CoverPeriodType FULL = new CoverPeriodType("full", "FULL", [:])
    public static final CoverPeriodType NONE = new CoverPeriodType("none", "NONE", [:])

    public static final all = [PERIOD, FULL, NONE]

    protected static Map types = [:]
    static {
        CoverPeriodType.all.each {
            CoverPeriodType.types[it.toString()] = it
        }
    }

    private CoverPeriodType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }

    public static CoverPeriodType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getCoverPeriod(this, parameters)
    }

    static ICoverPeriod getCoverPeriod(CoverPeriodType type, Map parameters) {
        switch (type) {
            case CoverPeriodType.NONE:
                return new NoPeriodCoveredStrategy()
            case CoverPeriodType.FULL:
                return new FullPeriodCoveredStrategy()
            case CoverPeriodType.PERIOD:
                return new PeriodCoveredStrategy(start : parameters['start'], end: parameters['end'])
        }
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
        "org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow.CoverPeriodType.getCoverPeriod(${this.class.name}.${typeName.toUpperCase()}, ${parameterString})"
    }
}