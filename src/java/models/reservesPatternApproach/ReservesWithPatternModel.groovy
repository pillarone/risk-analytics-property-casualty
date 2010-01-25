package models.reservesPatternApproach

import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.pc.generators.claims.DynamicClaimsGenerators
import org.pillarone.riskanalytics.domain.pc.reserves.cashflow.ClaimDevelopment
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContract

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */

public class ReservesWithPatternModel extends StochasticModel {

    DynamicClaimsGenerators claimsGenerators
    ClaimDevelopment claimDevelopment
    ReinsuranceContract reinsuranceContract

    public void initComponents() {
        claimsGenerators = new DynamicClaimsGenerators()
        claimDevelopment = new ClaimDevelopment()
        reinsuranceContract = new ReinsuranceContract()

        addStartComponent claimsGenerators
    }

    public void wireComponents() {
        claimDevelopment.inClaims = claimsGenerators.outClaims
        reinsuranceContract.inClaims = claimDevelopment.outClaims
    }
}