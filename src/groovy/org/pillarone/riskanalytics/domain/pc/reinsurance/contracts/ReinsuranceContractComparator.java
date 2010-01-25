package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts;

import java.util.Comparator;

/**
 * Allows to compare two reinsurance contract object according to their inuring priority.
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ReinsuranceContractComparator implements Comparator<ReinsuranceContract> {

    private static ReinsuranceContractComparator instance = null;

    private ReinsuranceContractComparator() {
    }

    public static ReinsuranceContractComparator getInstance() {
        if (instance == null) {
            instance = new ReinsuranceContractComparator();
        }
        return instance;
    }

    /**
     * @return a negative number if the inuring priority of the first object is smaller,
     *          zero if equal and a positive number if it is higher than the
     *          id of the second object.
     */
    public int compare(ReinsuranceContract contract1, ReinsuranceContract contract2) {
        return contract1.getParmInuringPriority() - contract2.getParmInuringPriority();
    }
}
