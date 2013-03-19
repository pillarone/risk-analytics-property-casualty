package org.pillarone.riskanalytics.domain.pc.lob

import org.pillarone.riskanalytics.domain.pc.company.ICompanyMarker
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString
import org.pillarone.riskanalytics.core.components.ComponentCategory

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@ComponentCategory(categories = ['SEGMENT','COMPANY'])
class CompanyConfigurableLobWithReserves extends ConfigurableLobWithReserves {

    ConstrainedString parmCompany = new ConstrainedString(ICompanyMarker, '')
}
