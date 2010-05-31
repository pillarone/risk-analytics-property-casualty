package org.pillarone.riskanalytics.domain.pc.underwriting;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory;
import org.pillarone.riskanalytics.core.util.GroovyUtils;
import org.pillarone.riskanalytics.domain.pc.constraints.UnderwritingPortion;
import org.pillarone.riskanalytics.domain.pc.lob.LobMarker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class UnderwritingLineOfBusinessComposer extends Component {

    private static final String underwriting = "Underwriting";
    private static final String portion = "Portion";

    private PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<UnderwritingInfo> outUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private ConstrainedMultiDimensionalParameter parmPortions = new ConstrainedMultiDimensionalParameter(
            GroovyUtils.toList("[[],[]]"),
            Arrays.asList(underwriting, portion),
            ConstraintsFactory.getConstraints(UnderwritingPortion.IDENTIFIER));


    protected void doCalculation() {
        if (inUnderwritingInfo.size() > 0) {
            List<UnderwritingInfo> lobUnderwritingInfos = new ArrayList<UnderwritingInfo>();
            int portionColumn = parmPortions.getColumnIndex(portion);
            Component lineOfBusiness = inUnderwritingInfo.get(0).sender; // works only if this component is part of a component implementing LobMarker
            for (UnderwritingInfo underwritingInfo : inUnderwritingInfo) {
                String originName = underwritingInfo.origin.getNormalizedName();
                int row = parmPortions.getColumnByName(underwriting).indexOf(originName);
                if (row > -1) {
                    UnderwritingInfo lobUnderwritingInfo = UnderwritingInfoPacketFactory.copy(underwritingInfo);
                    lobUnderwritingInfo.originalUnderwritingInfo = underwritingInfo;
                    lobUnderwritingInfo.premiumWritten *= (Double) parmPortions.getValueAt(row + 1, portionColumn);
                    lobUnderwritingInfo.premiumWrittenAsIf *= (Double) parmPortions.getValueAt(row + 1, portionColumn);
                    lobUnderwritingInfo.sumInsured *= (Double) parmPortions.getValueAt(row + 1, portionColumn);
                    lobUnderwritingInfo.maxSumInsured *= (Double) parmPortions.getValueAt(row + 1, portionColumn);
                    lobUnderwritingInfo.commission *= (Double) parmPortions.getValueAt(row + 1, portionColumn);
                    lobUnderwritingInfo.originalUnderwritingInfo = underwritingInfo;
                    lobUnderwritingInfo.origin = lineOfBusiness;
                    lobUnderwritingInfo.setLineOfBusiness((LobMarker) lineOfBusiness);
                    lobUnderwritingInfos.add(lobUnderwritingInfo);
                }
            }
            outUnderwritingInfo.addAll(lobUnderwritingInfos);
        }
    }

    public PacketList<UnderwritingInfo> getInUnderwritingInfo() {
        return inUnderwritingInfo;
    }

    public void setInUnderwritingInfo(PacketList<UnderwritingInfo> inUnderwritingInfo) {
        this.inUnderwritingInfo = inUnderwritingInfo;
    }

    public PacketList<UnderwritingInfo> getOutUnderwritingInfo() {
        return outUnderwritingInfo;
    }

    public void setOutUnderwritingInfo(PacketList<UnderwritingInfo> outUnderwritingInfo) {
        this.outUnderwritingInfo = outUnderwritingInfo;
    }

    public ConstrainedMultiDimensionalParameter getParmPortions() {
        return parmPortions;
    }

    public void setParmPortions(ConstrainedMultiDimensionalParameter parmPortions) {
        this.parmPortions = parmPortions;
    }
}