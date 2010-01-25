package org.pillarone.riskanalytics.domain.pc.claims.allocation

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.allocators.AllocationTable

/**
 * @author martin.melchior (at) fhnw (dot) ch
 */
// todo(meli): misleading class name!
class ClaimsAllocation extends Component {

    AbstractMultiDimensionalParameter parmAllocationTable
    PacketList<AllocationTable> outAllocationTable = new PacketList(AllocationTable)

    public void doCalculation() {
        outAllocationTable << new AllocationTable(table: parmAllocationTable)
    }
}