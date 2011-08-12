package org.pillarone.riskanalytics.domain.pc.constraints.validation

import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.pillarone.riskanalytics.domain.pc.constraints.CompanyPortion
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter

import org.pillarone.riskanalytics.core.util.GroovyUtils
import org.pillarone.riskanalytics.domain.utils.constraint.UnderwritingPortion
import org.pillarone.riskanalytics.domain.utils.constraint.ReservePortion

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class UnityPortionValidatorTests extends GroovyTestCase {


    AbstractParameterValidationService validator = new UnityPortionValidator().validationService

    void testCompanyPortionValidator() {

        def defaultConstraint = new CompanyPortion()

        // no errors
        ConstrainedMultiDimensionalParameter goodValues = new ConstrainedMultiDimensionalParameter(
                GroovyUtils.toList([['earth re', 'venus', 'mars'], [1.0, 0.5, 0.1]]), ["Reinsurer", "Covered Portion"],
                defaultConstraint)

        def noErrors = validator.validate(defaultConstraint, goodValues)

        assertEquals "number errors is 0", 0, noErrors.size()

        // portion outside unity interval
        ConstrainedMultiDimensionalParameter value = new ConstrainedMultiDimensionalParameter(
                GroovyUtils.toList([['earth re', 'venus', 'mars'], [1.0, 0.5, -0.1]]), ["Reinsurer", "Covered Portion"],
                defaultConstraint)

        def errors = validator.validate(defaultConstraint, value)

        assertEquals "number errors is 1", 1, errors.size()
        assertEquals "not in unity interval", "portion.unity.error.portions.not.in.unity.interval", errors[0].msg
        assertEquals "wrong value", -0.1, errors[0].args[1]

        // empty portions and non-empty component references
        ConstrainedMultiDimensionalParameter emptyPortionValue = new ConstrainedMultiDimensionalParameter(
                GroovyUtils.toList([['earth re'], []]), ["Reinsurer", "Covered Portion"],
                defaultConstraint)

        def emptyPortionErrors = validator.validate(defaultConstraint, emptyPortionValue)

        assertEquals "number errors ", 1, emptyPortionErrors.size()
        assertEquals "empty portion", "portion.unity.error.portions.empty", emptyPortionErrors[0].msg


    }


    void testReservePortionValidator() {

        def defaultConstraint = new ReservePortion()

        ConstrainedMultiDimensionalParameter value = new ConstrainedMultiDimensionalParameter(
                GroovyUtils.toList([['earth accident', 'venus liability', 'mars accident'], [-1.0, 0.5, 0.1]]),
                ["Reserves", "Portion of Claims"], defaultConstraint)

        def errors = validator.validate(defaultConstraint, value)

        assertEquals "number errors is 1", 1, errors.size()
        assertEquals "not in unity interval", "portion.unity.error.portions.not.in.unity.interval", errors[0].msg
        assertEquals "wrong value", -1.0, errors[0].args[1]

    }


    void testUnderwritingPortionValidator() {

        def defaultConstraint = new UnderwritingPortion()

        ConstrainedMultiDimensionalParameter value = new ConstrainedMultiDimensionalParameter(
                GroovyUtils.toList([['motor venus', 'motor mars', 'accident mars'], [1.0, 2.5, -0.1]]),
                ["Underwriting", "Portion"], defaultConstraint)

        def errors = validator.validate(defaultConstraint, value)

        assertEquals "number errors is 1", 1, errors.size()
        assertEquals "not in unity interval", "portion.unity.error.portions.not.in.unity.interval", errors[0].msg
        assertEquals "wrong value", 2.5, errors[0].args[1]

    }


}
