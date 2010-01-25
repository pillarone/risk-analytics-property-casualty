package org.pillarone.riskanalytics.domain.utils.randomnumbers;

import org.pillarone.riskanalytics.domain.utils.IRandomNumberGenerator;
import org.pillarone.riskanalytics.domain.utils.RandomNumberGeneratorFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 */
public class UniformDoubleList {

    public static List<Double> getDoubles(int number, boolean sorted) {
        IRandomNumberGenerator generator = RandomNumberGeneratorFactory.getUniformGenerator();
        List<Double> doubleList = new ArrayList<Double>();
        for (int i = 0; i < number; i++) {
            doubleList.add((Double) generator.nextValue());
        }
        if (sorted) {
            Collections.sort(doubleList);
            return doubleList;
        }
        else {
            return doubleList;
        }
    }

    public static List<Double> getDoubles(int number) {
       return getDoubles(number, false);
    }
}