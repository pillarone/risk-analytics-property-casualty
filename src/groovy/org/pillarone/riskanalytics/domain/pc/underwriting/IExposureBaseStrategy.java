package org.pillarone.riskanalytics.domain.pc.underwriting;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IExposureBaseStrategy {
    double scaleFactor(List<UnderwritingInfo> underwritingInfos);
}
