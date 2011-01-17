package org.pillarone.riskanalytics.domain.pc.underwriting

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */

public class PremiumWrittenExposureBaseStrategy extends DependingExposureBaseStrategy {

    static Log LOG = LogFactory.getLog(PremiumWrittenExposureBaseStrategy.class);

    public double scaleFactor(List<UnderwritingInfo> underwritingInfos) {
        List<UnderwritingInfo> filteredUnderwritingInfos = UnderwritingFilterBySegment.filterUnderwritingInfo(underwritingInfos, underwritingInformation.getValuesAsObjects());
        if (filteredUnderwritingInfos) {
            return filteredUnderwritingInfos.premium.sum();
        }
        else {
            LOG.error "filteredUnderwritingInfos is null!"
            return 1d;
        }
    }

    public IParameterObjectClassifier getType() {
        return ExposureBaseType.PREMIUMWRITTEN;
    }
}