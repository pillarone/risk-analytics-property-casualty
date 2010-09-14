package org.pillarone.riskanalytics.domain.pc.reinsurance.commissions

import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.utils.constraints.DoubleConstraints
import org.pillarone.riskanalytics.domain.pc.claims.Claim

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class SlidingCommissionStrategyTests extends GroovyTestCase {

    private ICommissionStrategy commissionStrategy
    private List<UnderwritingInfo> uwInfo
    private List<Claim> claims

    static ICommissionStrategy getSlidingCommissionStrategy(Map<Double, Double> bands = [:]) {
        // convert/split bands, a map with entries [LossRatioLowerLimit: CommissionRate], to two arrays:
        List<Double> LossRatios
        List<Double> Commissions
        if (bands.size() > 0) {
            LossRatios = bands.keySet().asList().sort()
            Commissions = LossRatios.collect {bands.get(it)}
        } else {
            // default commission bands if none specified
            LossRatios = [0.0d, 0.1d, 0.2d, 0.5d]
            Commissions = [0.2d, 0.10d, 0.05d, 0d]
        }
        ICommissionStrategy commissionStrategy = CommissionStrategyType.getStrategy(
                CommissionStrategyType.SLIDINGCOMMISSION,
                ['commissionBands': new ConstrainedMultiDimensionalParameter(
                        [LossRatios, Commissions],
                        [SlidingCommissionStrategy.LOSS_RATIO, SlidingCommissionStrategy.COMMISSION],
                        ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))])
        return commissionStrategy
    }

    void setUp() {
        // default values for test cases below (e.g. for those that don't use the above method's default bands)
        commissionStrategy = getSlidingCommissionStrategy([
                0.1d: 0.07d, // 7% on 10-40%, i.e., Commission is 7% if LossRatio is in [0.1, 0.4)
                0.4d: 0.05d, // 5% on 40-50%, i.e., Commission is 5% if LossRatio is in [0.4, 0.5)
                0.5d: 0.03d, // 3% on 50-60%, i.e., Commission is 3% if LossRatio is in [0.5, 0.6)
                0.6d: 0d,    // 0% from 60%, i.e., Commission is 0% if LossRatio is in [0.6, +Infinity)
        ])
        uwInfo = [new UnderwritingInfo(premiumWritten: 100)]
        claims = [new Claim(value: 0)]
    }

    /** this is a simple example testing that the sliding commission strategy:
     * (1) calculates the correct loss ratio;
     * (2) selects the correct band;
     * (3) uses the corresponding commission from that band; and
     * (4) distributes this commission proportionally to the premium written of each UnderwritingInfo packet,
     * (5) without the "additive" option.
     */
    void testUsage() {
        ICommissionStrategy commissionStrategy = getSlidingCommissionStrategy() // uses method's default bands
        // default commission bands: 20% Commission on [0,10%) LossRatio, 10% on [10%,20%), 5% on [20%,50%), 0% at or over 50% loss ratio

        UnderwritingInfo underwritingInfo200 = new UnderwritingInfo(premiumWritten: 200, commission: -50)
        UnderwritingInfo underwritingInfo100 = new UnderwritingInfo(premiumWritten: 100, commission: -5)
        // so total premium written is 300

        Claim claim05 = new Claim(value: 5)
        Claim claim15 = new Claim(value: 15)
        // so total loss is 20

        List underwritingInfos = [underwritingInfo200, underwritingInfo100]
        List claims = [claim05, claim15]
        // so loss ratio is 20/300 < 10%, and therefore the commission should be 20%

        commissionStrategy.calculateCommission claims, underwritingInfos, false, false

        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', -200 * 0.2, underwritingInfos[0].commission
        assertEquals 'underwritingInfo100', -100 * 0.2, underwritingInfos[1].commission
    }

    void testAdditiveUsage() {
        ICommissionStrategy commissionStrategy = getSlidingCommissionStrategy()
        // default commission bands: 20% Commission on [0,10%) LossRatio, 10% on [10%,20%), 5% on [20%,50%), 0% at or over 50% loss ratio

        UnderwritingInfo underwritingInfo200 = new UnderwritingInfo(premiumWritten: 200, commission: -50)
        List underwritingInfo = [underwritingInfo200]
        List claims = [new Claim(value: 25)]
        // loss ratio is 25/200 = 12.5%, in [10%, 20%], so the commission should be 10%

        commissionStrategy.calculateCommission claims, underwritingInfo, false, true
        // Nota bene: this is the "additive" usage!

        assertEquals '# outUnderwritingInfo packets', 1, underwritingInfo.size()
        assertEquals 'underwritingInfo200', -50 - 200 * 0.1, underwritingInfo[0].commission
    }

    /**
     * Test cases testPercentageSelectionCaseX (for X in {0a,0b,0c,1,2,3a,3b,4})
     * correspond to the spreadsheet found in Jira here:
     *
     */
    void testPercentageSelectionCase0a() {
        claims[0].value = -1e-6
        commissionStrategy.calculateCommission claims, uwInfo, false, false
        double lossRatio = claims.value.sum() / uwInfo.premiumWritten.sum() * 1E2
        assertEquals '# outUnderwritingInfo packets', 1, uwInfo.size()
        assertEquals "Underwriting Commission (%) resulting from Loss Ratio of ${lossRatio}%", -0d, uwInfo[0].commission
    }

    void testPercentageSelectionCase0b() {
        claims[0].value = 0d
        commissionStrategy.calculateCommission claims, uwInfo, false, false
        double lossRatio = claims.value.sum() / uwInfo.premiumWritten.sum() * 1E2
        assertEquals '# outUnderwritingInfo packets', 1, uwInfo.size()
        assertEquals "Underwriting Commission (%) resulting from Loss Ratio of ${lossRatio}%", 0d, uwInfo[0].commission, 1E-14
    }

    void testPercentageSelectionCase0c() {
        claims[0].value = 10d - 1e-6
        commissionStrategy.calculateCommission claims, uwInfo, false, false
        double lossRatio = claims.value.sum() / uwInfo.premiumWritten.sum() * 1E2
        assertEquals '# outUnderwritingInfo packets', 1, uwInfo.size()
        assertEquals "Underwriting Commission (%) resulting from Loss Ratio of ${lossRatio}%", 0d, uwInfo[0].commission, 1E-14
    }

    void testPercentageSelectionCase1() {
        claims[0].value = 10d
        commissionStrategy.calculateCommission claims, uwInfo, false, false
        double lossRatio = claims.value.sum() / uwInfo.premiumWritten.sum() * 1E2
        assertEquals '# outUnderwritingInfo packets', 1, uwInfo.size()
        assertEquals "Underwriting Commission (%) resulting from Loss Ratio of ${lossRatio}%", -7d, uwInfo[0].commission, 1E-14
    }

    void testPercentageSelectionCase2() {
        claims[0].value = 40d
        commissionStrategy.calculateCommission claims, uwInfo, false, false
        double lossRatio = claims.value.sum() / uwInfo.premiumWritten.sum() * 1E2
        assertEquals '# outUnderwritingInfo packets', 1, uwInfo.size()
        assertEquals "Underwriting Commission (%) resulting from Loss Ratio of ${lossRatio}%", -5d, uwInfo[0].commission
    }

    void testPercentageSelectionCase3a() {
        claims[0].value = 50d
        commissionStrategy.calculateCommission claims, uwInfo, false, false
        double lossRatio = claims.value.sum() / uwInfo.premiumWritten.sum() * 1E2
        assertEquals '# outUnderwritingInfo packets', 1, uwInfo.size()
        assertEquals "Underwriting Commission (%) resulting from Loss Ratio of ${lossRatio}%", -3d, uwInfo[0].commission
    }

    void testPercentageSelectionCase3b() {
        claims[0].value = 60d - 1e-6
        commissionStrategy.calculateCommission claims, uwInfo, false, false
        double lossRatio = claims.value.sum() / uwInfo.premiumWritten.sum() * 1E2
        assertEquals '# outUnderwritingInfo packets', 1, uwInfo.size()
        assertEquals "Underwriting Commission (%) resulting from Loss Ratio of ${lossRatio}%", -3d, uwInfo[0].commission
    }

    void testPercentageSelectionCase4() {
        claims[0].value = 60d
        commissionStrategy.calculateCommission claims, uwInfo, false, false
        double lossRatio = claims.value.sum() / uwInfo.premiumWritten.sum() * 1E2
        assertEquals '# outUnderwritingInfo packets', 1, uwInfo.size()
        assertEquals "Underwriting Commission (%) resulting from Loss Ratio of ${lossRatio}%", 0, uwInfo[0].commission, 1E-14
    }
}