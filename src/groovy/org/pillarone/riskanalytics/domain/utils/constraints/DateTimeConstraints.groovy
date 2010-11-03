package org.pillarone.riskanalytics.domain.utils.constraints

import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints
import org.joda.time.DateTime

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class DateTimeConstraints implements IMultiDimensionalConstraints {

    public static final String IDENTIFIER = "DATETIME"

    boolean matches(int row, int column, Object value) {
        value instanceof DateTime
    }

    String getName() {
        IDENTIFIER
    }

    Class getColumnType(int column) {
        DateTime
    }

    Integer getColumnIndex(Class marker) {
        null
    }
}
