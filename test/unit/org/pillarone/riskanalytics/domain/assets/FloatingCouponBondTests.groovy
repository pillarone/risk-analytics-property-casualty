package org.pillarone.riskanalytics.domain.assets

import org.joda.time.DateTime
import org.joda.time.Duration
import org.pillarone.riskanalytics.domain.assets.constants.BondType
import org.pillarone.riskanalytics.domain.assets.constants.Rating
import org.pillarone.riskanalytics.domain.assets.constants.Seniority

/**
 * @author cyril (dot) neyme (at) kpmg (dot) fr
 */

public class FloatingCouponBondTests extends GroovyTestCase {

    void testUsage() {

    }

    void testIfCouponIsFloating() {

        ConstantYieldCurveStrategy yieldCurve = new ConstantYieldCurveStrategy(1)
        DateTime startDate = new DateTime(2008, 12, 14, 0, 0, 0, 0)
        DateTime endDate = new DateTime(2009, 12, 14, 0, 0, 0, 0)
        BondParameters bond = new BondParameters()
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
        FixedInterestRateBond fixedInterestRateBond = new FixedInterestRateBond(bond)
        FloatingCouponBond floatingCouponBond = new FloatingCouponBond(bond)

        assertFalse "MarketValue difference test : ", fixedInterestRateBond.getMarketValue(startDate, yieldCurve) == floatingCouponBond.getMarketValue(startDate, yieldCurve)
        assertFalse "Coupon difference test : ", fixedInterestRateBond.coupon == floatingCouponBond.floatingCoupon(floatingCouponBond.convertDurationToPercentageOfAYear(Duration.standardDays(365)));
    }

}