package org.pillarone.riskanalytics.domain.pc.generators.fac;

import org.pillarone.riskanalytics.core.util.GroovyUtils;
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomDistribution;
import umontreal.iro.lecuyer.probdist.DiscreteDistribution;

import java.util.ArrayList;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class FacShareRetentionHelper {

    List<Double> probabilities = new ArrayList<Double>();
    List<Double> policiesCounts = new ArrayList<Double>();
    List<Double> facSharesQuotaShare = new ArrayList<Double>();
    List<Double> facSharesSurplus = new ArrayList<Double>();

    public FacShareRetentionHelper() {
    }

    public void add(double policiesCount, double facShareQuotaShare, double facShareSurplus) {
        policiesCounts.add(policiesCount);
        facSharesQuotaShare.add(facShareQuotaShare);
        facSharesSurplus.add(facShareSurplus);
    }

    private void initProbabilities() {
        double summedPoliciesCounts = 0;
        for (Double count : policiesCounts) {
            summedPoliciesCounts += count;
        }
        for (Double count : policiesCounts) {
            probabilities.add(count / summedPoliciesCounts);
        }
    }

    public RandomDistribution getFacQuotaShareDistribution() {
        if (probabilities.size() < facSharesQuotaShare.size()) {
            initProbabilities();
        }
        RandomDistribution distribution = new RandomDistribution();
        distribution.setDistribution(new DiscreteDistribution(GroovyUtils.asDouble(facSharesQuotaShare), GroovyUtils.asDouble(probabilities), facSharesQuotaShare.size()));
        return distribution;
    }

    public RandomDistribution getFacSurplusSharesDistribution() {
        if (probabilities.size() < facSharesSurplus.size()) {
            initProbabilities();
        }
        RandomDistribution distribution = new RandomDistribution();
        distribution.setDistribution(new DiscreteDistribution(GroovyUtils.asDouble(facSharesSurplus), GroovyUtils.asDouble(probabilities), facSharesQuotaShare.size()));
        return distribution;
    }
}
