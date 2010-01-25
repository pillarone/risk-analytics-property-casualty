package org.pillarone.riskanalytics.domain.pc.underwriting

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */

public class PremiumWrittenExposureBaseStrategy extends DependingExposureBaseStrategy {

    static Log LOG = LogFactory.getLog(PremiumWrittenExposureBaseStrategy.class);

    public double scaleFactor(List<UnderwritingInfo> underwritingInfos) {
        List<UnderwritingInfo> filteredUnderwritingInfos = UnderwritingFilterBySegment.filterUnderwritingInfo(underwritingInfos, underwritingInformation.getValuesAsObjects());
        if (filteredUnderwritingInfos) {
            return filteredUnderwritingInfos.premiumWritten.sum();
        }
        else {
            LOG.error "filteredUnderwritingInfos is null!"
            return 1d;
        }
    }

    public Object getType() {
        return ExposureBaseType.PREMIUMWRITTEN;
    }
}