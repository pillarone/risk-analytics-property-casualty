package models.reserves

import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.pc.generators.claims.DynamicDevelopedClaimsGenerators
import org.pillarone.riskanalytics.domain.pc.reinsurance.programs.MultiLineDynamicReinsuranceProgram
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.DynamicReservesGeneratorLean
import org.pillarone.riskanalytics.domain.pc.global.GlobalParameters

/**
 * @author shartmann (at) munichre (dot) com
 */
public class ReservesModel extends StochasticModel {

    GlobalParameters globalParameters
    DynamicDevelopedClaimsGenerators claimsGenerators
    DynamicReservesGeneratorLean reserveGenerators
    MultiLineDynamicReinsuranceProgram reinsurance

    public void initComponents() {
        globalParameters = new GlobalParameters()
        claimsGenerators = new DynamicDevelopedClaimsGenerators()
        reserveGenerators = new DynamicReservesGeneratorLean()
        reinsurance = new MultiLineDynamicReinsuranceProgram()

        addStartComponent claimsGenerators
    }

    public void wireComponents() {
        reserveGenerators.inClaims = claimsGenerators.outClaims
        reinsurance.inClaims = claimsGenerators.outClaims
        reinsurance.inClaims = reserveGenerators.outClaimsDevelopment
    }
}