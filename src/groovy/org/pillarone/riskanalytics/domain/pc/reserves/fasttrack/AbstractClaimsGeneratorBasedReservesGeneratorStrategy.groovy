package org.pillarone.riskanalytics.domain.pc.reserves.fasttrack

import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker

/**
 * @author shartmann (at) munichre (dot) com
 */

abstract public class AbstractClaimsGeneratorBasedReservesGeneratorStrategy implements IParameterObject, IReservesGeneratorStrategy{

    ComboBoxTableMultiDimensionalParameter basedOnClaimsGenerators = new ComboBoxTableMultiDimensionalParameter(
            [], ["Claims Generators"], PerilMarker)

    abstract public Object getType()

    public Map getParameters() {
        return [
                'basedOnClaimsGenerators' : basedOnClaimsGenerators
        ]
    }
}