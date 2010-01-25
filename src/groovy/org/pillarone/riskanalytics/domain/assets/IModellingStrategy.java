package org.pillarone.riskanalytics.domain.assets;

import org.pillarone.riskanalytics.core.parameterization.IParameterObject;

/**
 * @author cyril (dot) neyme (at) kpmg (dot) fr
 */
public interface IModellingStrategy extends IParameterObject {

    void reset();  //maybe useful to clean up

}
