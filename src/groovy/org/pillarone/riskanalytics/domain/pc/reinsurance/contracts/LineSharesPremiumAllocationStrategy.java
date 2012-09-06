package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts;

import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.util.GroovyUtils;
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.utils.constraint.SegmentPortion;
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class LineSharesPremiumAllocationStrategy extends AbstractPremiumAllocation {

    private static final String LINES = "Segment";
    private static final String SHARES = "Share";



    ConstrainedMultiDimensionalParameter lineOfBusinessShares = new ConstrainedMultiDimensionalParameter(
            GroovyUtils.toList("[[],[]]"),
            Arrays.asList(LINES, SHARES),
            ConstraintsFactory.getConstraints(SegmentPortion.IDENTIFIER));

    public PremiumAllocationType getType() {
        return PremiumAllocationType.LINE_SHARES;
    }

    public Map getParameters() {
        Map<String, ConstrainedMultiDimensionalParameter> map = new HashMap<String, ConstrainedMultiDimensionalParameter>(1);
        map.put("lineOfBusinessShares", lineOfBusinessShares);
        return map;
    }

    /**
     * Fills the parameter into the lookup structure
     * @param cededClaims not used within this strategy
     * @param grossUnderwritingInfos used to resolve segment instances
     */
    public void initSegmentShares(List<Claim> cededClaims, List<UnderwritingInfo> grossUnderwritingInfos) {
        if (grossUnderwritingInfos == null || grossUnderwritingInfos.size() == 0) return;
        Map<String, ISegmentMarker> segmentNameMapping = new HashMap<String, ISegmentMarker>();
        for (UnderwritingInfo underwritingInfo : grossUnderwritingInfos) {
            if (underwritingInfo.getLineOfBusiness() == null) continue;
            segmentNameMapping.put(underwritingInfo.getLineOfBusiness().getName(), underwritingInfo.getLineOfBusiness());
        }
        Map<ISegmentMarker, Double> segmentShares = new HashMap<ISegmentMarker, Double>();
        double totalShare = 0;
        for (int row = lineOfBusinessShares.getTitleRowCount(); row < lineOfBusinessShares.getRowCount(); row++) {
            String segmentName = (String) lineOfBusinessShares.getValueAt(row, lineOfBusinessShares.getColumnIndex(LINES));
            ISegmentMarker segment = segmentNameMapping.get(segmentName);
            if (segment == null) continue;  // map only to available lines
            Double share = (Double) lineOfBusinessShares.getValueAt(row, lineOfBusinessShares.getColumnIndex(SHARES));
            totalShare += share;
            segmentShares.put(segment, share);
        }
        // normalize entered segment shares to 1
        if (totalShare != 1.0) {
            for (Map.Entry<ISegmentMarker, Double> segmentShare : segmentShares.entrySet()) {
                segmentShares.put(segmentShare.getKey(), (segmentShare.getValue() / totalShare));
            }
        }

        initUnderwritingInfoShares(grossUnderwritingInfos, segmentShares);
    }
}
