package org.pillarone.riskanalytics.domain.utils.randomnumbers;

import org.pillarone.riskanalytics.core.parameterization.IParameterObject;
import umontreal.iro.lecuyer.randvarmulti.RandomMultivariateGen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author ali.majidi (at) munichre (dot) com, stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class MultiRandomNumberGenerator implements IMultiRandomGenerator, IParameterObject {

    private RandomMultivariateGen generator;
    DependencyType type;

    public DependencyType getType() {
        return type;
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }

    public List<Number> nextVector() {
        double[] p = new double[generator.getDimension()];
        List<Number> randomVector = new ArrayList<Number>(p.length);
        generator.nextPoint(p);
        for (int i = 0; i < generator.getDimension(); i++) {
            randomVector.add(p[i]);
        }
        return randomVector;
    }
}