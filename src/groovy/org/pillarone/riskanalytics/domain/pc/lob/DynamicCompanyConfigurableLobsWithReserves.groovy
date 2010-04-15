package org.pillarone.riskanalytics.domain.pc.lob


/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class DynamicCompanyConfigurableLobsWithReserves extends DynamicConfigurableLobsWithReserves {

    public CompanyConfigurableLobWithReserves createDefaultSubComponent() {
        new CompanyConfigurableLobWithReserves()
    }
}