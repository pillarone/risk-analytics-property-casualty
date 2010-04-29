package org.pillarone.riskanalytics.domain.pc.assetLiabilityMismatch;

import org.pillarone.riskanalytics.core.parameterization.ConstrainedString
import org.pillarone.riskanalytics.domain.pc.company.ICompanyMarker

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class DynamicCompanyConfigurableAssetLiabilityMismatchGenerator extends DynamicAssetLiabilityMismatchGenerator {

    public CompanyConfigurableAssetLiabilityMismatchGenerator createDefaultSubComponent() {
        new CompanyConfigurableAssetLiabilityMismatchGenerator(parmCompany: new ConstrainedString(ICompanyMarker, ''))
    }
}