package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.validation

import org.pillarone.riskanalytics.core.parameterization.validation.IParameterizationValidator
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationServiceImpl
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidationError
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.CXLContractStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class XLStrategyValidator implements IParameterizationValidator {

    private static Log LOG = LogFactory.getLog(XLStrategyValidator)

    private AbstractParameterValidationService validationService

    public XLStrategyValidator() {
        validationService = new ParameterValidationServiceImpl()
        registerConstraints()
    }

    List<ParameterValidationError> validate(List<ParameterHolder> parameters) {

        List<ParameterValidationError> errors = []

        for (ParameterHolder parameter in parameters) {
            if (parameter instanceof ParameterObjectParameterHolder) {
                IParameterObjectClassifier classifier = parameter.getClassifier()
                if (classifier instanceof ReinsuranceContractType) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug "validating ${parameter.path}"
                    }
                    try {
                        def currentErrors = validationService.validate(classifier, parameter.getParameterMap())
                        currentErrors*.path = parameter.path
                        errors.addAll(currentErrors)
                    }
                    catch (IllegalArgumentException ex) {
                    }
                }
            }
        }
        return errors
    }

    private void registerConstraints() {
        validationService.register(ReinsuranceContractType.CXL) {Map type ->
            return correctAggregateLimit(type, 'limit', 'aggregateLimit')
        }
        validationService.register(ReinsuranceContractType.CXL) {Map type ->
            return correctReinstatementPremium(type)
        }
        validationService.register(ReinsuranceContractType.WXL) {Map type ->
            return correctAggregateLimit(type, 'limit', 'aggregateLimit')
        }
        validationService.register(ReinsuranceContractType.WXL) {Map type ->
            return correctReinstatementPremium(type)
        }
        validationService.register(ReinsuranceContractType.WCXL) {Map type ->
            return correctAggregateLimit(type, 'limit', 'aggregateLimit')
        }
        validationService.register(ReinsuranceContractType.WCXL) {Map type ->
            return correctReinstatementPremium(type)
        }
        validationService.register(ReinsuranceContractType.GOLDORAK) {Map type ->
            return correctAggregateLimit(type, 'cxlLimit', 'cxlAggregateLimit')
        }
    }

    private def correctAggregateLimit(Map type, String limit, String aggregateLimit) {
        if (type.size() == 0 || type[limit] == null || type[aggregateLimit] == null) return
        if (type[limit] > type[aggregateLimit]) {
            return ["aggregateLimit.lower.than.limit"]
        }
        return
    }

    private def correctReinstatementPremium(Map type) {
        if (type.size() == 0 || type['reinstatementPremiums'] == null
                || type['aggregateLimit'] == null || type['limit'] == null) return
        int valueRows = (((TableMultiDimensionalParameter) type['reinstatementPremiums']).rowCount
                - ((TableMultiDimensionalParameter) type['reinstatementPremiums']).titleRowCount)
        if (valueRows == 1 && ((TableMultiDimensionalParameter) type['reinstatementPremiums']).getValueAt(1, 0) == 0) return
        double numberOfReinstatements = type['aggregateLimit'] / type['limit'] - 1  // -1 as the aggregate limit contains the base layer
        if (valueRows < numberOfReinstatements || valueRows > Math.ceil(numberOfReinstatements)) {
            return ["mismatching.reinstatement.premiums.and.aggregate.limit"]
        }
        return
    }
}

