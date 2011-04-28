package org.pillarone.riskanalytics.domain.assets;

import org.apache.commons.lang.ArrayUtils;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

import java.util.GregorianCalendar;
import java.util.Map;

/**
 * @author cyril (dot) neyme (at) kpmg (dot) fr
 */
public class ConstantYieldCurveStrategy extends AbstractParameterObject implements ITermStructure, IModellingStrategy {

    private double rate = 0;
    static final TermStructureType type = TermStructureType.CONSTANT;

    public ConstantYieldCurveStrategy(double rate) {
        this.setRate(rate);
    }

    public double getRiskFreeRate(double time) {
        return getRate();
    }

    public double getZeroCouponRate(GregorianCalendar date) {
        return getRate();
    }

    public IParameterObjectClassifier getType() {
        return type;
    }

    public Map getParameters() {
        return ArrayUtils.toMap(new Object[][]{
            {"rate", rate}
        });
    }

    public void reset() {

    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
