package org.pillarone.riskanalytics.domain.pc.constraints

import org.pillarone.riskanalytics.domain.pc.reserves.IReserveMarker
import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints
import org.pillarone.riskanalytics.domain.utils.constraints.IUnityPortion

/**
 * @author stefan.kunz@intuitive-collaboration.com
 */
class ReservePortion implements IMultiDimensionalConstraints, IUnityPortion {

    public static final String IDENTIFIER = "RESERVE_PORTION"
    public static int RESERVES_COLUMN_INDEX = 0;
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
        return IDENTIFIER
    }

    Class getColumnType(int column) {
        return column == 0 ? IReserveMarker : BigDecimal
    }

    int getPortionColumnIndex() {
        return PORTION_COLUMN_INDEX
    }

    int getComponentNameColumnIndex() {
        return RESERVES_COLUMN_INDEX
    }

     Integer getColumnIndex(Class marker) {
        if (IReserveMarker.isAssignableFrom(marker)) {
            return 0
        }
        else if (BigDecimal.isAssignableFrom(marker)) {
            return 1
        }
        return null;
    }
}