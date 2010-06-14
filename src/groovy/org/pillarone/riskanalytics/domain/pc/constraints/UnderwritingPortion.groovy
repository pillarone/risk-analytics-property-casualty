package org.pillarone.riskanalytics.domain.pc.constraints

import org.pillarone.riskanalytics.domain.pc.underwriting.IUnderwritingInfoMarker
import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints
import org.pillarone.riskanalytics.domain.utils.constraints.IUnityPortion

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class UnderwritingPortion implements IMultiDimensionalConstraints, IUnityPortion {

    public static final String IDENTIFIER = "UNDERWRITING_PORTION"
    public static int UNDERWRITING_COLUMN_INDEX = 0;
    public static int PORTION_COLUMN_INDEX = 1;

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

    int getPortionColumnIndex() {
        return PORTION_COLUMN_INDEX
    }

    int getComponentNameColumnIndex() {
        return UNDERWRITING_COLUMN_INDEX
    }
}
