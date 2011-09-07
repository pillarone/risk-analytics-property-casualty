package org.pillarone.riskanalytics.domain.pc.generators.fac;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.components.IterationStore;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString;
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.util.GroovyUtils;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingFilterUtilities;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;
import org.pillarone.riskanalytics.domain.utils.InputFormatConverter;
import org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker;
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomDistribution;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This component will send out in every iteration and period the same information. Only underwriting information
 * received in the first period and iteration are considered to build this information.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class FacShareDistributions extends Component {

    private IterationStore iterationStore;

    private PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<FacShareAndRetention> outDistributionsByUwInfo = new PacketList<FacShareAndRetention>(FacShareAndRetention.class);

    private ConstrainedString parmLinkedUnderwritingInfo = new ConstrainedString(IUnderwritingInfoMarker.class, "");

    private TableMultiDimensionalParameter parmAllocation = new TableMultiDimensionalParameter(
            GroovyUtils.convertToListOfList(new Object[]{0d, 0d, 0d, 0d, 0d}),
            Arrays.asList(MAX_SUM_INSURED, COUNT_OF_POLICIES, QUOTA_SHARE_PRC, SUX_SHARE_PRC, RETENTION_PRC));

    private static final String FAC_SHARE_AND_RETENTION = "fac share and retention";

    private static final String MAX_SUM_INSURED = "Max Sum Insured";
    private static final String COUNT_OF_POLICIES = "Count of Policies";
    private static final String QUOTA_SHARE_PRC = "Quota Share %";
    private static final String SUX_SHARE_PRC = "Surplus %";
    private static final String RETENTION_PRC = "Retention %";

    @Override
    protected void doCalculation() {
        FacShareAndRetention facShareAndRetention = getFacShareAndRetention();
        outDistributionsByUwInfo.add(facShareAndRetention);
    }

    private FacShareAndRetention getFacShareAndRetention() {
        FacShareAndRetention facShareAndRetention = (FacShareAndRetention) iterationStore.getFirstPeriod(FAC_SHARE_AND_RETENTION);
        if (facShareAndRetention == null) {
            List<UnderwritingInfo> filteredUnderwritingInfo = UnderwritingFilterUtilities.filterUnderwritingInfo(
                        inUnderwritingInfo, parmLinkedUnderwritingInfo.getSelectedComponent());
            Map<Double, UnderwritingInfo> uwInfoLookupBySumInsured = new HashMap<Double, UnderwritingInfo>();
            for (UnderwritingInfo uwInfo : filteredUnderwritingInfo) {
                uwInfoLookupBySumInsured.put(uwInfo.getMaxSumInsured(), uwInfo);
            }

            int columnMaxSumInsured = parmAllocation.getColumnIndex(MAX_SUM_INSURED);
            int columnCountOfPolicies = parmAllocation.getColumnIndex(COUNT_OF_POLICIES);
            int columnQuotaShare = parmAllocation.getColumnIndex(QUOTA_SHARE_PRC);
            int columnSurplus = parmAllocation.getColumnIndex(SUX_SHARE_PRC);
            int columnRetention = parmAllocation.getColumnIndex(RETENTION_PRC);

            Map<UnderwritingInfo, FacShareRetentionHelper> distributionsPerRiskBand = new HashMap<UnderwritingInfo, FacShareRetentionHelper>();

            for (int row = parmAllocation.getTitleRowCount(); row < parmAllocation.getRowCount(); row++) {
                Double maxSumInsured =  InputFormatConverter.getDouble(parmAllocation.getValueAt(row, columnMaxSumInsured));
                double policiesCount = InputFormatConverter.getDouble(parmAllocation.getValueAt(row, columnCountOfPolicies));
                double quotaSharePercentage = InputFormatConverter.getDouble(parmAllocation.getValueAt(row, columnQuotaShare));
                double suxPercentage = InputFormatConverter.getDouble(parmAllocation.getValueAt(row, columnSurplus));
                double retentionPercentage = InputFormatConverter.getDouble(parmAllocation.getValueAt(row, columnRetention));

                UnderwritingInfo lookupUwInfo = uwInfoLookupBySumInsured.get(maxSumInsured);
                if (lookupUwInfo != null) {
                    FacShareRetentionHelper helper = distributionsPerRiskBand.get(lookupUwInfo);
                    if (helper == null) {
                        helper = new FacShareRetentionHelper();
                        distributionsPerRiskBand.put(uwInfoLookupBySumInsured.get(maxSumInsured), helper);
                    }
                    helper.add(policiesCount, quotaSharePercentage, suxPercentage, retentionPercentage);
                }
            }

            facShareAndRetention = new FacShareAndRetention();
            for (Map.Entry<UnderwritingInfo, FacShareRetentionHelper> entry : distributionsPerRiskBand.entrySet()) {
                RandomDistribution quotaShareDistribution = ((FacShareRetentionHelper) entry.getValue()).getFacQuotaShareDistribution();
                RandomDistribution surplusDistribution = ((FacShareRetentionHelper) entry.getValue()).getFacSurplusSharesDistribution();
                RandomDistribution retentionDistribution = ((FacShareRetentionHelper) entry.getValue()).getFacRetentionDistribution();
                facShareAndRetention.add(entry.getKey(), quotaShareDistribution, surplusDistribution, retentionDistribution);
            }
            iterationStore.put(FAC_SHARE_AND_RETENTION, facShareAndRetention);
        }
        return facShareAndRetention;
    }

    public ConstrainedString getParmLinkedUnderwritingInfo() {
        return parmLinkedUnderwritingInfo;
    }

    public void setParmLinkedUnderwritingInfo(ConstrainedString parmLinkedUnderwritingInfo) {
        this.parmLinkedUnderwritingInfo = parmLinkedUnderwritingInfo;
    }

    public PacketList<UnderwritingInfo> getInUnderwritingInfo() {
        return inUnderwritingInfo;
    }

    public void setInUnderwritingInfo(PacketList<UnderwritingInfo> inUnderwritingInfo) {
        this.inUnderwritingInfo = inUnderwritingInfo;
    }

    public PacketList<FacShareAndRetention> getOutDistributionsByUwInfo() {
        return outDistributionsByUwInfo;
    }

    public void setOutDistributionsByUwInfo(PacketList<FacShareAndRetention> outDistributionsByUwInfo) {
        this.outDistributionsByUwInfo = outDistributionsByUwInfo;
    }

    public IterationStore getIterationStore() {
        return iterationStore;
    }

    public void setIterationStore(IterationStore iterationStore) {
        this.iterationStore = iterationStore;
    }

    public TableMultiDimensionalParameter getParmAllocation() {
        return parmAllocation;
    }

    public void setParmAllocation(TableMultiDimensionalParameter parmAllocation) {
        this.parmAllocation = parmAllocation;
    }
}
