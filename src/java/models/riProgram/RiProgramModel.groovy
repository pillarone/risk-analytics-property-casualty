package models.riProgram

import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.pc.generators.claims.AttritionalSingleClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.reinsurance.programs.DynamicReinsuranceProgram

/**
 * @author: stefan.kunz (at) intuitive-collaboration (dot) com
 */
class RiProgramModel extends StochasticModel {

    AttritionalSingleClaimsGenerator claimsGenerator
    DynamicReinsuranceProgram reinsuranceProgram

    void initComponents() {
        claimsGenerator = new AttritionalSingleClaimsGenerator()
        reinsuranceProgram = new DynamicReinsuranceProgram()
        allComponents << claimsGenerator
        allComponents << reinsuranceProgram
        addStartComponent claimsGenerator
    }

    void wireComponents() {
        reinsuranceProgram.inClaims = claimsGenerator.outClaims
    }
}