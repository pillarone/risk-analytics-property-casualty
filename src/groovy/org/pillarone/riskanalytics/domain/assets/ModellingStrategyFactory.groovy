package org.pillarone.riskanalytics.domain.assets
/**
 * @author cyril (dot) neyme (at) kpmg (dot) fr
 */

public class ModellingStrategyFactory {

  private static IModellingStrategy getCIRYieldCurveStrategy(double meanReversionParameter, double riskAversionParameter,

  double longRunMean, double volatility, double initialInterestRate){
      return new YieldCurveCIRStrategy(meanReversionParameter, riskAversionParameter,
                    longRunMean, volatility, initialInterestRate);
  }

  private static IModellingStrategy getConstantYieldCurveStrategy(double rate){
      return new ConstantYieldCurveStrategy(rate)
  }



  static IModellingStrategy getModellingStrategy(TermStructureType type, Map parameters) {
      IModellingStrategy curve
      switch (type) {
        case TermStructureType.CIR:
          curve = getCIRYieldCurveStrategy(parameters["meanReversionParameter"], parameters["riskAversionParameter"], parameters["longRunMean"],
                  parameters["volatility"], parameters["initialInterestRate"])
          break;
        case TermStructureType.CONSTANT:
          curve = getConstantYieldCurveStrategy(parameters["rate"])
          break
        //...
      }
      return curve;
  }



}