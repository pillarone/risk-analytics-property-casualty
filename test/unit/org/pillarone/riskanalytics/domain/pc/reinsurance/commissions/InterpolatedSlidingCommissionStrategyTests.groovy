package org.pillarone.riskanalytics.domain.pc.reinsurance.commissions

import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfo

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class InterpolatedSlidingCommissionStrategyTests extends GroovyTestCase {

    private ICommissionStrategy commissionStrategy
    private List<UnderwritingInfo> uwInfo
    private List<Claim> claims

    static ICommissionStrategy getInterpolatedSlidingCommissionStrategy(List<Double> lossRatios, List<Double> commissionRates) {

        ICommissionStrategy commissionStrategy = CommissionStrategyType.getStrategy(
                CommissionStrategyType.INTERPOLATEDSLIDINGCOMMISSION,
                ['commissionBands': new ConstrainedMultiDimensionalParameter(
                        [lossRatios, commissionRates],
                        [InterpolatedSlidingCommissionStrategy.LOSS_RATIO, InterpolatedSlidingCommissionStrategy.COMMISSION],
                        ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))])
        return commissionStrategy
    }

    void testOneRatio() {
        ICommissionStrategy commissionStrategy = getInterpolatedSlidingCommissionStrategy([0.35d], [0.2d])

        CededUnderwritingInfo underwritingInfo200 = new CededUnderwritingInfo(premium: 200, commission: -50)
        CededUnderwritingInfo underwritingInfo100 = new CededUnderwritingInfo(premium: 100, commission: -5)

        Claim claim70 = new Claim(value: 70)
        Claim claim10 = new Claim(value: 10)
        Claim claim25 = new Claim(value: 25)
        Claim claim100 = new Claim(value: 100)

        List underwritingInfos = [underwritingInfo200, underwritingInfo100]
        List claims = [claim10, claim70]

        commissionStrategy.calculateCommission claims, underwritingInfos, false, false
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', -200 * 0.2, underwritingInfos[0].commission
        assertEquals 'underwritingInfo200', -200 * 0.2, underwritingInfos[0].fixedCommission
        assertEquals 'underwritingInfo200', -0d, underwritingInfos[0].variableCommission, 1E-8
        assertEquals 'underwritingInfo100', -100 * 0.2, underwritingInfos[1].commission
        assertEquals 'underwritingInfo100', -100 * 0.2, underwritingInfos[1].fixedCommission
        assertEquals 'underwritingInfo100', -0d, underwritingInfos[1].variableCommission, 1E-8

        claims.add(claim25)

        commissionStrategy.calculateCommission claims, underwritingInfos, false, false
        assertEquals 'underwritingInfo200', -200 * 0.2, underwritingInfos[0].commission
        assertEquals 'underwritingInfo200', -200 * 0.2, underwritingInfos[0].fixedCommission
        assertEquals 'underwritingInfo200', -0d, underwritingInfos[0].variableCommission, 1E-8
        assertEquals 'underwritingInfo100', -100 * 0.2, underwritingInfos[1].commission
        assertEquals 'underwritingInfo100', -100 * 0.2, underwritingInfos[1].fixedCommission
        assertEquals 'underwritingInfo100', -0d, underwritingInfos[1].variableCommission, 1E-8

        claims.add(claim100)

        commissionStrategy.calculateCommission claims, underwritingInfos, false, false
        assertEquals 'underwritingInfo200', -200 * 0.2, underwritingInfos[0].commission
        assertEquals 'underwritingInfo200', -200 * 0.2, underwritingInfos[0].fixedCommission
        assertEquals 'underwritingInfo200', -0d, underwritingInfos[0].variableCommission, 1E-8
        assertEquals 'underwritingInfo100', -100 * 0.2, underwritingInfos[1].commission
        assertEquals 'underwritingInfo100', -100 * 0.2, underwritingInfos[1].fixedCommission
        assertEquals 'underwritingInfo100', -0d, underwritingInfos[1].variableCommission, 1E-8

        commissionStrategy.calculateCommission claims, underwritingInfos, false, true
        assertEquals 'underwritingInfo200', -200 * 0.2 - 200 * 0.2, underwritingInfos[0].commission
        assertEquals 'underwritingInfo200', -200 * 0.2 - 200 * 0.2, underwritingInfos[0].fixedCommission
        assertEquals 'underwritingInfo200', -0d, underwritingInfos[0].variableCommission, 1E-8
        assertEquals 'underwritingInfo100', -100 * 0.2 - 100 * 0.2, underwritingInfos[1].commission
        assertEquals 'underwritingInfo100', -100 * 0.2 - 100 * 0.2, underwritingInfos[1].fixedCommission
        assertEquals 'underwritingInfo100', -0d, underwritingInfos[1].variableCommission, 1E-8

        underwritingInfos[0].setCommission(-25)
        underwritingInfos[0].setFixedCommission(-10)
        underwritingInfos[0].setVariableCommission(-15)
        underwritingInfos[1].setCommission(-30)
        underwritingInfos[1].setFixedCommission(-25)
        underwritingInfos[1].setVariableCommission(-5)
        commissionStrategy.calculateCommission claims, underwritingInfos, false, true
        assertEquals 'underwritingInfo200', -200 * 0.2 - 25, underwritingInfos[0].commission
        assertEquals 'underwritingInfo200', -200 * 0.2 - 10, underwritingInfos[0].fixedCommission
        assertEquals 'underwritingInfo200', -15d, underwritingInfos[0].variableCommission
        assertEquals 'underwritingInfo100', -100 * 0.2 - 30, underwritingInfos[1].commission
        assertEquals 'underwritingInfo100', -100 * 0.2 - 25, underwritingInfos[1].fixedCommission
        assertEquals 'underwritingInfo100', -5d, underwritingInfos[1].variableCommission
    }

    void testTwoRatios() {
        ICommissionStrategy commissionStrategy = getInterpolatedSlidingCommissionStrategy([0.3d, 0.5d], [0.4d, 0.3d])

        UnderwritingInfo underwritingInfo200 = new CededUnderwritingInfo(premium: 200, commission: -50, fixedCommission: -10, variableCommission: -40)
        UnderwritingInfo underwritingInfo100 = new CededUnderwritingInfo(premium: 100, commission: -5, fixedCommission: -2, variableCommission: -3)

        Claim claim60 = new Claim(value: 60)
        Claim claim10 = new Claim(value: 10)
        Claim claim20 = new Claim(value: 20)

        List underwritingInfos = [underwritingInfo200, underwritingInfo100]
        List claims = [claim60, claim10]

        commissionStrategy.calculateCommission claims, underwritingInfos, false, true
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', -200 * 0.4 - 50, underwritingInfos[0].commission
        assertEquals 'underwritingInfo200', -200 * 0.3 - 10, underwritingInfos[0].fixedCommission
        assertEquals 'underwritingInfo200', -200 * 0.1 - 40d, underwritingInfos[0].variableCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.4 - 5, underwritingInfos[1].commission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.3 - 2, underwritingInfos[1].fixedCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.1 - 3, underwritingInfos[1].variableCommission, 1E-14

        commissionStrategy.calculateCommission claims, underwritingInfos, false, false
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', -200 * 0.4, underwritingInfos[0].commission, 1E-14
        assertEquals 'underwritingInfo200', -200 * 0.3, underwritingInfos[0].fixedCommission, 1E-14
        assertEquals 'underwritingInfo200', -200 * 0.1, underwritingInfos[0].variableCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.4, underwritingInfos[1].commission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.3, underwritingInfos[1].fixedCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.1, underwritingInfos[1].variableCommission, 1E-14

        claims.add(claim20)

        commissionStrategy.calculateCommission claims, underwritingInfos, false, false
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', -200 * 0.4, underwritingInfos[0].commission, 1E-14
        assertEquals 'underwritingInfo200', -200 * 0.3, underwritingInfos[0].fixedCommission, 1E-14
        assertEquals 'underwritingInfo200', -200 * 0.1, underwritingInfos[0].variableCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.4, underwritingInfos[1].commission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.3, underwritingInfos[1].fixedCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.1, underwritingInfos[1].variableCommission, 1E-14

        claims.add(new Claim(value: 30))

        commissionStrategy.calculateCommission claims, underwritingInfos, false, false
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', -200 * (0.4 * 0.5 + 0.3 * 0.5), underwritingInfos[0].commission, 1E-14
        assertEquals 'underwritingInfo200', -200 * 0.3, underwritingInfos[0].fixedCommission, 1E-14
        assertEquals 'underwritingInfo200', -200 * 0.05, underwritingInfos[0].variableCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * (0.4 * 0.5 + 0.3 * 0.5), underwritingInfos[1].commission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.3, underwritingInfos[1].fixedCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.05, underwritingInfos[1].variableCommission, 1E-14

        claims.add(new Claim(value: 15))

        commissionStrategy.calculateCommission claims, underwritingInfos, false, false
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', -200 * (0.4 * 0.25 + 0.3 * 0.75), underwritingInfos[0].commission, 1E-14
        assertEquals 'underwritingInfo200', -200 * 0.3, underwritingInfos[0].fixedCommission, 1E-14
        assertEquals 'underwritingInfo200', -200 * 0.025, underwritingInfos[0].variableCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * (0.4 * 0.25 + 0.3 * 0.75), underwritingInfos[1].commission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.3, underwritingInfos[1].fixedCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.025, underwritingInfos[1].variableCommission, 1E-14

        claims.add(new Claim(value: 15))

        commissionStrategy.calculateCommission claims, underwritingInfos, false, true
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', -200 * (0.4 * 0.25 + 0.3 * 0.75) - 200 * 0.3, underwritingInfos[0].commission, 1E-14
        assertEquals 'underwritingInfo200', -200 * 0.3 - 200 * 0.3, underwritingInfos[0].fixedCommission, 1E-14
        assertEquals 'underwritingInfo200', -200 * 0.025 - 0d, underwritingInfos[0].variableCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * (0.4 * 0.25 + 0.3 * 0.75) - 100 * 0.3, underwritingInfos[1].commission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.3 - 100 * 0.3, underwritingInfos[1].fixedCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.025 - 0d, underwritingInfos[1].variableCommission, 1E-14

        commissionStrategy.calculateCommission claims, underwritingInfos, false, false
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', -200 * 0.3, underwritingInfos[0].commission
        assertEquals 'underwritingInfo200', -200 * 0.3, underwritingInfos[0].fixedCommission
        assertEquals 'underwritingInfo200', -0d, underwritingInfos[0].variableCommission, 1E-8
        assertEquals 'underwritingInfo100', -100 * 0.3, underwritingInfos[1].commission
        assertEquals 'underwritingInfo100', -100 * 0.3, underwritingInfos[1].fixedCommission
        assertEquals 'underwritingInfo100', -0d, underwritingInfos[1].variableCommission, 1E-8

        claims.add(new Claim(value: 0.1))

        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', -200 * 0.3, underwritingInfos[0].commission
        assertEquals 'underwritingInfo200', -200 * 0.3, underwritingInfos[0].fixedCommission
        assertEquals 'underwritingInfo200', -0d, underwritingInfos[0].variableCommission, 1E-8
        assertEquals 'underwritingInfo100', -100 * 0.3, underwritingInfos[1].commission
        assertEquals 'underwritingInfo100', -100 * 0.3, underwritingInfos[1].fixedCommission
        assertEquals 'underwritingInfo100', -0d, underwritingInfos[1].variableCommission, 1E-8
    }

    void testManyRatiosIncludingJumps() {
        ICommissionStrategy commissionStrategy = getInterpolatedSlidingCommissionStrategy([0.2d, 0.2d, 0.3d, 0.3d, 0.3d, 0.4d, 0.5d, 0.5d],
                [0.6d, 0.5d, 0.4d, 0.3d, 0.2d, 0.1d, 0.05d, 0.01d])

        UnderwritingInfo underwritingInfo200 = new CededUnderwritingInfo(premium: 200, commission: -50, fixedCommission: -10, variableCommission: -40)
        UnderwritingInfo underwritingInfo100 = new CededUnderwritingInfo(premium: 100, commission: -5, fixedCommission: -2, variableCommission: -3)

        Claim claim30 = new Claim(value: 30)

        List underwritingInfos = [underwritingInfo200, underwritingInfo100]
        List claims = [claim30]

        commissionStrategy.calculateCommission claims, underwritingInfos, false, true
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', -200 * 0.6 - 50, underwritingInfos[0].commission, 1E-14
        assertEquals 'underwritingInfo200', -200 * 0.01 - 10, underwritingInfos[0].fixedCommission, 1E-14
        assertEquals 'underwritingInfo200', -200 * 0.59 - 40, underwritingInfos[0].variableCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.6 - 5, underwritingInfos[1].commission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.01 - 2, underwritingInfos[1].fixedCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.59 - 3, underwritingInfos[1].variableCommission, 1E-14

        commissionStrategy.calculateCommission claims, underwritingInfos, false, false
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', -200 * 0.6, underwritingInfos[0].commission, 1E-14
        assertEquals 'underwritingInfo200', -200 * 0.01, underwritingInfos[0].fixedCommission, 1E-14
        assertEquals 'underwritingInfo200', -200 * 0.59, underwritingInfos[0].variableCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.6, underwritingInfos[1].commission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.01, underwritingInfos[1].fixedCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.59, underwritingInfos[1].variableCommission, 1E-14

        claims.add(new Claim(value: 30))

        commissionStrategy.calculateCommission claims, underwritingInfos, false, true
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', -200 * 0.5 - 200 * 0.6, underwritingInfos[0].commission, 1E-14
        assertEquals 'underwritingInfo200', -200 * 0.01 - 200 * 0.01, underwritingInfos[0].fixedCommission, 1E-14
        assertEquals 'underwritingInfo200', -200 * 0.49 - 200 * 0.59, underwritingInfos[0].variableCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.5 - 100 * 0.6, underwritingInfos[1].commission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.01 - 100 * 0.01, underwritingInfos[1].fixedCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.49 - 100 * 0.59, underwritingInfos[1].variableCommission, 1E-14

        commissionStrategy.calculateCommission claims, underwritingInfos, false, false
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', -200 * 0.5, underwritingInfos[0].commission, 1E-14
        assertEquals 'underwritingInfo200', -200 * 0.01, underwritingInfos[0].fixedCommission, 1E-14
        assertEquals 'underwritingInfo200', -200 * 0.49, underwritingInfos[0].variableCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.5, underwritingInfos[1].commission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.01, underwritingInfos[1].fixedCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.49, underwritingInfos[1].variableCommission, 1E-14

        claims.add(new Claim(value: 15))

        commissionStrategy.calculateCommission claims, underwritingInfos, false, false
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', -200 * 0.45, underwritingInfos[0].commission, 1E-14
        assertEquals 'underwritingInfo200', -200 * 0.01, underwritingInfos[0].fixedCommission, 1E-14
        assertEquals 'underwritingInfo200', -200 * 0.44, underwritingInfos[0].variableCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.45, underwritingInfos[1].commission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.01, underwritingInfos[1].fixedCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.44, underwritingInfos[1].variableCommission, 1E-14

        claims.add(new Claim(value: 15))

        commissionStrategy.calculateCommission claims, underwritingInfos, false, false
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', -200 * 0.2, underwritingInfos[0].commission
        assertEquals 'underwritingInfo200', -200 * 0.01, underwritingInfos[0].fixedCommission
        assertEquals 'underwritingInfo200', -200 * 0.19, underwritingInfos[0].variableCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.2, underwritingInfos[1].commission
        assertEquals 'underwritingInfo100', -100 * 0.01, underwritingInfos[1].fixedCommission
        assertEquals 'underwritingInfo100', -100 * 0.19, underwritingInfos[1].variableCommission, 1E-14

        claims.add(new Claim(value: 15))

        commissionStrategy.calculateCommission claims, underwritingInfos, false, false
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', -200 * 0.15, underwritingInfos[0].commission, 1E-14
        assertEquals 'underwritingInfo200', -200 * 0.01, underwritingInfos[0].fixedCommission, 1E-14
        assertEquals 'underwritingInfo200', -200 * 0.14, underwritingInfos[0].variableCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.15, underwritingInfos[1].commission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.01, underwritingInfos[1].fixedCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.14, underwritingInfos[1].variableCommission, 1E-14

        claims.add(new Claim(value: 7.5))

        commissionStrategy.calculateCommission claims, underwritingInfos, false, false
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', -200 * (0.75 * 0.1 + 0.25 * 0.2), underwritingInfos[0].commission, 1E-14
        assertEquals 'underwritingInfo200', -200 * 0.01, underwritingInfos[0].fixedCommission, 1E-14
        assertEquals 'underwritingInfo200', -200 * 0.115, underwritingInfos[0].variableCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * (0.75 * 0.1 + 0.25 * 0.2), underwritingInfos[1].commission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.01, underwritingInfos[1].fixedCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.115, underwritingInfos[1].variableCommission, 1E-14

        claims.add(new Claim(value: 7.5))

        commissionStrategy.calculateCommission claims, underwritingInfos, false, false
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', -200 * 0.1, underwritingInfos[0].commission, 1E-14
        assertEquals 'underwritingInfo200', -200 * 0.01, underwritingInfos[0].fixedCommission, 1E-14
        assertEquals 'underwritingInfo200', -200 * 0.09, underwritingInfos[0].variableCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.1, underwritingInfos[1].commission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.01, underwritingInfos[1].fixedCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.09, underwritingInfos[1].variableCommission, 1E-14

        claims.add(new Claim(value: 15))

        commissionStrategy.calculateCommission claims, underwritingInfos, false, false
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', -200 * (0.5 * 0.1 + 0.5 * 0.05), underwritingInfos[0].commission, 1E-14
        assertEquals 'underwritingInfo200', -200 * 0.01, underwritingInfos[0].fixedCommission, 1E-14
        assertEquals 'underwritingInfo200', -200 * 0.065, underwritingInfos[0].variableCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * (0.5 * 0.1 + 0.5 * 0.05), underwritingInfos[1].commission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.01, underwritingInfos[1].fixedCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.065, underwritingInfos[1].variableCommission, 1E-14

        claims.add(new Claim(value: 15))

        commissionStrategy.calculateCommission claims, underwritingInfos, false, false
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', -200 * 0.01, underwritingInfos[0].commission, 1E-14
        assertEquals 'underwritingInfo200', -200 * 0.01, underwritingInfos[0].fixedCommission, 1E-14
        assertEquals 'underwritingInfo200', -0d, underwritingInfos[0].variableCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.01, underwritingInfos[1].commission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.01, underwritingInfos[1].fixedCommission, 1E-14
        assertEquals 'underwritingInfo100', -0d, underwritingInfos[1].variableCommission, 1E-14

        claims.add(new Claim(value: 50))

        commissionStrategy.calculateCommission claims, underwritingInfos, false, false
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', -200 * 0.01, underwritingInfos[0].commission, 1E-14
        assertEquals 'underwritingInfo200', -200 * 0.01, underwritingInfos[0].fixedCommission, 1E-14
        assertEquals 'underwritingInfo200', -0d, underwritingInfos[0].variableCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.01, underwritingInfos[1].commission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.01, underwritingInfos[1].fixedCommission, 1E-14
        assertEquals 'underwritingInfo100', -0d, underwritingInfos[1].variableCommission, 1E-14
    }

    void testStrategyHasStepFunctionsOnly() {

        ICommissionStrategy commissionStrategy = getInterpolatedSlidingCommissionStrategy([0.1d, 0.1d, 0.1d, 0.2d, 0.2d], [0.6d, 0.5d, 0.4d, 0.4d, 0.3d])

        UnderwritingInfo underwritingInfo200 = new CededUnderwritingInfo(premium: 200, commission: -50, fixedCommission: -20, variableCommission: -30)
        UnderwritingInfo underwritingInfo100 = new CededUnderwritingInfo(premium: 100, commission: -5, fixedCommission: -1, variableCommission: -4)

        Claim claim15 = new Claim(value: 15)

        List underwritingInfos = [underwritingInfo200, underwritingInfo100]
        List claims = [claim15]

        commissionStrategy.calculateCommission claims, underwritingInfos, false, true
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', -200 * 0.6 - 50, underwritingInfos[0].commission
        assertEquals 'underwritingInfo200', -200 * 0.3 - 20, underwritingInfos[0].fixedCommission, 1E-14
        assertEquals 'underwritingInfo200', -200 * 0.3 - 30, underwritingInfos[0].variableCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.6 - 5, underwritingInfos[1].commission
        assertEquals 'underwritingInfo100', -100 * 0.3 - 1, underwritingInfos[1].fixedCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.3 - 4, underwritingInfos[1].variableCommission, 1E-14

        commissionStrategy.calculateCommission claims, underwritingInfos, false, false
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', -200 * 0.6, underwritingInfos[0].commission
        assertEquals 'underwritingInfo200', -200 * 0.3, underwritingInfos[0].fixedCommission, 1E-14
        assertEquals 'underwritingInfo200', -200 * 0.3, underwritingInfos[0].variableCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.6, underwritingInfos[1].commission
        assertEquals 'underwritingInfo100', -100 * 0.3, underwritingInfos[1].fixedCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.3, underwritingInfos[1].variableCommission, 1E-14

        claims.add(new Claim(value: 15))

        commissionStrategy.calculateCommission claims, underwritingInfos, false, false
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', -200 * 0.4, underwritingInfos[0].commission
        assertEquals 'underwritingInfo200', -200 * 0.3, underwritingInfos[0].fixedCommission, 1E-14
        assertEquals 'underwritingInfo200', -200 * 0.1, underwritingInfos[0].variableCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.4, underwritingInfos[1].commission
        assertEquals 'underwritingInfo100', -100 * 0.3, underwritingInfos[1].fixedCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.1, underwritingInfos[1].variableCommission, 1E-14

        claims.add(new Claim(value: 25))

        commissionStrategy.calculateCommission claims, underwritingInfos, false, false
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', -200 * 0.4, underwritingInfos[0].commission
        assertEquals 'underwritingInfo200', -200 * 0.3, underwritingInfos[0].fixedCommission, 1E-14
        assertEquals 'underwritingInfo200', -200 * 0.1, underwritingInfos[0].variableCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.4, underwritingInfos[1].commission
        assertEquals 'underwritingInfo100', -100 * 0.3, underwritingInfos[1].fixedCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.1, underwritingInfos[1].variableCommission, 1E-14

        claims.add(new Claim(value: 5))

        commissionStrategy.calculateCommission claims, underwritingInfos, false, false
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', -200 * 0.3, underwritingInfos[0].commission
        assertEquals 'underwritingInfo200', -200 * 0.3, underwritingInfos[0].fixedCommission, 1E-14
        assertEquals 'underwritingInfo200', -0d, underwritingInfos[0].variableCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.3, underwritingInfos[1].commission
        assertEquals 'underwritingInfo100', -100 * 0.3, underwritingInfos[1].fixedCommission, 1E-14
        assertEquals 'underwritingInfo100', -0d, underwritingInfos[1].variableCommission, 1E-14

        claims.add(new Claim(value: 30))

        commissionStrategy.calculateCommission claims, underwritingInfos, false, false
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', -200 * 0.3, underwritingInfos[0].commission
        assertEquals 'underwritingInfo200', -200 * 0.3, underwritingInfos[0].fixedCommission, 1E-14
        assertEquals 'underwritingInfo200', -0d, underwritingInfos[0].variableCommission, 1E-14
        assertEquals 'underwritingInfo100', -100 * 0.3, underwritingInfos[1].commission
        assertEquals 'underwritingInfo100', -100 * 0.3, underwritingInfos[1].fixedCommission, 1E-14
        assertEquals 'underwritingInfo100', -0d, underwritingInfos[1].variableCommission, 1E-14
    }
}
