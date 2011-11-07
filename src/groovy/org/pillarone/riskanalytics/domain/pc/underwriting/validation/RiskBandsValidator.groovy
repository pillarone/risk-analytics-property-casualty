package org.pillarone.riskanalytics.domain.pc.underwriting.validation

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.pillarone.riskanalytics.core.parameterization.validation.IParameterizationValidator
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidation
import org.pillarone.riskanalytics.core.parameterization.validation.ValidationType
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.IReinsuranceContractStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReverseSurplusContractStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.SurplusContractStrategy
import org.pillarone.riskanalytics.domain.pc.underwriting.RiskBands
import org.pillarone.riskanalytics.domain.pc.validation.ValidatorUtils
import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationImpl
import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationServiceImpl

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class RiskBandsValidator implements IParameterizationValidator {

    private static Log LOG = LogFactory.getLog(RiskBandsValidator)

    private AbstractParameterValidationService validationService

    public RiskBandsValidator() {
        validationService = new ParameterValidationServiceImpl()
        registerConstraints()
    }

    List<ParameterValidation> validate(List<ParameterHolder> parameters) {

        List<ParameterValidation> errors = []

        /** key: path  */
        Map<String, IReinsuranceContractStrategy> contracts = ValidatorUtils.getReinsuranceContracts(
                parameters, [SurplusContractStrategy, ReverseSurplusContractStrategy])
        /** key: path  */
        Map<String, TableMultiDimensionalParameter> underwritingInfos = ValidatorUtils.getUnderwritingInfos(parameters)

        for(Map.Entry<String, TableMultiDimensionalParameter> entry: underwritingInfos) {
            def currentErrors = validationService.validate(entry.value, entry.value.getColumnByName(RiskBands.MAXIMUM_SUM_INSURED))
            currentErrors*.path = entry.key
            errors.addAll(currentErrors)
        }

        if (!contracts.isEmpty()) {
            for (Map.Entry<String, TableMultiDimensionalParameter> item: underwritingInfos.entrySet()) {
                String path = item.key
                List<Double> numberOfPolicies = item.value.getColumnByName(RiskBands.NUMBER_OF_POLICIES)
                for (int row = 0; row < numberOfPolicies.size(); row++) {
                    if (numberOfPolicies[row] > 0) continue
                    ParameterValidationImpl error = new ParameterValidationImpl(ValidationType.WARNING,
                        'surplus.ri.needs.non.trivial.number.of.policies', [numberOfPolicies[row], row+1]
                    )
                    error.path = path
                    errors << error
                }
            }
        }
        return errors
    }

    private void registerConstraints() {

        validationService.register(TableMultiDimensionalParameter) {List type ->
            Set<Double> set = new HashSet(type)
            if (set.size() != type.size()) {
                return [ValidationType.WARNING, "underwriting.info.value.of.max.sum.insured.not.unique"]
            }
            return true
        }

        validationService.register(TableMultiDimensionalParameter) {List type ->
            if (type.any {it < 0}) {
                return [ValidationType.ERROR, "underwriting.info.value.of.max.sum.insured.negative"]
            }
            return true
        }
    }
}