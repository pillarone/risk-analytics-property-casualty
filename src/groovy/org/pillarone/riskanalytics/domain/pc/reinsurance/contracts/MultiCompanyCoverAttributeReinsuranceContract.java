package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts;

import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory;
import org.pillarone.riskanalytics.core.util.GroovyUtils;
import org.pillarone.riskanalytics.domain.pc.constraints.CompanyPortion;
import org.pillarone.riskanalytics.domain.pc.creditrisk.ReinsurerDefault;
import org.pillarone.riskanalytics.domain.utils.InputFormatConverter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * This reinsurance contract allows to specify the portions covered by each reinsurer. Furthermore it considers if
 * a specific reinsurer has gone default and reduces the cover correspondingly.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class MultiCompanyCoverAttributeReinsuranceContract extends MultiCoverAttributeReinsuranceContract {

    private PacketList<ReinsurerDefault> inReinsurersDefault = new PacketList<ReinsurerDefault>(ReinsurerDefault.class);

    private ConstrainedMultiDimensionalParameter parmReinsurers = new ConstrainedMultiDimensionalParameter(
            GroovyUtils.convertToListOfList(new Object[]{"", 1d}),
            Arrays.asList(REINSURER, PORTION),
            ConstraintsFactory.getConstraints(CompanyPortion.IDENTIFIER));

    private static final String REINSURER = "Reinsurer";
    private static final String PORTION = "Covered Portion";

    /**
     * Evaluate the received default information and adjust the coveredByReinsurer factor accordingly
     * before applying the contract.
     */
    @Override
    public void doCalculation() {
        Map<String, Double> reinsurersDefault = new HashMap<String, Double>(inReinsurersDefault.size());
        for (ReinsurerDefault reinsurerDefault : inReinsurersDefault) {
            reinsurersDefault.put(reinsurerDefault.getReinsurer(), reinsurerDefault.isDefaultOccurred() ? 0d : 1d);
        }
        double coveredByReinsurers = 0d;
        for (int row = parmReinsurers.getTitleRowCount(); row < parmReinsurers.getRowCount(); row++) {
            double portion = InputFormatConverter.getDouble(parmReinsurers.getValueAt(row, parmReinsurers.getColumnIndex(PORTION)));
            String reinsurer = InputFormatConverter.getString(parmReinsurers.getValueAt(row, parmReinsurers.getColumnIndex(REINSURER)));
            double reinsurerDefault = reinsurersDefault.get(reinsurer) == null ? 1d : reinsurersDefault.get(reinsurer);
            coveredByReinsurers += portion * reinsurerDefault;
        }
        parmContractStrategy.adjustCovered(coveredByReinsurers);
        super.doCalculation();
        parmContractStrategy.resetCovered();
    }

    public ConstrainedMultiDimensionalParameter getParmReinsurers() {
        return parmReinsurers;
    }

    public void setParmReinsurers(ConstrainedMultiDimensionalParameter parmReinsurers) {
        this.parmReinsurers = parmReinsurers;
    }

    public PacketList<ReinsurerDefault> getInReinsurersDefault() {
        return inReinsurersDefault;
    }

    public void setInReinsurersDefault(PacketList<ReinsurerDefault> inReinsurersDefault) {
        this.inReinsurersDefault = inReinsurersDefault;
    }
}
