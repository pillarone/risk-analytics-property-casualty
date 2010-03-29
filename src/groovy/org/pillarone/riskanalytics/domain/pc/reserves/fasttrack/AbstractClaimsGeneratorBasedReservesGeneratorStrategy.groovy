package org.pillarone.riskanalytics.domain.pc.reserves.fasttrack

import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier

/**
 * @author shartmann (at) munichre (dot) com
 */

abstract public class AbstractClaimsGeneratorBasedReservesGeneratorStrategy implements IParameterObject, IReservesGeneratorStrategy{

    ComboBoxTableMultiDimensionalParameter basedOnClaimsGenerators = new ComboBoxTableMultiDimensionalParameter(
            [], ["Claims Generators"], PerilMarker)

    abstract public IParameterObjectClassifier getType()

    public Map getParameters() {
        return [
                'basedOnClaimsGenerators' : basedOnClaimsGenerators
        ]
    }
}