package org.pillarone.riskanalytics.domain.pc.filter;

import org.pillarone.riskanalytics.core.components.PeriodStore;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;
import org.pillarone.riskanalytics.domain.pc.claims.ClaimFilterUtilities;
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.ICoverAttributeStrategy;
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.ILinesOfBusinessCoverAttributeStrategy;
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.IPerilCoverAttributeStrategy;
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.IReservesCoverAttributeStrategy;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;
import org.pillarone.riskanalytics.domain.utils.marker.IReserveMarker;
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class FilterUtils {

    private static final String COVERED_LINES = "covered lines";
    private static final String COVERED_LINES_DERIVED = "covered lines derived from filter claims";
    private static final String COVERED_PERILS = "covered perils";
    private static final String COVERED_RESERVES = "covered reserves";


    public static List<ISegmentMarker> getCoveredLines(ComboBoxTableMultiDimensionalParameter parmCovered,
                                                  PeriodStore periodStore) {
        List<ISegmentMarker> coveredLines = (List<ISegmentMarker>) periodStore.get(COVERED_LINES);
        if (coveredLines == null) {
            coveredLines = parmCovered.getValuesAsObjects(0, false);
            periodStore.put(COVERED_LINES, coveredLines);
        }
        return coveredLines;
    }

    public static List<ISegmentMarker> getCoveredLines(ICoverAttributeStrategy parmCovered, PeriodStore periodStore) {
        List<ISegmentMarker> coveredLines = (List<ISegmentMarker>) periodStore.get(COVERED_LINES);
        if (coveredLines == null && parmCovered instanceof ILinesOfBusinessCoverAttributeStrategy) {
            coveredLines = ((ILinesOfBusinessCoverAttributeStrategy) parmCovered).getLines().getValuesAsObjects(0, false);
            periodStore.put(COVERED_LINES, coveredLines);
        }
        return coveredLines;
    }

    public static List<ISegmentMarker> getCoveredLines(ComboBoxTableMultiDimensionalParameter parmCovered,
                                                  PacketList channel, PeriodStore periodStore) {
        List<ISegmentMarker> coveredLines = (List<ISegmentMarker>) periodStore.get(COVERED_LINES);
        if (coveredLines == null) {
            coveredLines = parmCovered.getValuesAsObjects(0, false);
            periodStore.put(COVERED_LINES, coveredLines);
            if (coveredLines.isEmpty()) {
                coveredLines = ClaimFilterUtilities.getLinesOfBusiness(channel);
                periodStore.put(COVERED_LINES_DERIVED, coveredLines);
            }
        }
        return coveredLines;
    }

    public static List<IPerilMarker> getCoveredPerils(ComboBoxTableMultiDimensionalParameter parmCovered,
                                                     PeriodStore periodStore) {
        List<IPerilMarker> coveredPerils = (List<IPerilMarker>) periodStore.get(COVERED_PERILS);
        if (coveredPerils == null) {
            coveredPerils = parmCovered.getValuesAsObjects(0, false);
            periodStore.put(COVERED_PERILS, coveredPerils);
        }
        return coveredPerils;
    }

    public static List<IPerilMarker> getCoveredPerils(ICoverAttributeStrategy parmCovered, PeriodStore periodStore) {
        List<IPerilMarker> coveredPerils = (List<IPerilMarker>) periodStore.get(COVERED_PERILS);
        if (coveredPerils == null && parmCovered instanceof IPerilCoverAttributeStrategy) {
            coveredPerils = ((IPerilCoverAttributeStrategy) parmCovered).getPerils().getValuesAsObjects(0, false);
            periodStore.put(COVERED_PERILS, coveredPerils);
        }
        return coveredPerils;
    }

    public static List<IReserveMarker> getCoveredReserves(ComboBoxTableMultiDimensionalParameter parmCovered,
                                                          PeriodStore periodStore) {
        List<IReserveMarker> coveredReserves = (List<IReserveMarker>) periodStore.get(COVERED_RESERVES);
        if (coveredReserves == null) {
            coveredReserves = parmCovered.getValuesAsObjects(0, false);
            periodStore.put(COVERED_RESERVES, coveredReserves);
        }
        return coveredReserves;
    }

    public static List<IReserveMarker> getCoveredReserves(ICoverAttributeStrategy parmCovered, PeriodStore periodStore) {
        List<IReserveMarker> coveredReserves = (List<IReserveMarker>) periodStore.get(COVERED_RESERVES);
        if (coveredReserves == null && parmCovered instanceof IReservesCoverAttributeStrategy) {
            coveredReserves = ((IReservesCoverAttributeStrategy) parmCovered).getReserves().getValuesAsObjects(0, false);
            periodStore.put(COVERED_RESERVES, coveredReserves);
        }
        return coveredReserves;
    }
}
