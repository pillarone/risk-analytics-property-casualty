package org.pillarone.riskanalytics.domain.pc.assetLiabilityMismatch;

import org.pillarone.riskanalytics.core.parameterization.ConstrainedString
import org.pillarone.riskanalytics.domain.pc.company.ICompanyMarker
import org.pillarone.riskanalytics.core.components.ComponentCategory

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
@ComponentCategory(categories = ['ALM','GENERATOR'])
public class DynamicCompanyConfigurableAssetLiabilityMismatchGenerator extends DynamicAssetLiabilityMismatchGenerator {

    public CompanyConfigurableAssetLiabilityMismatchGenerator createDefaultSubComponent() {
        new CompanyConfigurableAssetLiabilityMismatchGenerator(parmCompany: new ConstrainedString(ICompanyMarker, ''))
    }
}