package org.pillarone.riskanalytics.domain.pc.reinsurance.commissions;

import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.util.GroovyUtils;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;
import org.pillarone.riskanalytics.domain.utils.InputFormatConverter;
import org.pillarone.riskanalytics.domain.utils.constraints.DoubleConstraints;

import java.util.*;


/**
 * Assigns a commission rate and calculates the commission on ceded premium based on the loss ratio
 * (total losses / total premium).
 * <p/>
 * The commission rate is a right-continuous step-function of the loss ratio, with a finite number of jumps.
 * Each step interval, or "commission band", is realized internally as a key-value pair in a Java Map object.
 * Each map entry's key is the interval's left endpoint, and the map's value is the commission rate that
 * applies for loss ratios in the interval. Because the interval's right endpoint is not stored in the map,
 * we use the following conventions for defining & evaluating the resulting step function:
 * <ol>
 * <li>A first band, from -Infinity, with commission 0, is always added.
 * <li>Intermediate bands must be given in order of increasing lower limit.
 * <li>Each band applies to loss ratios inclusive of the lower limit, but
 * exclusive of the upper limit (which is the next band's lower limit, if any).
 * Otherwise, the last commission is used for all sufficiently large
 * loss ratios (i.e. at or above the last band's lower limit).
 * <li>The last band given effectively has no upper limit.
 * The caller must therefore specify a last band with commission 0 under typical use cases.
 * </ol>
 *
 * @author shartmann (at) munichre (dot) com, ben.ginsberg (at) intuitive-collaboration.com
 */
public class SlidingCommissionStrategy implements ICommissionStrategy {

    public static final String LOSS_RATIO = "Loss Ratio (from)";
    public static final String COMMISSION = "Commission";
    public static final int LOSS_RATIO_COLUMN_INDEX = 0;
    public static final int COMMISSION_COLUMN_INDEX = 1;

    private ConstrainedMultiDimensionalParameter commissionBands = new ConstrainedMultiDimensionalParameter(
            GroovyUtils.convertToListOfList(new Object[]{0d, 0d}),
            Arrays.asList(LOSS_RATIO, COMMISSION),
            ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER));

    private SortedMap<Double, Double> commissionRatePerLossRatio;

    public IParameterObjectClassifier getType() {
        return CommissionStrategyType.SLIDINGCOMMISSION;
    }

    public Map getParameters() {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("commissionBands", commissionBands);
        return map;
    }

    public void calculateCommission(List<Claim> claims, List<UnderwritingInfo> underwritingInfos, boolean isFirstPeriod, boolean isAdditive) {
        double totalClaims = 0d;
        double totalPremium = 0d;
        for (Claim claim : claims) {
            totalClaims += claim.getUltimate();
        }
        for (UnderwritingInfo underwritingInfo : underwritingInfos) {
            totalPremium += underwritingInfo.getPremiumWritten();
        }
        double totalLossRatio = totalClaims / totalPremium;
        double commissionRate;
        if (commissionRatePerLossRatio == null) initCommissionRates();
        double fixedCommissionRate = commissionRatePerLossRatio.get(commissionRatePerLossRatio.lastKey());
        if (totalLossRatio < commissionRatePerLossRatio.firstKey())
            commissionRate = commissionRatePerLossRatio.get(commissionRatePerLossRatio.firstKey());
        else if (commissionRatePerLossRatio.containsKey(totalLossRatio))
            commissionRate = commissionRatePerLossRatio.get(totalLossRatio);
        else
            commissionRate = commissionRatePerLossRatio.headMap(totalLossRatio).get(commissionRatePerLossRatio.headMap(totalLossRatio).lastKey());

        if (isAdditive) {
            for (UnderwritingInfo underwritingInfo : underwritingInfos) {
                double premiumWritten = underwritingInfo.getPremiumWritten();
                underwritingInfo.setCommission(underwritingInfo.getCommission() - premiumWritten * commissionRate);
                underwritingInfo.setFixedCommission(underwritingInfo.getFixedCommission() - premiumWritten * fixedCommissionRate);
                underwritingInfo.setVariableCommission(underwritingInfo.getVariableCommission() - premiumWritten * (commissionRate - fixedCommissionRate));
            }
        }
        else {
            for (UnderwritingInfo underwritingInfo : underwritingInfos) {
                double premiumWritten = underwritingInfo.getPremiumWritten();
                underwritingInfo.setCommission(-premiumWritten * commissionRate);
                underwritingInfo.setFixedCommission(-premiumWritten * fixedCommissionRate);
                underwritingInfo.setVariableCommission(-premiumWritten * (commissionRate - fixedCommissionRate));
            }
        }
    }

    private void initCommissionRates() {
        int numberOfEntries = commissionBands.getValueRowCount();
        commissionRatePerLossRatio = new TreeMap<Double, Double>();
        for (int i = 1; i <= numberOfEntries; i++) {
            double lossRatio = InputFormatConverter.getDouble(commissionBands.getValueAt(i, LOSS_RATIO_COLUMN_INDEX));
            double commissionRate = InputFormatConverter.getDouble(commissionBands.getValueAt(i, COMMISSION_COLUMN_INDEX));
            double previousCommissionRate = commissionRate;
            if (commissionRatePerLossRatio.containsKey(lossRatio))
                previousCommissionRate = commissionRatePerLossRatio.get(lossRatio);
            commissionRatePerLossRatio.put(lossRatio, Math.min(previousCommissionRate, commissionRate));
        }
    }

    public SortedMap<Double, Double> getCommissionRatePerLossRatio() {
        return commissionRatePerLossRatio;
    }

    public void setCommissionRatePerLossRatio(SortedMap<Double, Double> commissionRatePerLossRatio) {
        this.commissionRatePerLossRatio = commissionRatePerLossRatio;
    }
}