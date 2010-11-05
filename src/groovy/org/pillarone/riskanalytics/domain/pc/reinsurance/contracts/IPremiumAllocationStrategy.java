package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts;

import org.pillarone.riskanalytics.core.parameterization.IParameterObject;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;

import java.util.List;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public interface IPremiumAllocationStrategy extends IParameterObject {
    void initSegmentShares(List<Claim> cededClaims, List<UnderwritingInfo> grossUnderwritingInfos);
    double getShare(UnderwritingInfo grossUnderwritingInfo);
}