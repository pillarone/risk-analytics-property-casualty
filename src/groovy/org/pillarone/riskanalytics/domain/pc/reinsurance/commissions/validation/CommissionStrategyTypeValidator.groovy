package org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.validation;

import org.pillarone.riskanalytics.core.parameterization.validation.IParameterizationValidator
import org.apache.commons.logging.LogFactory
import org.apache.commons.logging.Log
import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationServiceImpl
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidationError
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.SlidingCommissionStrategy;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class CommissionStrategyTypeValidator implements IParameterizationValidator {

    private static Log LOG = LogFactory.getLog(CommissionStrategyTypeValidator)
    private static final double EPSILON = 1E-6 // guard for "close-enough" checks instead of == for doubles

    private AbstractParameterValidationService validationService

    public CommissionStrategyTypeValidator() {
        validationService = new ParameterValidationServiceImpl()
        registerConstraints()
    }

    List<ParameterValidationError> validate(List<ParameterHolder> parameters) {

        List<ParameterValidationError> errors = []

        for (ParameterHolder parameter in parameters) {
            if (parameter instanceof ParameterObjectParameterHolder) {
                IParameterObjectClassifier classifier = parameter.getClassifier()
                if (classifier instanceof CommissionStrategyType) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug "validating ${parameter.path}"
                    }
                    def currentErrors = validationService.validate(classifier, parameter.getParameterMap())
                    currentErrors*.path = parameter.path
                    errors.addAll(currentErrors)
                }
                errors.addAll(validate(parameter.classifierParameters.values().toList()))
            }
        }
        return errors
    }

    private void registerConstraints() {
        validationService.register(CommissionStrategyType.SLIDINGCOMMISSION) {Map type ->
            double firstLossRatio = type.commissionBands.getValueAt(1, 1)
            if (firstLossRatio == 0) return true
            ["commission.sliding.error.first.supporting.point.not.zero", firstLossRatio]
        }
        validationService.register(CommissionStrategyType.SLIDINGCOMMISSION) {Map type ->
            double[] lossRatios = type.commissionBands.getColumnByName(SlidingCommissionStrategy.LOSS_RATIO)
            if (!lossRatios) {
                return ["commission.sliding.error.loss.ratios.empty"]
            }
            for (int i = 1; i < lossRatios.length; i++) {
                if (lossRatios[i - 1] >= lossRatios[i]) {
                    return ["commission.sliding.error.loss.ratios.not.strictly.increasing", i, lossRatios[i - 1], lossRatios[i]]
                }
            }
            return true
        }

        validationService.register(CommissionStrategyType.SLIDINGCOMMISSION) {Map type ->
            double[] commissions = type.commissionBands.getColumnByName(SlidingCommissionStrategy.COMMISSION)
            if (!commissions) {
                return ["commission.sliding.error.commissions.empty"]
            }
            for (int i = 0; i < commissions.length; i++) {
                if (commissions[i] < 0) {
                    return ["commission.sliding.error.commissions.negative", i, commissions[i]]
                }
            }
            return true
        }

        validationService.register(CommissionStrategyType.SLIDINGCOMMISSION) {Map type ->
            double[] commissions = type.commissionBands.getColumnByName(SlidingCommissionStrategy.COMMISSION)
            if (!commissions) {
                return ["commission.sliding.error.commissions.empty"]
            }
            for (int i = 1; i < commissions.length; i++) {
                if (commissions[i] > commissions[i - 1]) {
                    return ["commission.sliding.error.commissions.not.decreasing", i, commissions[i - 1], commissions[i]]
                }
            }
            return true
        }


    }
}
