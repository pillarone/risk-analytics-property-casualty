package org.pillarone.riskanalytics.domain.pc.assetLiabilityMismatch;

import org.pillarone.riskanalytics.domain.pc.company.ICompanyMarker
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */

public class CompanyConfigurableAssetLiabilityMismatchGenerator extends AssetLiabilityMismatchGenerator {

    ConstrainedString parmCompany = new ConstrainedString(ICompanyMarker, '')
  
}
