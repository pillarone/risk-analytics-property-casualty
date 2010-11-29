package org.pillarone.riskanalytics.domain.pc.generators.claims

import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.RandomDistribution
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.underwriting.IUnderwritingInfoMarker
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.utils.constraints.DoubleConstraints
import org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType
import models.claims.ClaimsModel
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.utils.DistributionModified
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.core.util.MathUtils
import umontreal.iro.lecuyer.rng.RandomStreamBase
import org.pillarone.riskanalytics.domain.utils.randomnumbers.UniformDoubleList
import org.pillarone.riskanalytics.domain.utils.FrequencyDistributionType
import org.pillarone.riskanalytics.domain.pc.constants.FrequencySeverityClaimType
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class PMLClaimsGeneratorStrategyTest extends GroovyTestCase {

    TypableClaimsGenerator claimsGenerator

    void setUp() {
        claimsGenerator = new TypableClaimsGenerator()
        claimsGenerator.setParmAssociateExposureInfo(RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:]))
        claimsGenerator.setSimulationScope(new SimulationScope(model: new ClaimsModel(), iterationScope: new IterationScope(periodScope: new PeriodScope())))
        ConstraintsFactory.registerConstraint(new DoubleConstraints())
    }


    void testPMLDataNoModification() {

        ConstrainedMultiDimensionalParameter pmlData = new ConstrainedMultiDimensionalParameter(
                [[0.1d, 0.2d, 0.3d, 0.4d, 0.7d, 1.5d, 2.2d, 4.0d, 5.0d], [0.1d, 45d, 200d, 500d, 600d, 800d, 1500d, 2016d, 4000d]], ["return period", "maximum claim"], ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))
        DistributionModified claimsSizeModification = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
        claimsGenerator.setParmClaimsModel(ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.PML,
                ["pmlData": pmlData, "claimsSizeModification": claimsSizeModification,'produceClaim': FrequencySeverityClaimType.SINGLE]))


        MathUtils.initRandomStreamBase(1531)
        RandomStreamBase referenceStream = MathUtils.getRandomStream(MathUtils.getRandomStreamBase(), 0).clone()
        List<Double> randomNumbers = UniformDoubleList.getDoubles((int) 300, false, referenceStream);
        RandomDistribution poissonDist = (RandomDistribution) FrequencyDistributionType.getStrategy(FrequencyDistributionType.POISSON,
                new HashMap<String, Double>() {
                    {
                        put("lambda", 10);
                    }
                });
        double frequency = poissonDist.getDistribution().inverseF(randomNumbers[0]);
        List<Double> cumProb = []; cumProb[0] = 0
        double[] periods = [0.1d, 0.2d, 0.3d, 0.4d, 0.7d, 1.5d, 2.2d, 4.0d, 5.0d]
        for (int i = 1; i < 9; i++) {
            cumProb[i] = 1d - 0.1d / periods[i]
        }
        TableMultiDimensionalParameter cumValues = new TableMultiDimensionalParameter([[0.1d, 45d, 200d, 500d, 600d, 800d, 1500d, 2016d, 4000d], cumProb], ['observations', 'cumulative probabilities'])
        Map distributionData = new HashMap();
        distributionData.put("discreteEmpiricalCumulativeValues", cumValues);
        RandomDistribution cumEmpiricalDistribution = DistributionType.getStrategy(DistributionType.DISCRETEEMPIRICALCUMULATIVE, distributionData);
        List<Double> claimValues = new ArrayList<Double>();
        for (int i = 0; i < frequency; i++) {
            claimValues.add(cumEmpiricalDistribution.getDistribution().inverseF(randomNumbers[i + 1]));
        }

        claimsGenerator.doCalculation();

        List<Double> outClaimsUltimate = []
        for (int i = 0; i < claimsGenerator.outClaims.size(); i++) {
            outClaimsUltimate[i] = claimsGenerator.outClaims.get(i).getUltimate()
        }

        assertEquals "number of claims", frequency, claimsGenerator.outClaims.size()
        assertEquals "claims", true, claimValues.containsAll(outClaimsUltimate)

    }

    void testPMLDataShift() {

        ConstrainedMultiDimensionalParameter pmlData = new ConstrainedMultiDimensionalParameter(
                [[0.1d, 0.2d, 0.3d, 0.4d, 0.7d, 1.5d, 2.2d, 4.0d, 5.0d], [0.1d, 45d, 200d, 500d, 600d, 800d, 1500d, 2016d, 4000d]], ["return period", "maximum claim"], ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))
        DistributionModified claimsSizeModification = DistributionModifier.getStrategy(DistributionModifier.SHIFT, ["shift": 1000])
        claimsGenerator.setParmClaimsModel(ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.PML,
                ["pmlData": pmlData, "claimsSizeModification": claimsSizeModification,'produceClaim': FrequencySeverityClaimType.SINGLE]))

        MathUtils.initRandomStreamBase(1731)
        RandomStreamBase referenceStream = MathUtils.getRandomStream(MathUtils.getRandomStreamBase(), 0).clone()
        List<Double> randomNumbers = UniformDoubleList.getDoubles((int) 300, false, referenceStream);
        RandomDistribution poissonDist = (RandomDistribution) FrequencyDistributionType.getStrategy(FrequencyDistributionType.POISSON,
                new HashMap<String, Double>() {
                    {put("lambda", 10);}
                });
        double frequency = poissonDist.getDistribution().inverseF(randomNumbers[0]);
        List<Double> cumProb = []; cumProb[0] = 0
        double[] periods = [0.1d, 0.2d, 0.3d, 0.4d, 0.7d, 1.5d, 2.2d, 4.0d, 5.0d]
        for (int i = 1; i < 9; i++) {
            cumProb[i] = 1d - 0.1d / periods[i]
        }
        TableMultiDimensionalParameter cumValues = new TableMultiDimensionalParameter([[0.1d, 45d, 200d, 500d, 600d, 800d, 1500d, 2016d, 4000d], cumProb], ['observations', 'cumulative probabilities'])
        Map distributionData = new HashMap();
        distributionData.put("discreteEmpiricalCumulativeValues", cumValues);
        RandomDistribution cumEmpiricalDistribution = DistributionType.getStrategy(DistributionType.DISCRETEEMPIRICALCUMULATIVE, distributionData);
        List<Double> claimValues = new ArrayList<Double>();
        for (int i = 0; i < frequency; i++) {
            claimValues.add(cumEmpiricalDistribution.getDistribution().inverseF(randomNumbers[i + 1]) + 1000);
        }

        claimsGenerator.doCalculation();

        List<Double> outClaimsUltimate = []
        for (int i = 0; i < claimsGenerator.outClaims.size(); i++) {
            outClaimsUltimate[i] = claimsGenerator.outClaims.get(i).getUltimate()
        }

        assertEquals "number of claims", frequency, claimsGenerator.outClaims.size()
        assertEquals "claims", true, claimValues.containsAll(outClaimsUltimate)

    }

    void testPMLDataTruncation() {

        ConstrainedMultiDimensionalParameter pmlData = new ConstrainedMultiDimensionalParameter(
                [[0.01d, 0.05d, 0.1d, 0.2d, 0.7d, 1.5d, 2.2d, 4.0d, 5.0d], [0.1d, 45d, 200d, 500d, 600d, 800d, 1500d, 2016d, 4000d]], ["return period", "maximum claim"], ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))
        DistributionModified claimsSizeModification = DistributionModifier.getStrategy(DistributionModifier.TRUNCATED, ["min": 200d, "max": 8000d])
        claimsGenerator.setParmClaimsModel(ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.PML,
                ["pmlData": pmlData, "claimsSizeModification": claimsSizeModification,'produceClaim': FrequencySeverityClaimType.SINGLE]))


        MathUtils.initRandomStreamBase(1031)
        RandomStreamBase referenceStream = MathUtils.getRandomStream(MathUtils.getRandomStreamBase(), 0).clone()
        List<Double> randomNumbers = UniformDoubleList.getDoubles((int) 300, false, referenceStream);
        RandomDistribution poissonDist = (RandomDistribution) FrequencyDistributionType.getStrategy(FrequencyDistributionType.POISSON,
                new HashMap<String, Double>() {
                    {
                        put("lambda", 1 / 0.1d);
                    }
                });
        double frequency = poissonDist.getDistribution().inverseF(randomNumbers[0]);
        List<Double> cumProb = []; cumProb[0] = 0
        double[] periods = [0.01d, 0.05d, 0.1d, 0.2d, 0.7d, 1.5d, 2.2d, 4.0d, 5.0d]
        for (int i = 1; i < 9; i++) {
            cumProb[i] = 1d - 0.01d / periods[i]
        }
        List<Double> truncatedCumProb = []
        for (int i = 0; i < 7; i++) {
            truncatedCumProb[i] = (cumProb[i + 2] - cumProb[1]) / (1 - cumProb[1])
        }
        TableMultiDimensionalParameter cumValues = new TableMultiDimensionalParameter([[200d, 500d, 600d, 800d, 1500d, 2016d, 4000d], truncatedCumProb], ['observations', 'cumulative probabilities'])
        Map distributionData = new HashMap();
        distributionData.put("discreteEmpiricalCumulativeValues", cumValues);
        RandomDistribution cumEmpiricalDistribution = DistributionType.getStrategy(DistributionType.DISCRETEEMPIRICALCUMULATIVE, distributionData);
        List<Double> claimValues = new ArrayList<Double>();
        for (int i = 0; i < frequency; i++) {
            claimValues.add(cumEmpiricalDistribution.getDistribution().inverseF(randomNumbers[i + 1]));
        }

        claimsGenerator.doCalculation();

        List<Double> outClaimsUltimate = []
        for (int i = 0; i < claimsGenerator.outClaims.size(); i++) {
            outClaimsUltimate[i] = claimsGenerator.outClaims.get(i).getUltimate()
        }

        assertEquals "number of claims", frequency, claimsGenerator.outClaims.size()
        assertEquals "claims", true, claimValues.containsAll(outClaimsUltimate)

    }

    void testPMLDataTruncationShift() {

        ConstrainedMultiDimensionalParameter pmlData = new ConstrainedMultiDimensionalParameter(
                [[0.01d, 0.05d, 0.1d, 0.2d, 0.7d, 1.5d, 2.2d, 4.0d, 5.0d], [0.1d, 45d, 200d, 500d, 600d, 800d, 1500d, 2016d, 4000d]], ["return period", "maximum claim"], ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))
        DistributionModified claimsSizeModification = DistributionModifier.getStrategy(DistributionModifier.TRUNCATEDSHIFT, ["min": 200d, "max": 800d, "shift": 1000])
        claimsGenerator.setParmClaimsModel(ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.PML,
                ["pmlData": pmlData, "claimsSizeModification": claimsSizeModification,'produceClaim': FrequencySeverityClaimType.SINGLE]))


        MathUtils.initRandomStreamBase(5031)
        RandomStreamBase referenceStream = MathUtils.getRandomStream(MathUtils.getRandomStreamBase(), 0).clone()
        List<Double> randomNumbers = UniformDoubleList.getDoubles((int) 300, false, referenceStream);
        RandomDistribution poissonDist = (RandomDistribution) FrequencyDistributionType.getStrategy(FrequencyDistributionType.POISSON,
                new HashMap<String, Double>() {
                    {
                        put("lambda", 1 / 0.1d - 1 / 1.5);
                    }
                });
        double frequency = poissonDist.getDistribution().inverseF(randomNumbers[0]);
        List<Double> cumProb = []; cumProb[0] = 0
        double[] periods = [0.01d, 0.05d, 0.1d, 0.2d, 0.7d, 1.5d, 2.2d, 4.0d, 5.0d]
        for (int i = 1; i < 9; i++) {
            cumProb[i] = 1d - 0.01d / periods[i]
        }
        List<Double> truncatedCumProb = []
        for (int i = 0; i < 4; i++) {
            truncatedCumProb[i] = (cumProb[i + 2] - cumProb[1]) / (cumProb[5] - cumProb[1])
        }
        TableMultiDimensionalParameter cumValues = new TableMultiDimensionalParameter([[200d, 500d, 600d, 800d], truncatedCumProb], ['observations', 'cumulative probabilities'])
        Map distributionData = new HashMap();
        distributionData.put("discreteEmpiricalCumulativeValues", cumValues);
        RandomDistribution cumEmpiricalDistribution = DistributionType.getStrategy(DistributionType.DISCRETEEMPIRICALCUMULATIVE, distributionData);
        List<Double> claimValues = new ArrayList<Double>();
        for (int i = 0; i < frequency; i++) {
            claimValues.add(cumEmpiricalDistribution.getDistribution().inverseF(randomNumbers[i + 1]) + 1000);
        }

        claimsGenerator.doCalculation();

        List<Double> outClaimsUltimate = []
        for (int i = 0; i < claimsGenerator.outClaims.size(); i++) {
            outClaimsUltimate[i] = claimsGenerator.outClaims.get(i).getUltimate()
        }

        assertEquals "number of claims", frequency, claimsGenerator.outClaims.size()
        assertEquals "claims", true, claimValues.containsAll(outClaimsUltimate)

    }

    void testPMLDataCensored() {

        ConstrainedMultiDimensionalParameter pmlData = new ConstrainedMultiDimensionalParameter(
                [[0.01d, 0.05d, 0.1d, 0.2d, 0.7d, 1.5d, 2.2d, 4.0d, 5.0d], [0.1d, 45d, 200d, 500d, 600d, 800d, 1500d, 2016d, 4000d]], ["return period", "maximum claim"], ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))
        DistributionModified claimsSizeModification = DistributionModifier.getStrategy(DistributionModifier.CENSORED, ["min": 0, "max": 800d])
        claimsGenerator.setParmClaimsModel(ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.PML,
                ["pmlData": pmlData, "claimsSizeModification": claimsSizeModification,'produceClaim': FrequencySeverityClaimType.SINGLE]))


        MathUtils.initRandomStreamBase(1037)
        RandomStreamBase referenceStream = MathUtils.getRandomStream(MathUtils.getRandomStreamBase(), 0).clone()
        List<Double> randomNumbers = UniformDoubleList.getDoubles((int) 300, false, referenceStream);
        RandomDistribution poissonDist = (RandomDistribution) FrequencyDistributionType.getStrategy(FrequencyDistributionType.POISSON,
                new HashMap<String, Double>() {
                    {
                        put("lambda", 1 / 0.01d);
                    }
                });
        double frequency = poissonDist.getDistribution().inverseF(randomNumbers[0]);
        List<Double> cumProb = []; cumProb[0] = 0
        double[] periods = [0.01d, 0.05d, 0.1d, 0.2d, 0.7d, 1.5d, 2.2d, 4.0d, 5.0d]
        for (int i = 1; i < 9; i++) {
            cumProb[i] = 1d - 0.01d / periods[i]
        }

        TableMultiDimensionalParameter cumValues = new TableMultiDimensionalParameter([[0.1d, 45d, 200d, 500d, 600d, 800d, 1500d, 2016d, 4000d], cumProb], ['observations', 'cumulative probabilities'])
        Map distributionData = new HashMap();
        distributionData.put("discreteEmpiricalCumulativeValues", cumValues);
        RandomDistribution cumEmpiricalDistribution = DistributionType.getStrategy(DistributionType.DISCRETEEMPIRICALCUMULATIVE, distributionData);
        List<Double> claimValues = new ArrayList<Double>();
        for (int i = 0; i < frequency; i++) {
            claimValues.add(Math.min(cumEmpiricalDistribution.getDistribution().inverseF(randomNumbers[i + 1]), 800d));
        }

        claimsGenerator.doCalculation();

        List<Double> outClaimsUltimate = []
        for (int i = 0; i < claimsGenerator.outClaims.size(); i++) {
            outClaimsUltimate[i] = claimsGenerator.outClaims.get(i).getUltimate()
        }

        assertEquals "number of claims", frequency, claimsGenerator.outClaims.size()
        assertEquals "claims", true, claimValues.containsAll(outClaimsUltimate)

    }

    void testPMLDataCensoredShift() {

        ConstrainedMultiDimensionalParameter pmlData = new ConstrainedMultiDimensionalParameter(
                [[0.01d, 0.05d, 0.1d, 0.2d, 0.7d, 1.5d, 2.2d, 4.0d, 5.0d], [0.1d, 45d, 200d, 500d, 600d, 800d, 1500d, 2016d, 4000d]], ["return period", "maximum claim"], ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))
        DistributionModified claimsSizeModification = DistributionModifier.getStrategy(DistributionModifier.CENSOREDSHIFT, ["min": 0, "max": 800d, "shift": 2000])
        claimsGenerator.setParmClaimsModel(ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.PML,
                ["pmlData": pmlData, "claimsSizeModification": claimsSizeModification,'produceClaim': FrequencySeverityClaimType.SINGLE]))


        MathUtils.initRandomStreamBase(10037)
        RandomStreamBase referenceStream = MathUtils.getRandomStream(MathUtils.getRandomStreamBase(), 0).clone()
        List<Double> randomNumbers = UniformDoubleList.getDoubles((int) 300, false, referenceStream);
        RandomDistribution poissonDist = (RandomDistribution) FrequencyDistributionType.getStrategy(FrequencyDistributionType.POISSON,
                new HashMap<String, Double>() {
                    {
                        put("lambda", 1 / 0.01d);
                    }
                });
        double frequency = poissonDist.getDistribution().inverseF(randomNumbers[0]);
        List<Double> cumProb = []; cumProb[0] = 0
        double[] periods = [0.01d, 0.05d, 0.1d, 0.2d, 0.7d, 1.5d, 2.2d, 4.0d, 5.0d]
        for (int i = 1; i < 9; i++) {
            cumProb[i] = 1d - 0.01d / periods[i]
        }

        TableMultiDimensionalParameter cumValues = new TableMultiDimensionalParameter([[0.1d, 45d, 200d, 500d, 600d, 800d, 1500d, 2016d, 4000d], cumProb], ['observations', 'cumulative probabilities'])
        Map distributionData = new HashMap();
        distributionData.put("discreteEmpiricalCumulativeValues", cumValues);
        RandomDistribution cumEmpiricalDistribution = DistributionType.getStrategy(DistributionType.DISCRETEEMPIRICALCUMULATIVE, distributionData);
        List<Double> claimValues = new ArrayList<Double>();
        for (int i = 0; i < frequency; i++) {
            claimValues.add(Math.min(cumEmpiricalDistribution.getDistribution().inverseF(randomNumbers[i + 1]), 800d) + 2000);
        }

        claimsGenerator.doCalculation();

        List<Double> outClaimsUltimate = []
        for (int i = 0; i < claimsGenerator.outClaims.size(); i++) {
            outClaimsUltimate[i] = claimsGenerator.outClaims.get(i).getUltimate()
        }

        assertEquals "number of claims", frequency, claimsGenerator.outClaims.size()
        assertEquals "claims", true, claimValues.containsAll(outClaimsUltimate)

    }

    void testPMLDataLeftTruncationRightCensored() {

        ConstrainedMultiDimensionalParameter pmlData = new ConstrainedMultiDimensionalParameter(
                [[0.01d, 0.05d, 0.1d, 0.2d, 0.7d, 1.5d, 2.2d, 4.0d, 5.0d], [0.1d, 45d, 200d, 500d, 600d, 800d, 1500d, 2016d, 4000d]], ["return period", "maximum claim"], ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))
        DistributionModified claimsSizeModification = DistributionModifier.getStrategy(DistributionModifier.LEFTTRUNCATEDRIGHTCENSORED, ["min": 200d, "max": 800d])
        claimsGenerator.setParmClaimsModel(ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.PML,
                ["pmlData": pmlData, "claimsSizeModification": claimsSizeModification,'produceClaim': FrequencySeverityClaimType.SINGLE]))


        MathUtils.initRandomStreamBase(81031)
        RandomStreamBase referenceStream = MathUtils.getRandomStream(MathUtils.getRandomStreamBase(), 0).clone()
        List<Double> randomNumbers = UniformDoubleList.getDoubles((int) 300, false, referenceStream);
        RandomDistribution poissonDist = (RandomDistribution) FrequencyDistributionType.getStrategy(FrequencyDistributionType.POISSON,
                new HashMap<String, Double>() {
                    {put("lambda", 1 / 0.1d);}
                });
        double frequency = poissonDist.getDistribution().inverseF(randomNumbers[0]);
        List<Double> cumProb = []; cumProb[0] = 0
        double[] periods = [0.01d, 0.05d, 0.1d, 0.2d, 0.7d, 1.5d, 2.2d, 4.0d, 5.0d]
        for (int i = 1; i < 9; i++) {
            cumProb[i] = 1d - 0.01d / periods[i]
        }
        List<Double> truncatedCumProb = []
        for (int i = 0; i < 7; i++) {
            truncatedCumProb[i] = (cumProb[i + 2] - cumProb[1]) / (1 - cumProb[1])
        }
        TableMultiDimensionalParameter cumValues = new TableMultiDimensionalParameter([[200d, 500d, 600d, 800d, 1500d, 2016d, 4000d], truncatedCumProb], ['observations', 'cumulative probabilities'])
        Map distributionData = new HashMap();
        distributionData.put("discreteEmpiricalCumulativeValues", cumValues);
        RandomDistribution cumEmpiricalDistribution = DistributionType.getStrategy(DistributionType.DISCRETEEMPIRICALCUMULATIVE, distributionData);
        List<Double> claimValues = new ArrayList<Double>();
        for (int i = 0; i < frequency; i++) {
            claimValues.add(Math.min(cumEmpiricalDistribution.getDistribution().inverseF(randomNumbers[i + 1]), 800d));
        }

        claimsGenerator.doCalculation();

        List<Double> outClaimsUltimate = []
        for (int i = 0; i < claimsGenerator.outClaims.size(); i++) {
            outClaimsUltimate[i] = claimsGenerator.outClaims.get(i).getUltimate()
        }

        assertEquals "number of claims", frequency, claimsGenerator.outClaims.size()
        assertEquals "claims", true, claimValues.containsAll(outClaimsUltimate)

    }

}
