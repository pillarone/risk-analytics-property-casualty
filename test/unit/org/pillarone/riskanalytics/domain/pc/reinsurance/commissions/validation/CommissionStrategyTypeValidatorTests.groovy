package org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.validation

import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.utils.constraints.DoubleConstraints
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.SlidingCommissionStrategy

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class CommissionStrategyTypeValidatorTests extends GroovyTestCase {

    AbstractParameterValidationService validator = new CommissionStrategyTypeValidator().validationService

    void testDefaultSlidingCommissionValidator() {
        def defaultSlidingCommission = CommissionStrategyType.SLIDINGCOMMISSION
        assertEquals 0, validator.validate(defaultSlidingCommission,
                ['commissionBands': new ConstrainedMultiDimensionalParameter(
                        [[0d], [0d]],
                        [SlidingCommissionStrategy.LOSS_RATIO, SlidingCommissionStrategy.COMMISSION],
                        ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))]).size()
    }

    void testSlidingCommissionValidatorWrongRoot() {
        def defaultSlidingCommission = CommissionStrategyType.SLIDINGCOMMISSION
        def errors = validator.validate(defaultSlidingCommission,
                ['commissionBands': new ConstrainedMultiDimensionalParameter(
                        [[1d], [0d]],
                        [SlidingCommissionStrategy.LOSS_RATIO, SlidingCommissionStrategy.COMMISSION],
                        ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))])
        assertNotNull errors
        assertEquals 'one error message', 1, errors.size()
        assertEquals 'first loss ratio not null', 'commission.sliding.error.sliding.first.supporting.point.not.zero', errors[0].msg
    }

    void testSlidingCommissionValidatorAllCriteriasFail() {
        def defaultSlidingCommission = CommissionStrategyType.SLIDINGCOMMISSION
        def errors = validator.validate(defaultSlidingCommission,
                ['commissionBands': new ConstrainedMultiDimensionalParameter(
                        [[1d, 0d, 2d], [0d, 2d, 1d]],
                        [SlidingCommissionStrategy.LOSS_RATIO, SlidingCommissionStrategy.COMMISSION],
                        ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))])
        assertNotNull errors
        assertEquals 'one error message', 3, errors.size()
        assertEquals 'first loss ratio not null', 'commission.sliding.error.sliding.first.supporting.point.not.zero', errors[0].msg
        assertEquals 'loss ratio not increasing', 'commission.sliding.error.loss.ratios.not.increasing', errors[1].msg
        assertEquals 'commissions not decreasing', 'commission.sliding.error.commissions.not.decreasing', errors[2].msg
    }

}