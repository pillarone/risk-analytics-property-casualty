package org.pillarone.riskanalytics.domain.pc.company

import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class CompanyPortion implements IMultiDimensionalConstraints {

    public static final String IDENTIFIER = "COMPANY_PORTION"

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
        return column == 0 ? ICompanyMarker : BigDecimal
    }
}