package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class PremiumSharesPremiumAllocationStrategy implements IPremiumAllocationStrategy {

    PremiumAllocationType getType() {
        PremiumAllocationType.PREMIUM_SHARES
    }

    public Map getParameters() {
        return [:];
    }
}
