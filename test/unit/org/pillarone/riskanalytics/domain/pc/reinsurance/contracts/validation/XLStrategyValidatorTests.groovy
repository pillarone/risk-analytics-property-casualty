package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.validation

import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.IReinsuranceContractStrategy
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class XLStrategyValidatorTests extends GroovyTestCase {

    AbstractParameterValidationService validator = new XLStrategyValidator().validationService

    static IReinsuranceContractStrategy getContractStrategy(ReinsuranceContractType type, double limit, double aggregateLimit) {
        getContractStrategy(type, limit, aggregateLimit, new TableMultiDimensionalParameter([0d], ['Reinstatement Premium']))
    }

    static IReinsuranceContractStrategy getContractStrategy(ReinsuranceContractType type,
                                               double limit, double aggregateLimit,
                                               AbstractMultiDimensionalParameter reinstatementPremiums) {
        ReinsuranceContractType.getStrategy(
            type,
                [attachmentPoint: 0, limit: limit, aggregateLimit: aggregateLimit, aggregateDeductible: 0,
                 premiumBase: PremiumBase.ABSOLUTE, premium: 0, reinstatementPremiums: reinstatementPremiums, coveredByReinsurer: 1])
    }

    static IReinsuranceContractStrategy getGoldorakContractStrategy(double limit, double aggregateLimit) {
        ReinsuranceContractType.getStrategy(
            ReinsuranceContractType.GOLDORAK,
                [cxlAttachmentPoint: 0, cxlLimit: limit, cxlAggregateLimit: aggregateLimit, cxlAggregateDeductible: 0,
                 premiumBase: PremiumBase.ABSOLUTE, premium: 0, coveredByReinsurer: 1,
                 reinstatementPremiums: new TableMultiDimensionalParameter([0d], ['Reinstatement Premium']),
                 slAttachmentPoint: 0, slLimit: 0, goldorakSlThreshold: 0])
    }

    void testCXL() {
        IReinsuranceContractStrategy contract = getContractStrategy(ReinsuranceContractType.CXL, 0, 0)
        def errors = validator.validate(contract.getType(), contract.getParameters())
        assertEquals 0, errors.size()

        contract = getContractStrategy(ReinsuranceContractType.CXL, 1000, 2000, new TableMultiDimensionalParameter([1d], ['Reinstatement Premium']))
        errors = validator.validate(contract.getType(), contract.getParameters())
        assertEquals 0, errors.size()

        contract = getContractStrategy(ReinsuranceContractType.CXL, 1000, 0, new TableMultiDimensionalParameter([], ['Reinstatement Premium']))
        errors = validator.validate(contract.getType(), contract.getParameters())
        assertEquals 1, errors.size()

        contract = getContractStrategy(ReinsuranceContractType.CXL, 1000, 0)
        errors = validator.validate(contract.getType(), contract.getParameters())
        assertEquals 2, errors.size()
    }

    void testWXL() {
        IReinsuranceContractStrategy contract = getContractStrategy(ReinsuranceContractType.WXL, 0, 0)
        def errors = validator.validate(contract.getType(), contract.getParameters())
        assertEquals 0, errors.size()

        contract = getContractStrategy(ReinsuranceContractType.WXL, 1000, 2000, new TableMultiDimensionalParameter([1d], ['Reinstatement Premium']))
        errors = validator.validate(contract.getType(), contract.getParameters())
        assertEquals 0, errors.size()

        contract = getContractStrategy(ReinsuranceContractType.WXL, 1000, 0, new TableMultiDimensionalParameter([], ['Reinstatement Premium']))
        errors = validator.validate(contract.getType(), contract.getParameters())
        assertEquals 1, errors.size()

        contract = getContractStrategy(ReinsuranceContractType.WXL, 1000, 0)
        errors = validator.validate(contract.getType(), contract.getParameters())
        assertEquals 2, errors.size()
    }

    void testWCXL() {
        IReinsuranceContractStrategy contract = getContractStrategy(ReinsuranceContractType.WCXL, 0, 0)
        def errors = validator.validate(contract.getType(), contract.getParameters())
        assertEquals 0, errors.size()

        contract = getContractStrategy(ReinsuranceContractType.WCXL, 1000, 2000, new TableMultiDimensionalParameter([1d], ['Reinstatement Premium']))
        errors = validator.validate(contract.getType(), contract.getParameters())
        assertEquals 0, errors.size()

        contract = getContractStrategy(ReinsuranceContractType.WCXL, 1000, 0, new TableMultiDimensionalParameter([], ['Reinstatement Premium']))
        errors = validator.validate(contract.getType(), contract.getParameters())
        assertEquals 1, errors.size()

        contract = getContractStrategy(ReinsuranceContractType.WCXL, 1000, 0)
        errors = validator.validate(contract.getType(), contract.getParameters())
        assertEquals 2, errors.size()
    }

    void testGoldorak() {
        IReinsuranceContractStrategy contract = getGoldorakContractStrategy(0, 0)
        def errors = validator.validate(contract.getType(), contract.getParameters())
        assertEquals 0, errors.size()

        contract = getGoldorakContractStrategy(1000, 0)
        errors = validator.validate(contract.getType(), contract.getParameters())
        assertEquals 1, errors.size()
    }
}
