package org.pillarone.riskanalytics.domain.pc.lob

import org.pillarone.riskanalytics.domain.pc.company.ICompanyMarker
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class CompanyConfigurableLobWithReserves extends ConfigurableLobWithReserves {

    ConstrainedString parmCompany = new ConstrainedString(ICompanyMarker, '')
}
