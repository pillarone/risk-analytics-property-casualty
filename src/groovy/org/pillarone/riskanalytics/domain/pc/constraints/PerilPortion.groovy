package org.pillarone.riskanalytics.domain.pc.constraints

import org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker
import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints
import org.pillarone.riskanalytics.core.components.IComponentMarker;

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class PerilPortion implements IMultiDimensionalConstraints {

    public static final String IDENTIFIER = "PERIL_PORTION"

    boolean matches(int row, int column, Object value) {
        if (column == 0) {
            return value instanceof String
        }
        else {
            return value instanceof Number
        }
    }

    String getName() {
        return IDENTIFIER
    }

    Class getColumnType(int column) {
        return column == 0 ? PerilMarker : BigDecimal
    }

    Integer getColumnIndex(Class marker) {
        if (PerilMarker.isAssignableFrom(marker)) {
            return 0
        }
        else if (BigDecimal.isAssignableFrom(marker)) {
            return 1
        }
        return null;
    }

}
