package org.pillarone.riskanalytics.domain.pc.allocators

import org.pillarone.riskanalytics.core.packets.Packet
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter

/**
 * @author martin.melchior (at) fhnw (dot) ch
 */
class AllocationTable extends Packet {
    AbstractMultiDimensionalParameter table

    Map<Double, Double> getMap(String keyName, String valueName) {
        if (table) {
            // get the column indices associated with key and value
            int keyIdx = table.columnNames.indexOf(keyName)
            int valueIdx = table.columnNames.indexOf(valueName)
            if (keyIdx < 0 || valueIdx < 0) {
                throw new IllegalArgumentException("AllocationTable.noKeyValue")
            }

            // check whether all the values are positive
            // normalize the values so that these sum up to unity
            double sum = 0d
            for (Double value: table.values[valueIdx]) {
                if (value < 0) {
                    throw new IllegalArgumentException("AllocationTable.negativeValuesInData")
                }
                sum += value
            }

            // if sum == 0 then just continue (the allocator included in the model might be not in use) and do not normalize.
            if (sum == 0d) {
                sum = 1d
            }

            // compose the map
            Map<Double, Double> map = [:]
            for (int row = 0; row < table.valueRowCount; row++) {
                map[table.values[keyIdx][row]] = table.values[valueIdx][row] / sum
            }
            return map
        } else {
            return null
        }
    }
}