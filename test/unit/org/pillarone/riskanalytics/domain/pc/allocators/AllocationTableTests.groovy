package org.pillarone.riskanalytics.domain.pc.allocators

import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter

/**
 * @author martin.melchior (at) fhnw (dot) ch
 */
class AllocationTableTests extends GroovyTestCase {

    void testGetMap() {
        AbstractMultiDimensionalParameter table = new TableMultiDimensionalParameter(
            [[100d, 400d, 800d], [80d, -200d, 500d], [5000d, 2000d, 3000d], [0d, 0d, 0d]],
            ['col1', 'col2', 'col3', 'col4']
        )
        AllocationTable allocationTable = new AllocationTable(table: table)
        List<Double> weights = [0.5d, 0.2d, 0.3d]
        Map<Double, Double> map = allocationTable.getMap('col1', 'col3')
        List<Double> out = []
        out.addAll(map.values())
        assertEquals "allocation weights col 3", weights, out

        shouldFail(IllegalArgumentException, {
            allocationTable.getMap('col1', 'col2')
        })

        weights = [0d, 0d, 0d]
        map = allocationTable.getMap('col1', 'col4')
        out = []
        out.addAll(map.values())
        assertEquals "allocation weights col 4", weights, out
    }
}