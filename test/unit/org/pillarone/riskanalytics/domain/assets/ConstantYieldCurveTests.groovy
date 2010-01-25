package org.pillarone.riskanalytics.domain.assets

import org.joda.time.DateTime

/**
 * @author cyril (dot) neyme (at) kpmg (dot) fr
 */

public class ConstantYieldCurveTests extends GroovyTestCase {


  

  void testGetRiskFreeRate(){
    ConstantYieldCurveStrategy yieldCurve = new ConstantYieldCurveStrategy(10.5)
    //DateTime calendar = new DateTime(2009,10,5,0,0,0,0)

    assertEquals "InterestRate: ", 10.5, yieldCurve.getRiskFreeRate(0)
    //assertEquals "InterestRate: ", 10.5, yieldCurve.getZeroCouponRate(calendar);
  }


  void testUsage(){

  }
}