package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class ClaimsSharesPremiumAllocationStrategy implements IPremiumAllocationStrategy {

    PremiumAllocationType getType() {
        PremiumAllocationType.CLAIMS_SHARES
    }

    public Map getParameters() {
        return [:];
    }
}

