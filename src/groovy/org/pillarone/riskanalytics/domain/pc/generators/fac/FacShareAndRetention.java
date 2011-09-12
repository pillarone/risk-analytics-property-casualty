package org.pillarone.riskanalytics.domain.pc.generators.fac;


import org.pillarone.riskanalytics.core.packets.Packet;
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.IReinsuranceContractStrategy;
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.QuotaShareContractStrategy;
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.SurplusContractStrategy;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomDistribution;
import org.pillarone.riskanalytics.domain.utils.math.generator.IRandomNumberGenerator;
import org.pillarone.riskanalytics.domain.utils.math.generator.RandomNumberGeneratorFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class FacShareAndRetention extends Packet {

    Map<UnderwritingInfo, FacRandomDistributions> facDistributionsByUwInfo;

    public FacShareAndRetention() {
        facDistributionsByUwInfo = new HashMap<UnderwritingInfo, FacRandomDistributions>();
    }

    public void add(UnderwritingInfo underwritingInfo, RandomDistribution quotaShare, RandomDistribution surplus) {
        if (facDistributionsByUwInfo.get(underwritingInfo) == null) {
            facDistributionsByUwInfo.put(underwritingInfo, new FacRandomDistributions(quotaShare, surplus));
        }
    }

    public Double getShare(UnderwritingInfo key, IReinsuranceContractStrategy contractStrategy) {
        return facDistributionsByUwInfo.get(key).getFacShare(contractStrategy);
    }
    
    public Double getQuotaShare(UnderwritingInfo key) {
        FacRandomDistributions facShareDistributions = facDistributionsByUwInfo.get(key);
        if (facShareDistributions == null) {
            return 0d;
        }
        else {
            return facShareDistributions.getFacQuotaShare();
        }
    }

    public Double getSurplusShare(UnderwritingInfo key) {
        FacRandomDistributions facShareDistributions = facDistributionsByUwInfo.get(key);
        if (facShareDistributions == null) {
            return 0d;
        }
        else {
            return facShareDistributions.getFacSurplusShare();
        }
    }

    private class FacRandomDistributions {

        IRandomNumberGenerator facQuotaShareGenerator;
        IRandomNumberGenerator facSurplusShareGenerator;

        FacRandomDistributions(RandomDistribution facQuotaShare, RandomDistribution facSurplusShare) {
            facQuotaShareGenerator = RandomNumberGeneratorFactory.getGenerator(facQuotaShare);
            facSurplusShareGenerator = RandomNumberGeneratorFactory.getGenerator(facSurplusShare);
        }

        double getFacShare(IReinsuranceContractStrategy contractStrategy) {
            if (contractStrategy instanceof QuotaShareContractStrategy) {
                return getFacQuotaShare();
            }
            else if (contractStrategy instanceof SurplusContractStrategy) {
                return getFacSurplusShare();
            }
            else {
                return 0d;
            }
        }

        double getFacQuotaShare() {
            return (Double) facQuotaShareGenerator.nextValue();
        }

        double getFacSurplusShare() {
            return (Double) facSurplusShareGenerator.nextValue();
        }
    }
}
