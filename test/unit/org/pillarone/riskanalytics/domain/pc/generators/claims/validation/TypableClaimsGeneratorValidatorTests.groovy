package org.pillarone.riskanalytics.domain.pc.generators.claims.validation

import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.underwriting.IUnderwritingInfoMarker


class TypableClaimsGeneratorValidatorTests extends GroovyTestCase {

    void testUnderwriting() {

        TypableClaimsGeneratorValidator validator = new TypableClaimsGeneratorValidator()
        assertTrue validator.hasSelectedUnderwritingInfo(new ComboBoxTableMultiDimensionalParameter(['string'], ['title'], IUnderwritingInfoMarker))
        assertTrue validator.hasSelectedUnderwritingInfo(new ComboBoxTableMultiDimensionalParameter([['string']], ['title'], IUnderwritingInfoMarker))

        assertFalse validator.hasSelectedUnderwritingInfo(new ComboBoxTableMultiDimensionalParameter([2], ['title'], IUnderwritingInfoMarker))
        assertFalse validator.hasSelectedUnderwritingInfo(new ComboBoxTableMultiDimensionalParameter([], ['title'], IUnderwritingInfoMarker))
        assertFalse validator.hasSelectedUnderwritingInfo(new ComboBoxTableMultiDimensionalParameter([[]], ['title'], IUnderwritingInfoMarker))

    }

}
