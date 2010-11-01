package org.pillarone.riskanalytics.domain.pc.generators.claims

import org.pillarone.riskanalytics.domain.utils.DistributionModified
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.utils.RandomDistribution
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.utils.constraints.DoubleConstraints
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.utils.FrequencyDistributionType
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class PMLClaimsGeneratorStrategy implements IClaimsGeneratorStrategy {

    ConstrainedMultiDimensionalParameter pmlData = new ConstrainedMultiDimensionalParameter([[0d], [0d]], Arrays.asList("return period", "maximum claim"), ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))
    DistributionModified claimsSizeModification = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
    RandomDistribution claimsSizeDistribution;
    RandomDistribution frequencyDistribution;

    public IParameterObjectClassifier getType() {
        return ClaimsGeneratorType.PML
    }

    public Map getParameters() {
        ['pmlData': pmlData, 'claimsSizeModification': claimsSizeModification]
    }

    public DistributionModified getClaimsSizeModification() {
        return claimsSizeModification
    }

    Exposure getClaimsSizeBase() {}

    public RandomDistribution getClaimsSizeDistribution() {
        return claimsSizeDistribution
    }

    public RandomDistribution getFrequencyDistribution() {
        return frequencyDistribution
    }

    DistributionModified getFrequencyModification() {
        return DistributionModifier.getStrategy(DistributionModifier.NONE, new HashMap())
    }

    FrequencyBase getFrequencyBase() {
        return FrequencyBase.ABSOLUTE
    }

    void initDistributions() {

        List<Double> returnPeriods = pmlData.getColumnByName("return period");
        List<Double> observations = pmlData.getColumnByName("maximum claim");
        List<Double> frequencies = [];
        for (Double period: returnPeriods) {
            frequencies.add(1 / period);
        }
        List<Double> cumProbabilities = [];
        for (Double frequency: frequencies) {
            cumProbabilities.add(1d - frequency / frequencies.get(0));
        }
        Map<String, TableMultiDimensionalParameter> parameters = new HashMap<String, TableMultiDimensionalParameter>();
        TableMultiDimensionalParameter table = new TableMultiDimensionalParameter(Arrays.asList(observations, cumProbabilities), Arrays.asList("observations", "cumulative probabilities"));
        parameters.put("discreteEmpiricalCumulativeValues", table);
        claimsSizeDistribution = (RandomDistribution) DistributionType.getStrategy(DistributionType.DISCRETEEMPIRICALCUMULATIVE, parameters);

        double lambda = frequencies[0];
        if (claimsSizeModification.getType().equals(DistributionModifier.TRUNCATED) || claimsSizeModification.getType().equals(DistributionModifier.TRUNCATEDSHIFT)) {
            double min = (Double) claimsSizeModification.getParameters().get("min");
            double max = (Double) claimsSizeModification.getParameters().get("max");
            List<Double> observationsAndBoundaries = [];
            observationsAndBoundaries.addAll(observations);
            observationsAndBoundaries.add(min);
            observationsAndBoundaries.add(max);
            Collections.sort(observationsAndBoundaries);
            int indexMin = observationsAndBoundaries.indexOf(min);
            int indexMax = observationsAndBoundaries.lastIndexOf(max);
            if ((indexMax - 1) == observations.size()) {
                lambda = frequencies[indexMin];
            }
            else {
                lambda = frequencies[indexMin] - frequencies[indexMax - 2];
            }
        }
        else if (claimsSizeModification.getType().equals(DistributionModifier.LEFTTRUNCATEDRIGHTCENSORED)) {
            double min = (Double) claimsSizeModification.getParameters().get("min");
            List<Double> observationsAndMin = new ArrayList<Double>();
            observationsAndMin.addAll(observations);
            observationsAndMin.add(min);
            Collections.sort(observationsAndMin);
            int indexMin = observationsAndMin.indexOf(min);
            lambda = frequencies[indexMin];
        }
        Map<String, Double> lambdaParam = new HashMap<String, Double>();
        lambdaParam.put("lambda", lambda);
        frequencyDistribution = (RandomDistribution) FrequencyDistributionType.getStrategy(FrequencyDistributionType.POISSON, lambdaParam);
    }

}