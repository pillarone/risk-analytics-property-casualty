package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts;

import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString;
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory;
import org.pillarone.riskanalytics.core.util.GroovyUtils;
import org.pillarone.riskanalytics.domain.pc.company.CompanyPortion;
import org.pillarone.riskanalytics.domain.pc.company.ICompanyMarker;

import java.util.Arrays;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class MultiCompanyCoverAttributeReinsuranceContract extends MultiCoverAttributeReinsuranceContract {

    private ConstrainedString parmCedentCompany = new ConstrainedString(ICompanyMarker.class, "");
    private ConstrainedMultiDimensionalParameter parmReinsurers = new ConstrainedMultiDimensionalParameter(
            GroovyUtils.convertToListOfList(new Object[]{"", 1d}),
            Arrays.asList(REINSURER, PORTION),
            ConstraintsFactory.getConstraints(CompanyPortion.IDENTIFIER));

    private static final String REINSURER = "Reinsurer";
    private static final String PORTION = "Covered Portion";

    public ConstrainedString getParmCedentCompany() {
        return parmCedentCompany;
    }

    public void setParmCedentCompany(ConstrainedString parmCedentCompany) {
        this.parmCedentCompany = parmCedentCompany;
    }

    public ConstrainedMultiDimensionalParameter getParmReinsurers() {
        return parmReinsurers;
    }

    public void setParmReinsurers(ConstrainedMultiDimensionalParameter parmReinsurers) {
        this.parmReinsurers = parmReinsurers;
    }
}
