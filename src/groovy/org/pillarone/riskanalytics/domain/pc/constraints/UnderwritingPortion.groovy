package org.pillarone.riskanalytics.domain.pc.constraints

import org.pillarone.riskanalytics.domain.pc.underwriting.IUnderwritingInfoMarker
import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class UnderwritingPortion implements IMultiDimensionalConstraints {

    public static final String IDENTIFIER = "UNDERWRITING_PORTION"

    boolean matches(int row, int column, Object value) {
        if (column == 0) {
            return value instanceof String
        }
        else {
            return value instanceof Double || value instanceof BigDecimal
        }
    }

    String getName() {
        return IDENTIFIER;
    }

    Class getColumnType(int column) {
        column == 0 ? IUnderwritingInfoMarker : BigDecimal
    }


}
