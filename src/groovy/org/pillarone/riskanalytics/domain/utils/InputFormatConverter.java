package org.pillarone.riskanalytics.domain.utils;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains utility methods to read values from MDP not based on any constraints.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class InputFormatConverter {

    public static boolean convertBoolean(String b) {
        if (b.equals("true")) {
            return true;
        }
        else if (b.equals("false")) {
            return false;
        }
        else {
            throw new IllegalArgumentException(b + " can't be converted to a boolean.");
        }
    }

    public static String getString(Object value) {
        return String.valueOf(value);
    }

    public static double getDouble(Object value) {
        double result;
        if (value instanceof Integer) {
            Integer temp = (Integer) value;
            result = temp.doubleValue();
        }
        else if (value instanceof BigDecimal) {
            BigDecimal temp = (BigDecimal) value;
            result = temp.doubleValue();
        }
        else if (value instanceof String) {
            result = Double.valueOf((String) value);
        }
        else {
            result = (Double) value;
        }
        return result;
    }

    public static int getInt(Object value) {
        int result;
        if (value instanceof Double) {
            Double temp = (Double) value;
            result = temp.intValue();
        }
        else if (value instanceof String) {
            result = Integer.valueOf((String) value);
        }
        else {
            result = (Integer) value;
        }
        return result;
    }

    public static DateTime convertToDateTime(String date) {
        if (date.length() == 0) return null;
        String[] splittedDate = date.split("-");
        return new DateTime(Integer.valueOf(splittedDate[0]), Integer.valueOf(splittedDate[1]), Integer.valueOf(splittedDate[2]), 0,0,0,0);
    }

    public static List<DateTime> convertToDateTime(List<String> dates) {
        List<DateTime> temp = new ArrayList<DateTime>();
        for (String stringDate : dates) {
            String[] splittedDate = stringDate.split("-");
            temp.add(new DateTime(Integer.valueOf(splittedDate[0]), Integer.valueOf(splittedDate[1]), Integer.valueOf(splittedDate[2]), 0,0,0,0));
        }
        return temp;
    }
}
