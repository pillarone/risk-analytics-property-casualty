package org.pillarone.riskanalytics.domain.pc.underwriting;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.components.ComponentCategory;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;

import java.util.Arrays;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@ComponentCategory(categories = {"UNDERWRITING","FILTER"})
public class UnderwritingFilterBySegment extends Component {

    private PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<UnderwritingInfo> outUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private ComboBoxTableMultiDimensionalParameter parmUnderwritingSegments = new ComboBoxTableMultiDimensionalParameter(
            Arrays.asList(new String[]{""}),
            Arrays.asList(new String[]{"underwriting segment"}),
            IUnderwritingInfoMarker.class);


    public void doCalculation() {
        outUnderwritingInfo.addAll(UnderwritingFilterUtilities.filterUnderwritingInfo(inUnderwritingInfo, parmUnderwritingSegments.getValuesAsObjects()));
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

    public ComboBoxTableMultiDimensionalParameter getParmUnderwritingSegments() {
        return parmUnderwritingSegments;
    }

    public void setParmUnderwritingSegments(ComboBoxTableMultiDimensionalParameter parmUnderwritingSegments) {
        this.parmUnderwritingSegments = parmUnderwritingSegments;
    }
}