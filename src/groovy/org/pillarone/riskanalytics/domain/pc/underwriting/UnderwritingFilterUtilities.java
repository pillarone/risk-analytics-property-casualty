package org.pillarone.riskanalytics.domain.pc.underwriting;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.claims.ClaimFilterUtilities;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker;

import java.util.ArrayList;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class UnderwritingFilterUtilities {
    /**
     * @param underwritingInfos underwriting info packets to be filtered
     * @param coverCriteria     components such as RiskBands
     * @return an underwriting info packet is added to the list of filtered underwriting info packets if the
     *         originalUnderwritingInfo references an element of the cover criteria. If it didn't match or
     *         originalUnderwritingInfo property is null, the origin property is evaluated.
     */
    public static List<UnderwritingInfo> filterUnderwritingInfo(List<UnderwritingInfo> underwritingInfos, List coverCriteria) {
        List<UnderwritingInfo> filterUnderwritingInfos = new ArrayList<UnderwritingInfo>(underwritingInfos.size());
        if (coverCriteria != null) {
            for (UnderwritingInfo underwritingInfo : underwritingInfos) {
                if (underwritingInfo.getOriginalUnderwritingInfo() != null
                        && coverCriteria.contains(underwritingInfo.getOriginalUnderwritingInfo())
                        || coverCriteria.contains(underwritingInfo.origin)) {
                    filterUnderwritingInfos.add(underwritingInfo);
                }
            }
        }
        return filterUnderwritingInfos;
    }

    /**
     * @param underwritingInfos underwriting info packets to be filtered
     * @param coverCriteria     components such as RiskBands
     * @return an underwriting info packet is added to the list of filtered underwriting info packets if the origin
     *         references an element of the cover criteria.
     */
    public static List<UnderwritingInfo> filterUnderwritingInfoByOrigin(List<UnderwritingInfo> underwritingInfos, List<Component> coverCriteria) {
        List<UnderwritingInfo> filterUnderwritingInfos = new ArrayList<UnderwritingInfo>(underwritingInfos.size());
        if (coverCriteria != null) {
            for (UnderwritingInfo underwritingInfo : underwritingInfos) {
                if (coverCriteria.contains(underwritingInfo.origin)) {
                    filterUnderwritingInfos.add(underwritingInfo);
                }
            }
        }
        return filterUnderwritingInfos;
    }


    /**
     * @param underwritingInfos      underwriting info packets to be filtered
     * @param coveredLinesOfBusiness components such as RiskBands
     * @return an underwriting info packet is added to the list of filtered underwriting info packets if the lineOfBusiness
     *         references an element of the coveredLinesOfBusiness.
     */
    public static List<UnderwritingInfo> filterUnderwritingInfoByLob(List<UnderwritingInfo> underwritingInfos, List<ISegmentMarker> coveredLinesOfBusiness) {
        List<UnderwritingInfo> filterUnderwritingInfos = new ArrayList<UnderwritingInfo>(underwritingInfos.size());
        if (coveredLinesOfBusiness != null) {
            for (UnderwritingInfo underwritingInfo : underwritingInfos) {
                if (coveredLinesOfBusiness.contains(underwritingInfo.getLineOfBusiness())) {
                    filterUnderwritingInfos.add(underwritingInfo);
                }
            }
        }
        return filterUnderwritingInfos;
    }


    /**
     * @param underwritingInfos underwriting info packets to be filtered
     * @param coveredLines      components such as RiskBands
     * @param claims            claims to be filtered by Covered Segments and covered perils to obtain share factor for premium
     * @param coveredPerils     Claims generator components
     * @return an underwriting info packet is added to the list of filtered underwriting info packets if the lineOfBusiness
     *         references an element of the coveredLinesOfBusiness. The premium is scaled according to the weight of covered perils in
     *         line of business under consideration.
     */
    public static List<UnderwritingInfo> filterUnderwritingInfoByLobAndScaleByPerilsInLob(List<UnderwritingInfo> underwritingInfos, List<ISegmentMarker> coveredLines,
                                                                                          List<Claim> claims, List<IPerilMarker> coveredPerils) {

        List<UnderwritingInfo> filteredAndScaledUnderwritingInfos = new ArrayList<UnderwritingInfo>(underwritingInfos.size());

        for (ISegmentMarker coveredLine : coveredLines) {
            if (coveredLine == null) continue;
            List<Claim> lobClaims = ClaimFilterUtilities.filterClaimsByLine(claims, coveredLine, false);
            double aggregatedLobClaim = 0d;
            for (Claim claim : lobClaims) {
                aggregatedLobClaim += claim.getUltimate();
            }
            List<Claim> perilsInLobClaims = ClaimFilterUtilities.filterClaimsByPeril(lobClaims, coveredPerils);
            double aggregatedPerilsInLobClaim = 0d;
            for (Claim claim : perilsInLobClaims) {
                aggregatedPerilsInLobClaim += claim.getUltimate();
            }
            double perilShareInLob = aggregatedLobClaim == 0 ? 1d : aggregatedPerilsInLobClaim / aggregatedLobClaim;
            for (UnderwritingInfo underwritingInfo : underwritingInfos) {
                if (coveredLine.equals(underwritingInfo.getLineOfBusiness())) {
                    if (perilShareInLob == 1) {
                        filteredAndScaledUnderwritingInfos.add(underwritingInfo);
                    }
                    else {
                        UnderwritingInfo scaledUnderwritingInfo = underwritingInfo.copy();
                        scaledUnderwritingInfo.setPremium(scaledUnderwritingInfo.getPremium() * perilShareInLob);
                        filteredAndScaledUnderwritingInfos.add(scaledUnderwritingInfo);
                    }
                }
            }
        }
        return filteredAndScaledUnderwritingInfos;
    }


    /**
     * same method as filterUnderwritingInfoByLob(), but the order in the result list is the same as in
     * method filterUnderwritingInfoByLobAndScaleByPerilsInLob()
     */
    public static List<UnderwritingInfo> filterUnderwritingInfoByLobWithoutScaling(List<UnderwritingInfo> underwritingInfos, List<ISegmentMarker> coveredLines) {

        List<UnderwritingInfo> filteredUnderwritingInfos = new ArrayList<UnderwritingInfo>(underwritingInfos.size());
        for (ISegmentMarker coveredLine : coveredLines) {
            for (UnderwritingInfo underwritingInfo : underwritingInfos) {
                if (coveredLine.equals(underwritingInfo.getLineOfBusiness())) {
                    filteredUnderwritingInfos.add(underwritingInfo);
                }
            }
        }
        return filteredUnderwritingInfos;
    }

    /**
     * @param underwritingInfos underwriting info packets to be filtered
     * @param coverCriteria     components such as RiskBands
     * @return an underwriting info packet is added to the list of filtered underwriting info packets if the origin
     *         references an element of the cover criteria.
     */
    public static List<UnderwritingInfo> filterUnderwritingInfoByOriginal(List<UnderwritingInfo> underwritingInfos, List<Component> coverCriteria) {
        List<UnderwritingInfo> filterUnderwritingInfos = new ArrayList<UnderwritingInfo>(underwritingInfos.size());
        if (coverCriteria != null) {
            for (UnderwritingInfo underwritingInfo : underwritingInfos) {
                if (coverCriteria.contains(underwritingInfo.getOriginalUnderwritingInfo().origin)) {
                    filterUnderwritingInfos.add(underwritingInfo);
                }
            }
        }
        return filterUnderwritingInfos;
    }

    public static List<UnderwritingInfo> filterUnderwritingInfos(List<UnderwritingInfo> underwritingInfos, List coveredOrigin, List coveredOriginal) {
        List<UnderwritingInfo> filteredUnderwritingInfos = new ArrayList<UnderwritingInfo>();
        if ((coveredOrigin == null || coveredOrigin.size() == 0) && (coveredOriginal == null) || (coveredOriginal.size() == 0)) {
            filteredUnderwritingInfos.addAll(underwritingInfos);
        } else if (coveredOrigin.size() > 0 && coveredOriginal.size() > 0) {
            for (UnderwritingInfo underwritingInfo : underwritingInfos) {
                if (coveredOrigin.contains(underwritingInfo.origin) && coveredOriginal.contains(underwritingInfo.getOriginalUnderwritingInfo().origin)) {
                    filteredUnderwritingInfos.add(underwritingInfo);
                }
            }
        } else if (coveredOrigin.size() > 0) {
            for (UnderwritingInfo underwritingInfo : underwritingInfos) {
                if (coveredOrigin.contains(underwritingInfo.origin)) {
                    filteredUnderwritingInfos.add(underwritingInfo);
                }
            }
        } else if (coveredOriginal.size() > 0) {
            for (UnderwritingInfo underwritingInfo : underwritingInfos) {
                if (coveredOriginal.contains(underwritingInfo.getOriginalUnderwritingInfo().origin)) {
                    filteredUnderwritingInfos.add(underwritingInfo);
                }
            }
        }
        return filteredUnderwritingInfos;
    }

    public static List<UnderwritingInfo> filterUnderwritingInfoByOrigin(List<UnderwritingInfo> underwritingInfos, Component origin) {
        List<UnderwritingInfo> filterUnderwritingInfos = new ArrayList<UnderwritingInfo>(underwritingInfos.size());
        for (UnderwritingInfo underwritingInfo : underwritingInfos) {
            if (underwritingInfo.origin.equals(origin)) {
                filterUnderwritingInfos.add(underwritingInfo);
            }
        }
        return filterUnderwritingInfos;
    }

    public static List<CededUnderwritingInfo> filterUnderwritingInfoByOriginalOrigin(List<UnderwritingInfo> underwritingInfosGross, List<CededUnderwritingInfo> underwritingInfosCeded) {
        List<Component> originalOrigins = new ArrayList<Component>();
        for (UnderwritingInfo underwritingInfoGross : underwritingInfosGross) {
            originalOrigins.add(underwritingInfoGross.getOriginalUnderwritingInfo().origin);
        }
        List<CededUnderwritingInfo> filteredUnderwritingInfos = new ArrayList<CededUnderwritingInfo>(underwritingInfosGross.size());
        for (CededUnderwritingInfo underwritingInfoCeded : underwritingInfosCeded) {
            if (originalOrigins.contains(underwritingInfoCeded.getOriginalUnderwritingInfo().origin)) {
                filteredUnderwritingInfos.add(underwritingInfoCeded);
            }
        }
        return filteredUnderwritingInfos;
    }
}
