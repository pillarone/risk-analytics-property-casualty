package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow;

import java.util.Comparator;

/**
 * Allows to compare two reinsurance contract object according to their inuring priority.
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class MultiLinesPerilsReinsuranceContractComparator implements Comparator<MultiLinesPerilsReinsuranceContract> {

    private static MultiLinesPerilsReinsuranceContractComparator instance = null;

    private MultiLinesPerilsReinsuranceContractComparator() {
    }

    public static MultiLinesPerilsReinsuranceContractComparator getInstance() {
        if (instance == null) {
            instance = new MultiLinesPerilsReinsuranceContractComparator();
        }
        return instance;
    }

    /**
     * @return a negative number if the inuring priority of the first object is smaller,
     *          zero if equal and a positive number if it is higher than the
     *          id of the second object.
     */
    public int compare(MultiLinesPerilsReinsuranceContract contract1, MultiLinesPerilsReinsuranceContract contract2) {
        return Double.compare(contract1.getParmInuringPriority(), contract2.getParmInuringPriority());
    }
}