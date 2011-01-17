package org.pillarone.riskanalytics.domain.pc.underwriting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pillarone.riskanalytics.domain.pc.constants.Exposure;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class UnderwritingUtilities {

    static Log LOG = LogFactory.getLog(UnderwritingUtilities.class);

    public static double scaleFactor(List<UnderwritingInfo> underwritingInfos, Exposure base) {
        if (underwritingInfos.size() > 0) {
            double scaleFactor = 0d;
            if (base.equals(Exposure.PREMIUM_WRITTEN)) {
                for (UnderwritingInfo underwritingInfo : underwritingInfos) {
                    scaleFactor += underwritingInfo.getPremium();
                }
            }
            else if (base.equals(Exposure.NUMBER_OF_POLICIES)) {
                for (UnderwritingInfo underwritingInfo : underwritingInfos) {
                    scaleFactor += underwritingInfo.getNumberOfPolicies();
                }
            }
            else if (base.equals(Exposure.SUM_INSURED)) {
                for (UnderwritingInfo underwritingInfo : underwritingInfos) {
                    scaleFactor += underwritingInfo.getSumInsured();
                }
            }
            else if (base.equals(Exposure.ABSOLUTE)) {
                scaleFactor = 1d;
            }
            return scaleFactor;
        }
        else {
            return 1d;
        }
    }
}