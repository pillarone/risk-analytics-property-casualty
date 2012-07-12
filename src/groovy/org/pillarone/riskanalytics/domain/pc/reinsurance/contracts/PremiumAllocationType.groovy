package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.utils.constraint.SegmentPortion
import org.pillarone.riskanalytics.core.simulation.InvalidParameterException

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class PremiumAllocationType extends AbstractParameterObjectClassifier {

    protected static Map types = [:]
    static {
        PremiumAllocationType.all.each {
            PremiumAllocationType.types[it.toString()] = it
        }
    }

    private static final String LINES = "Segment";
    private static final String SHARES = "Share";

    public static final PremiumAllocationType PREMIUM_SHARES = new PremiumAllocationType("Premium Shares", "PREMIUM_SHARES", [:])
    public static final PremiumAllocationType CLAIMS_SHARES = new PremiumAllocationType("Claims Shares", "CLAIMS_SHARES", [:])
    public static final PremiumAllocationType LINE_SHARES = new PremiumAllocationType("Segment Shares", "LINE_SHARES",
            [lineOfBusinessShares: new ConstrainedMultiDimensionalParameter([[],[]], [LINES, SHARES],
                    ConstraintsFactory.getConstraints(SegmentPortion.IDENTIFIER))])

    public static final all = [PREMIUM_SHARES, CLAIMS_SHARES, LINE_SHARES]

    private PremiumAllocationType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }

    public static PremiumAllocationType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return PremiumAllocationType.getStrategy(this, parameters)
    }

    static IPremiumAllocationStrategy getStrategy(PremiumAllocationType type, Map parameters) {
        IPremiumAllocationStrategy premiumAllocator;
        switch (type) {
            case PremiumAllocationType.PREMIUM_SHARES:
                premiumAllocator = new PremiumSharesPremiumAllocationStrategy()
                break;
            case PremiumAllocationType.CLAIMS_SHARES:
                premiumAllocator = new ClaimsSharesPremiumAllocationStrategy()
                break;
            case PremiumAllocationType.LINE_SHARES:
                premiumAllocator = new LineSharesPremiumAllocationStrategy(lineOfBusinessShares: (ConstrainedMultiDimensionalParameter) parameters["lineOfBusinessShares"])
                break;
            default:
                throw new InvalidParameterException("PremiumAllocationType $type not implemented")

        }
        return premiumAllocator;
    }

}


