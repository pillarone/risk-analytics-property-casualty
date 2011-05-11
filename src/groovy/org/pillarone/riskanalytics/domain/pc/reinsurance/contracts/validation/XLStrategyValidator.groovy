package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.validation

import org.pillarone.riskanalytics.core.parameterization.validation.IParameterizationValidator
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationServiceImpl
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidation
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.validation.ValidationType

/**
 * This validator checks consistency of limit, aggregate limit and reinstatement premium parameters.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class XLStrategyValidator implements IParameterizationValidator {

    private static Log LOG = LogFactory.getLog(XLStrategyValidator)

    private AbstractParameterValidationService validationService

    public XLStrategyValidator() {
        validationService = new ParameterValidationServiceImpl()
        registerConstraints()
    }

    List<ParameterValidation> validate(List<ParameterHolder> parameters) {

        List<ParameterValidation> errors = []

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
            return [ValidationType.ERROR, "aggregateLimit.lower.than.limit", type[aggregateLimit], type[limit]]
        }
        return
    }

    private def correctReinstatementPremium(Map type) {
        if (type.size() == 0 || type['reinstatementPremiums'] == null) return
        Double aggregateLimitParameter = (Double) type['aggregateLimit']
        Double limit = (Double) type['limit']
        if (aggregateLimitParameter == null || limit == null) return
        TableMultiDimensionalParameter reinstatementPremiums = (TableMultiDimensionalParameter) type['reinstatementPremiums']
        int numberOfReinstatementsBasedOnSpecifiedRIPremium = reinstatementPremiums.valueRowCount
        boolean freeReinstatements = numberOfReinstatementsBasedOnSpecifiedRIPremium == 1 && reinstatementPremiums.getValueAt(1, 0) == 0
        if (freeReinstatements) return
        double numberOfReinstatements = aggregateLimitParameter / limit - 1  // -1 as the aggregate limit contains the base layer
        double aggregateLimitCalculated = (numberOfReinstatementsBasedOnSpecifiedRIPremium + 1) * limit
//        int usableReinstatements = (aggregateLimitParameter - limit) / limit
        if (numberOfReinstatementsBasedOnSpecifiedRIPremium < numberOfReinstatements) {
            return [ValidationType.ERROR, "mismatching.reinstatement.premiums.and.aggregate.limit.beyond", aggregateLimitParameter, aggregateLimitCalculated]
        }
        else if (numberOfReinstatementsBasedOnSpecifiedRIPremium > Math.ceil(numberOfReinstatements)) {
            return [ValidationType.ERROR, "mismatching.reinstatement.premiums.and.aggregate.limit.below",
                    numberOfReinstatements, numberOfReinstatementsBasedOnSpecifiedRIPremium,
                    aggregateLimitParameter, aggregateLimitCalculated]
        }
        return
    }
}

