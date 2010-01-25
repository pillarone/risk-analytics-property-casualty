package org.pillarone.riskanalytics.domain.pc.underwriting;

import org.pillarone.riskanalytics.core.parameterization.IParameterObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AbsoluteExposureBaseStrategy implements IExposureBaseStrategy, IParameterObject {

    public Object getType() {
        return ExposureBaseType.ABSOLUTE;
    }

    public Map getParameters() {
        return new HashMap();
    }

    public double scaleFactor(List<UnderwritingInfo> underwritingInfos) {
        return 1;
    }
}