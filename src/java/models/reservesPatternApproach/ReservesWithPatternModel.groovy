package models.reservesPatternApproach

import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.pc.generators.claims.DynamicClaimsGenerators
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.reserves.cashflow.ClaimDevelopment
import org.pillarone.riskanalytics.domain.pc.global.GlobalParameters

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */

public class ReservesWithPatternModel extends StochasticModel {

    GlobalParameters globalParameters
    DynamicClaimsGenerators claimsGenerators
    ClaimDevelopment claimDevelopment
    ReinsuranceContract reinsuranceContract

    public void initComponents() {
        globalParameters = new GlobalParameters()
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