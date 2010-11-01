package org.pillarone.riskanalytics.domain.pc.underwriting;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.claims.ClaimFilterUtilities;
import org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker;
import org.pillarone.riskanalytics.domain.pc.lob.LobMarker;

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
                if (underwritingInfo.originalUnderwritingInfo != null
                        && coverCriteria.contains(underwritingInfo.originalUnderwritingInfo)
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
    public static List<UnderwritingInfo> filterUnderwritingInfoByLob(List<UnderwritingInfo> underwritingInfos, List<LobMarker> coveredLinesOfBusiness) {
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
     * @param claims            claims to be filtered by covered lines and covered perils to obtain share factor for premiumWritten
     * @param coveredPerils     Claims generator components
     * @return an underwriting info packet is added to the list of filtered underwriting info packets if the lineOfBusiness
     *         references an element of the coveredLinesOfBusiness. The premium is scaled according to the weight of covered perils in
     *         line of business under consideration.
     */
    public static List<UnderwritingInfo> filterUnderwritingInfoByLobAndScaleByPerilsInLob(List<UnderwritingInfo> underwritingInfos, List<LobMarker> coveredLines,
                                                                                          List<Claim> claims, List<PerilMarker> coveredPerils) {

        List<UnderwritingInfo> filteredAndScaledUnderwritingInfos = new ArrayList<UnderwritingInfo>(underwritingInfos.size());

        for (LobMarker coveredLine : coveredLines) {
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
            for (UnderwritingInfo underwritingInfo : underwritingInfos) {
                if (coveredLine.equals(underwritingInfo.getLineOfBusiness())) {
                    UnderwritingInfo scaledUnderwritingInfo = underwritingInfo.copy();
                    scaledUnderwritingInfo.premiumWritten *= aggregatedPerilsInLobClaim / aggregatedLobClaim;
                    scaledUnderwritingInfo.premiumWrittenAsIf *= aggregatedPerilsInLobClaim / aggregatedLobClaim;
                    filteredAndScaledUnderwritingInfos.add(scaledUnderwritingInfo);
                }
            }
        }
        return filteredAndScaledUnderwritingInfos;
    }


    /**
     * same method as filterUnderwritingInfoByLob(), but the order in the result list is the same as in
     * method filterUnderwritingInfoByLobAndScaleByPerilsInLob()
     */
    public static List<UnderwritingInfo> filterUnderwritingInfoByLobWithoutScaling(List<UnderwritingInfo> underwritingInfos, List<LobMarker> coveredLines) {

        List<UnderwritingInfo> filteredUnderwritingInfos = new ArrayList<UnderwritingInfo>(underwritingInfos.size());
        for (LobMarker coveredLine : coveredLines) {
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
                if (coverCriteria.contains(underwritingInfo.originalUnderwritingInfo.origin)) {
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
                if (coveredOrigin.contains(underwritingInfo.origin) && coveredOriginal.contains(underwritingInfo.originalUnderwritingInfo.origin)) {
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
                if (coveredOriginal.contains(underwritingInfo.originalUnderwritingInfo.origin)) {
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

    public static List<UnderwritingInfo> filterUnderwritingInfoByOriginalOrigin(List<UnderwritingInfo> underwritingInfosGross, List<UnderwritingInfo> underwritingInfosCeded) {
        List<Component> originalOrigins = new ArrayList<Component>();
        for (UnderwritingInfo underwritingInfoGross : underwritingInfosGross) {
            originalOrigins.add(underwritingInfoGross.originalUnderwritingInfo.origin);
        }
        List<UnderwritingInfo> filteredUnderwritingInfos = new ArrayList<UnderwritingInfo>(underwritingInfosGross.size());
        for (UnderwritingInfo underwritingInfoCeded : underwritingInfosCeded) {
            if (originalOrigins.contains(underwritingInfoCeded.originalUnderwritingInfo.origin)) {
                filteredUnderwritingInfos.add(underwritingInfoCeded);
            }
        }
        return filteredUnderwritingInfos;
    }
}
