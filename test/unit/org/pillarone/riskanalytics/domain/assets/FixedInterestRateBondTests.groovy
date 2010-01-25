package org.pillarone.riskanalytics.domain.assets

import org.joda.time.DateTime
import org.joda.time.Duration
import org.pillarone.riskanalytics.domain.assets.constants.BondType
import org.pillarone.riskanalytics.domain.assets.constants.Rating
import org.pillarone.riskanalytics.domain.assets.constants.Seniority

/**
 * @author cyril (dot) neyme (at) kpmg (dot) fr
 */

public class FixedInterestRateBondTests extends GroovyTestCase{

    void testUsage(){

    }

    void testGetMarketValue(){

      DateTime startDate = new DateTime(2008,12,14,0,0,0,0)
      DateTime endDate = new DateTime(2009,12,14,0,0,0,0)
      ConstantYieldCurveStrategy yieldCurve = new ConstantYieldCurveStrategy(1)
      BondParameters bond = new BondParameters();
      bond.bondType = BondType.CORPORATE_BOND
      bond.setBookValue(1)
      bond.purchasePrice = 1
      bond.quantity = 10
      bond.coupon = 10
      bond.faceValue = 100
      bond.maturityDate = endDate
      bond.installmentPeriod = Duration.standardDays(365)
      bond.rating = Rating.AAA
      bond.seniority = Seniority.SENIOR_SECURED
      FixedInterestRateBond testBond = new FixedInterestRateBond(bond)

      assertEquals "BondPrice: ", 650, testBond.getMarketValue(startDate, yieldCurve)


    }


    void testGetActuarialRate(){

      DateTime startDate = new DateTime(2008,12,14,0,0,0,0)
      DateTime endDate = new DateTime(2009,12,14,0,0,0,0)
      ConstantYieldCurveStrategy yieldCurve = new ConstantYieldCurveStrategy(1)
      BondParameters bond = new BondParameters();
      bond.bondType = BondType.CORPORATE_BOND
      bond.setBookValue(1)
      bond.purchasePrice = 1
      bond.quantity = 10
      bond.coupon = 10
      bond.faceValue = 100
      bond.maturityDate = endDate
      bond.installmentPeriod = Duration.standardDays(365)
      bond.rating = Rating.AAA
      bond.seniority = Seniority.SENIOR_SECURED
      FixedInterestRateBond testBond = new FixedInterestRateBond(bond)

      assertEquals "ActuarialRate: ", 1, testBond.getActuarialRate(startDate, yieldCurve), 0.0001
    }

    void testGetCapitalGain(){

      DateTime startDate = new DateTime(2008,12,14,0,0,0,0)
      DateTime endDate = new DateTime(2009,12,14,0,0,0,0)
      ConstantYieldCurveStrategy yieldCurve = new ConstantYieldCurveStrategy(1)
      BondParameters bond = new BondParameters();
      bond.bondType = BondType.CORPORATE_BOND
      bond.setBookValue(1)
      bond.purchasePrice = 1
      bond.quantity = 10
      bond.coupon = 10
      bond.faceValue = 100
      bond.maturityDate = endDate
      bond.installmentPeriod = Duration.standardDays(365)
      bond.rating = Rating.AAA
      bond.seniority = Seniority.SENIOR_SECURED
      FixedInterestRateBond testBond = new FixedInterestRateBond(bond)

      assertEquals "Capital Gain: ", 640, testBond.getCapitalGain(startDate, yieldCurve) // allows to test clone() method
    }

    void testIsExpired(){
      DateTime startDate = new DateTime(2008,12,14,0,0,0,0)
      DateTime endDate = new DateTime(2009,12,14,0,0,0,0)
      BondParameters bond = new BondParameters();
      bond.maturityDate = endDate
      FixedInterestRateBond testBond = new FixedInterestRateBond(bond)

      assertFalse "Bond has expired : ", testBond.isExpired(startDate)
    }

    void testDuration(){
      DateTime startDate = new DateTime(2008,12,14,0,0,0,0)
      DateTime endDate = new DateTime(2009,12,14,0,0,0,0)
      ConstantYieldCurveStrategy yieldCurve = new ConstantYieldCurveStrategy(1)
      BondParameters bond = new BondParameters();
      bond.bondType = BondType.CORPORATE_BOND
      bond.setBookValue(1)
      bond.purchasePrice = 1
      bond.quantity = 10
      bond.coupon = 10
      bond.faceValue = 100
      bond.maturityDate = endDate
      bond.installmentPeriod = Duration.standardDays(365)
      bond.rating = Rating.AAA
      bond.seniority = Seniority.SENIOR_SECURED
      FixedInterestRateBond testBond = new FixedInterestRateBond(bond)

      assertEquals "Duration: ", 55/65.0, testBond.getDurationDiscrete(startDate, yieldCurve), 0.00001
    }
}