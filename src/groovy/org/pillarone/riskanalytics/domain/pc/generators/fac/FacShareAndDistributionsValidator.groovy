package org.pillarone.riskanalytics.domain.pc.generators.fac


import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.apache.commons.logging.LogFactory
import org.apache.commons.logging.Log
import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationServiceImpl
import org.pillarone.riskanalytics.core.parameterization.validation.ValidationType
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidation
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.MultiDimensionalParameterHolder
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.underwriting.RiskBands
import org.pillarone.riskanalytics.core.parameterization.validation.IParameterizationValidator
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.IReinsuranceContractStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.SurplusContractStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReverseSurplusContractStrategy
import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationImpl
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder
import org.pillarone.riskanalytics.core.parameterization.validation.ValidationType
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.pc.validation.ValidatorUtils

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class FacShareAndDistributionsValidator implements IParameterizationValidator {

    private static Log LOG = LogFactory.getLog(FacShareAndDistributionsValidator)

    private AbstractParameterValidationService validationService

    public FacShareAndDistributionsValidator() {
        validationService = new ParameterValidationServiceImpl()
        registerConstraints()
    }

    List<ParameterValidation> validate(List<ParameterHolder> parameters) {

        List<ParameterValidation> errors = []

        /** key: path  */
        Map<String, TableMultiDimensionalParameter> treatyAllocationForFac = ValidatorUtils.getTreatyAllocationFac(parameters)
        /** key: path  */
        Map<String, TableMultiDimensionalParameter> underwritingInfos = ValidatorUtils.getUnderwritingInfos(parameters)
        /** key: max sum insured, value: path underwriting infos */
        Map<Double, String> maxSumInsuredAndPaths = [:]
        for (Map.Entry<String, TableMultiDimensionalParameter> uwTable : underwritingInfos) {
            List<Double> maxSumInsurreds = uwTable.value.getColumnByName(RiskBands.MAXIMUM_SUM_INSURED);
//            maxSumInsuredAndPaths.put()
        }

//        for(Map.Entry<String, TableMultiDimensionalParameter> entry: underwritingInfos) {
//            def currentErrors = validationService.validate(entry.value, entry.value.getColumnByName(RiskBands.MAXIMUM_SUM_INSURED))
//            currentErrors*.path = entry.key
//            errors.addAll(currentErrors)
//        }
//
//        if (!contracts.isEmpty()) {
//            for (Map.Entry<String, TableMultiDimensionalParameter> item: underwritingInfos.entrySet()) {
//                String path = item.key
//                List<Double> numberOfPolicies = item.value.getColumnByName(RiskBands.NUMBER_OF_POLICIES)
//                for (int row = 0; row < numberOfPolicies.size(); row++) {
//                    if (numberOfPolicies[row] > 0) continue
//                    ParameterValidationImpl error = new ParameterValidationImpl(ValidationType.WARNING,
//                        'surplus.ri.needs.non.trivial.number.of.policies', [numberOfPolicies[row], row+1]
//                    )
//                    error.path = path
//                    errors << error
//                }
//            }
//        }
        return errors
    }

//    public static Map<String, TableMultiDimensionalParameter> getUnderwritingInfos(List<ParameterHolder> parameters) {
//        Map<String, TableMultiDimensionalParameter> underwritingInfos = [:]
//        for (ParameterHolder parameter in parameters) {
//            if (parameter instanceof MultiDimensionalParameterHolder) {
//                AbstractMultiDimensionalParameter value = parameter.getBusinessObject()  // parameter.value ??
//                if (value instanceof TableMultiDimensionalParameter) {
//                    List<String> titles = value.getColumnNames()  // value.titles ?
//                    if (titles.contains(RiskBands.MAXIMUM_SUM_INSURED)) {
//                        if (LOG.isDebugEnabled()) {
//                            LOG.debug "validating ${parameter.path}"
//                        }
//                        underwritingInfos[parameter.path] = value
//                    }
//                }
//            }
//        }
//        underwritingInfos
//    }
//
//    public static Map<String, IReinsuranceContractStrategy> getReinsuranceContracts(List<ParameterHolder> parameters,
//        List<Class<? extends IReinsuranceContractStrategy>> contractTypes) {
//        Map<String, IReinsuranceContractStrategy> contracts = [:]
//        for (ParameterHolder parameter in parameters) {
//            if (parameter instanceof ParameterObjectParameterHolder) {
//                try {
//                    IParameterObject parameterHolder = parameter.getBusinessObject()
//                    for (Class allowedStrategy : contractTypes) {
//                        if (parameterHolder.class.equals(allowedStrategy.class)) {
//                            contracts[parameter.path] = parameterHolder
//                        }
//                    }
//                }
//                catch (IllegalArgumentException ex) {
//                    // https://issuetracking.intuitive-collaboration.com/jira/browse/PMO-1542
//                    LOG.debug("call parameter.getBusinessObject() failed " + ex.toString())
//                }
//            }
//        }
//        contracts
//    }

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