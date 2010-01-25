package org.pillarone.riskanalytics.domain.pc.constraints

import org.pillarone.riskanalytics.domain.pc.reserves.IReserveMarker
import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints

/**
 * @author stefan.kunz@intuitive-collaboration.com
 */
class ReservePortion implements IMultiDimensionalConstraints {

    public static final String IDENTIFIER = "RESERVE_PORTION"

    boolean matches(int row, int column, Object value) {
        if (column == 0) {
            return value instanceof String
        }
        else {
            return value instanceof Double || value instanceof BigDecimal
        }
    }

    String getName() {
        return IDENTIFIER
    }

    Class getColumnType(int column) {
        return column == 0 ? IReserveMarker : BigDecimal
    }


}