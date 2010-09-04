package org.pillarone.riskanalytics.domain.pc.company

import org.pillarone.riskanalytics.domain.pc.creditrisk.CreditDefault
import org.pillarone.riskanalytics.domain.pc.reinsurance.programs.ReinsuranceMarketWithBouquetCommissionProgram
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.MultiCompanyCoverAttributeReinsuranceContract
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.domain.assets.constants.Rating

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class DynamicCompanyTests extends GroovyTestCase {

    CreditDefault creditDefault = new CreditDefault(name: 'credit default')
    DynamicCompany companies = new DynamicCompany(name: 'companies')
    Company marsRe = new Company(name: 'mars re', parmRating: Rating.NO_DEFAULT)
    Company earthInsurance = new Company(name: 'earth insurance', parmRating: Rating.DEFAULT)
    ReinsuranceMarketWithBouquetCommissionProgram reinsuranceMarket = new ReinsuranceMarketWithBouquetCommissionProgram(name: 'market')
    MultiCompanyCoverAttributeReinsuranceContract quotaShare = new MultiCompanyCoverAttributeReinsuranceContract(name: 'quota share')

    void testUsage() {
        companies.addSubComponent marsRe
        companies.addSubComponent earthInsurance
        companies.internalWiring()
        reinsuranceMarket.subContracts.addSubComponent quotaShare
        WiringUtils.use(WireCategory) {
            companies.inDefaultProbability = creditDefault.outDefaultProbability
            reinsuranceMarket.inReinsurersDefault = companies.outReinsurersDefault
        }
        reinsuranceMarket.internalWiring()
        companies.allocateChannelsToPhases()
        creditDefault.execute()

//        assertEquals '# default probability packets', 2, reinsuranceMarket.inReinsurersDefault.size()
    }
}
