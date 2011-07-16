package org.pillarone.riskanalytics.domain.pc.constraints

import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints
import org.pillarone.riskanalytics.domain.utils.constraint.IUnityPortion
import org.pillarone.riskanalytics.domain.pc.company.ICompanyMarker

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class CompanyPortion implements IMultiDimensionalConstraints, IUnityPortion {

    public static final String IDENTIFIER = "COMPANY_PORTION"
    public static int COMPANY_COLUMN_INDEX = 0;
    public static int PORTION_COLUMN_INDEX = 1;

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
        return column == 0 ? ICompanyMarker : Double
    }

    int getPortionColumnIndex() {
        return PORTION_COLUMN_INDEX
    }

    int getComponentNameColumnIndex() {
        return COMPANY_COLUMN_INDEX
    }

    Integer getColumnIndex(Class marker) {
        if (ICompanyMarker.isAssignableFrom(marker)) {
            return 0
        }
        else if (BigDecimal.isAssignableFrom(marker)) {
            return 1
        }
        return null;
    }
}