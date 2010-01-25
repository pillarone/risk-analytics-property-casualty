package org.pillarone.riskanalytics.domain.assets

/**
 * @author cyril (dot) neyme (at) kpmg (dot) fr
 */

public class YieldCurveCIRStrategyTests extends GroovyTestCase {

    void testUsage() {

    }

    void testTermStructureDiscountFactorCalculation() {
        CIRYieldModellingChoices curve = new CIRYieldModellingChoices()
      
        curve.meanReversionParameter = 0.01
        curve.riskAversionParameter = 0.0
        curve.longRunMean = 0.08
        curve.volatility = 0.1
        curve.initialInterestRate = 0.05

        YieldCurveCIRStrategy yieldCurve = new YieldCurveCIRStrategy(curve)

        assertEquals "date 0:", 1, yieldCurve.termStructureDiscountFactorCalculation(0)
        assertEquals "date 1:", 0.95117, yieldCurve.termStructureDiscountFactorCalculation(1), 0.0001
        assertEquals "date 0.5:", 0.97528, yieldCurve.termStructureDiscountFactorCalculation(0.5),0.0001
        assertEquals "date .75:", 0.96315, yieldCurve.termStructureDiscountFactorCalculation(0.75),0.0001
    }


    void testYield(){
        CIRYieldModellingChoices curve = new CIRYieldModellingChoices()

        curve.meanReversionParameter = 0.01
        curve.riskAversionParameter = 0.0
        curve.longRunMean = 0.08
        curve.volatility = 0.1
        curve.initialInterestRate = 0.05

        YieldCurveCIRStrategy yieldCurve = new YieldCurveCIRStrategy(curve)

        //assertEquals "date 0:", 1, yieldCurve.termStructureDiscountFactorCalculation(0)
        assertEquals "date 1:", 0.05134, yieldCurve.termStructureYieldCalculation(1), 0.0001
        assertEquals "date 0.5:", 0.05132, yieldCurve.termStructureYieldCalculation(0.5),0.0001
        assertEquals "date .75:", 0.05134, yieldCurve.termStructureYieldCalculation(0.75),0.0001
    }

    void testLongRunMean() {

    }

}