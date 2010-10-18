package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.util.GroovyUtils
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.pc.constraints.SegmentPortion

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class LineSharesPremiumAllocationStrategy implements IPremiumAllocationStrategy {

    private static final String LINES = "Business Lines";
    private static final String SHARES = "shares";

    ConstrainedMultiDimensionalParameter lineOfBusinessShares = new ConstrainedMultiDimensionalParameter(
            GroovyUtils.toList("[[],[]]"),
            Arrays.asList(LINES, SHARES),
            ConstraintsFactory.getConstraints(SegmentPortion.IDENTIFIER));

    PremiumAllocationType getType() {
        PremiumAllocationType.LINE_SHARES
    }

    public Map getParameters() {
        return ["lineOfBusinessShares": lineOfBusinessShares];
    }
}


