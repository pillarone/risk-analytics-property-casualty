package models.finiteRe

import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.pc.generators.claims.DynamicClaimsGenerators
import org.pillarone.riskanalytics.domain.pc.reinsurance.FiniteRe
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.MultiLineReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.reinsurance.programs.MultiLineDynamicReinsuranceProgram
import org.pillarone.riskanalytics.domain.pc.global.GlobalParameters

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */

public class FiniteReModel extends StochasticModel {

    GlobalParameters globalParameters
    DynamicClaimsGenerators claimsGenerators
    MultiLineDynamicReinsuranceProgram lineOfBusinessReinsurance
    MultiLineReinsuranceContract wholeAccountStopLoss
    FiniteRe finiteRe


    public void initComponents() {
        globalParameters = new GlobalParameters()
        claimsGenerators = new DynamicClaimsGenerators()
        lineOfBusinessReinsurance = new MultiLineDynamicReinsuranceProgram()
        wholeAccountStopLoss = new MultiLineReinsuranceContract()
        finiteRe = new FiniteRe()

        addStartComponent claimsGenerators
    }

    public void wireComponents() {
        lineOfBusinessReinsurance.inClaims = claimsGenerators.outClaims
        wholeAccountStopLoss.inClaims = lineOfBusinessReinsurance.outClaimsCeded
        finiteRe.inClaims = wholeAccountStopLoss.outCoveredClaims
    }
}