package org.pillarone.riskanalytics.domain.pc.generators.fac;

import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class FacShareUtils {

    public static FacShareAndRetention filterFacShares(List<FacShareAndRetention> facShares, List coverCriteria) {
        FacShareAndRetention filteredFacShares = new FacShareAndRetention();
        if (coverCriteria != null) {
            for (FacShareAndRetention facShare : facShares) {
                UnderwritingInfo underwritingInfo = facShare.firstUnderwritingInfo();
                if (underwritingInfo.getOriginalUnderwritingInfo() != null
                        && coverCriteria.contains(underwritingInfo.getOriginalUnderwritingInfo())
                        || coverCriteria.contains(underwritingInfo.origin)) {
                    filteredFacShares.addAll(facShare);
                }
            }
        }
        return filteredFacShares;
    }
}
