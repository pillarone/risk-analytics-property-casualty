package org.pillarone.riskanalytics.domain.pc.company


import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory


import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.lob.CompanyConfigurableLobWithReserves
import org.pillarone.riskanalytics.domain.pc.assetLiabilityMismatch.CompanyConfigurableAssetLiabilityMismatchGenerator
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.MultiCompanyCoverAttributeReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.core.components.PeriodStore

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */

class CompanyTests extends GroovyTestCase {

    Company companyVenusRe = new Company(name: 'venus re', periodStore: new PeriodStore(null))
    Company companyMarsRe = new Company(name: 'mars re', periodStore: new PeriodStore(null))
    Company companyPlutoRe = new Company(name: 'pluto re', periodStore: new PeriodStore(null))

    ConstrainedString parmCompanyVenusRe = new ConstrainedString(ICompanyMarker, 'venus re')
    ConstrainedString parmCompanyMarsRe = new ConstrainedString(ICompanyMarker, 'mars re')
    ConstrainedString parmCompanyPlutoRe = new ConstrainedString(ICompanyMarker, 'pluto re')

    CompanyConfigurableLobWithReserves motorVenusRe = new CompanyConfigurableLobWithReserves(parmCompany: parmCompanyVenusRe)
    CompanyConfigurableLobWithReserves motorMarsRe = new CompanyConfigurableLobWithReserves(parmCompany: parmCompanyMarsRe)
    CompanyConfigurableLobWithReserves motorPlutoRe = new CompanyConfigurableLobWithReserves(parmCompany: parmCompanyPlutoRe)
    CompanyConfigurableLobWithReserves accidentMarsRe = new CompanyConfigurableLobWithReserves(parmCompany: parmCompanyMarsRe)

    CompanyConfigurableAssetLiabilityMismatchGenerator loanVenusRe = new CompanyConfigurableAssetLiabilityMismatchGenerator(parmCompany: parmCompanyVenusRe)
    CompanyConfigurableAssetLiabilityMismatchGenerator loanMarsRe = new CompanyConfigurableAssetLiabilityMismatchGenerator(parmCompany: parmCompanyMarsRe)
    CompanyConfigurableAssetLiabilityMismatchGenerator loanPlutoRe = new CompanyConfigurableAssetLiabilityMismatchGenerator(parmCompany: parmCompanyPlutoRe)
    CompanyConfigurableAssetLiabilityMismatchGenerator loan2MarsRe = new CompanyConfigurableAssetLiabilityMismatchGenerator(parmCompany: parmCompanyMarsRe)


    ConstrainedMultiDimensionalParameter reinsurerMarsRe = new ConstrainedMultiDimensionalParameter([['mars re'], [1d]],
            ["Reinsurer", "Covered Portion"], ConstraintsFactory.getConstraints('COMPANY_PORTION'))
    ConstrainedMultiDimensionalParameter reinsurerPlutoAndVenus = new ConstrainedMultiDimensionalParameter([['pluto re', 'venus re'], [0.5d, 0.3d]],
            ["Reinsurer", "Covered Portion"], ConstraintsFactory.getConstraints('COMPANY_PORTION'))

    MultiCompanyCoverAttributeReinsuranceContract motorVenusReQuotaShare = new MultiCompanyCoverAttributeReinsuranceContract(
            parmReinsurers: reinsurerMarsRe)
    MultiCompanyCoverAttributeReinsuranceContract accidentMarsReStopLoss = new MultiCompanyCoverAttributeReinsuranceContract(
            parmReinsurers: reinsurerPlutoAndVenus)


    void testClaims() {
        parmCompanyVenusRe.selectedComponent = companyVenusRe
        parmCompanyMarsRe.selectedComponent = companyMarsRe
        parmCompanyPlutoRe.selectedComponent = companyPlutoRe

        // ClaimsGross
        Claim claimG100V = new Claim(ultimate: 100, lineOfBusiness: motorVenusRe)
        Claim claimG500M = new Claim(ultimate: 500, lineOfBusiness: motorMarsRe)
        Claim claimG200V = new Claim(ultimate: 200, lineOfBusiness: motorVenusRe)
        Claim claimG600P = new Claim(ultimate: 600, lineOfBusiness: motorPlutoRe)
        Claim claimG300M = new Claim(ultimate: 300, lineOfBusiness: accidentMarsRe)

        // ClaimsCeded
        Claim claimC50V = new Claim(ultimate: 50, lineOfBusiness: motorVenusRe,
                reinsuranceContract: motorVenusReQuotaShare)
        Claim claimC200M = new Claim(ultimate: 200, lineOfBusiness: motorMarsRe)
        Claim claimC180V = new Claim(ultimate: 180, lineOfBusiness: motorVenusRe, reinsuranceContract: motorVenusReQuotaShare)
        Claim claimC150M = new Claim(ultimate: 150, lineOfBusiness: accidentMarsRe, reinsuranceContract: accidentMarsReStopLoss)

        companyVenusRe.inClaimsGross << claimG100V << claimG500M << claimG200V << claimG600P << claimG300M
        companyVenusRe.inClaimsCeded << claimC50V << claimC200M << claimC180V << claimC150M
        companyVenusRe.doCalculation(Company.PHASE_AGGREGATION)
        assertEquals 'number of gross claim for company venus re', 1, companyVenusRe.outClaimsGross.size()
        assertEquals 'number of gross claim for primary insurer venus re', 1, companyVenusRe.outClaimsGrossPrimaryInsurer.size()
        assertEquals 'number of gross claim for reinsurer venus re', 1, companyVenusRe.outClaimsGrossReinsurer.size()
        assertEquals 'number of net claim for primary insurer venus re', 1, companyVenusRe.outClaimsNetPrimaryInsurer.size()
        assertEquals('correct aggregated gross claim for company venus re', claimG100V.ultimate + claimG200V.ultimate + 0.3d * claimC150M.ultimate,
                companyVenusRe.outClaimsGross[0].ultimate)
        assertEquals('correct aggregated gross claim for primary insurer venus re', claimG100V.ultimate + claimG200V.ultimate,
                companyVenusRe.outClaimsGrossPrimaryInsurer[0].ultimate)
        assertEquals('correct aggregated gross claim for reinsurer venus re', 0.3d * claimC150M.ultimate,
                companyVenusRe.outClaimsGrossReinsurer[0].ultimate)
        assertEquals 'number of ceded claim for company venus re', 1, companyVenusRe.outClaimsCeded.size()
        assertEquals('correct aggregate ceded claim for company venus re', claimC50V.ultimate + claimC180V.ultimate,
                companyVenusRe.outClaimsCeded[0].ultimate)
        assertEquals 'number of net claim for company venus re', 1, companyVenusRe.outClaimsNet.size()
        assertEquals('correct aggregate net claim for company venus re',
                claimG100V.ultimate + claimG200V.ultimate + 0.3d * claimC150M.ultimate - (claimC50V.ultimate + claimC180V.ultimate),
                companyVenusRe.outClaimsNet[0].ultimate)
        assertEquals('correct aggregate net claim for primary insurer venus re',
                claimG100V.ultimate + claimG200V.ultimate - (claimC50V.ultimate + claimC180V.ultimate),
                companyVenusRe.outClaimsNetPrimaryInsurer[0].ultimate)


        companyMarsRe.inClaimsGross << claimG100V << claimG500M << claimG200V << claimG600P << claimG300M
        companyMarsRe.inClaimsCeded << claimC50V << claimC200M << claimC180V << claimC150M
        companyMarsRe.doCalculation(Company.PHASE_AGGREGATION)
        assertEquals 'number of gross claims for company mars re', 1, companyMarsRe.outClaimsGross.size()
        assertEquals 'number of gross claims for primary insurer mars re', 1, companyMarsRe.outClaimsGrossPrimaryInsurer.size()
        assertEquals 'number of gross claims for reinsurer mars re', 1, companyMarsRe.outClaimsGrossReinsurer.size()
        assertEquals 'number of net claims for primary insurer mars re', 1, companyMarsRe.outClaimsNetPrimaryInsurer.size()
        assertEquals('correct aggregate gross claim for company mars re',
                claimG500M.ultimate + claimC50V.ultimate + claimC180V.ultimate + claimG300M.ultimate, companyMarsRe.outClaimsGross[0].ultimate)
        assertEquals('correct aggregate gross claim for primary insurer mars re',
                claimG500M.ultimate + claimG300M.ultimate, companyMarsRe.outClaimsGrossPrimaryInsurer[0].ultimate)
        assertEquals('correct aggregate gross claim for reinsurer mars re',
                claimC50V.ultimate + claimC180V.ultimate, companyMarsRe.outClaimsGrossReinsurer[0].ultimate)
        assertEquals 'number of ceded claims for company mars re', 1, companyMarsRe.outClaimsCeded.size()
        assertEquals('correct aggregate ceded claim for company mars re', claimC200M.ultimate + claimC150M.ultimate,
                companyMarsRe.outClaimsCeded[0].ultimate)
        assertEquals('correct aggregate net claim for company mars re',
                claimG500M.ultimate + claimC50V.ultimate + claimC180V.ultimate + claimG300M.ultimate - claimC200M.ultimate - claimC150M.ultimate,
                companyMarsRe.outClaimsNet[0].ultimate)
        assertEquals('correct aggregate net claim for primary insurer mars re',
                claimG500M.ultimate + claimG300M.ultimate - claimC200M.ultimate - claimC150M.ultimate,
                companyMarsRe.outClaimsNetPrimaryInsurer[0].ultimate)


        companyPlutoRe.inClaimsGross << claimG100V << claimG500M << claimG200V << claimG600P << claimG300M
        companyPlutoRe.inClaimsCeded << claimC50V << claimC200M << claimC180V << claimC150M
        companyPlutoRe.doCalculation(Company.PHASE_AGGREGATION)
        assertEquals 'number of gross claims for company Pluto re', 1, companyPlutoRe.outClaimsGross.size()
        assertEquals 'number of gross claims for primary insurer Pluto re', 1, companyPlutoRe.outClaimsGrossPrimaryInsurer.size()
        assertEquals 'number of gross claims for reinsurer Pluto re', 1, companyPlutoRe.outClaimsGrossReinsurer.size()
        assertEquals 'number of net claims for primary insurer Pluto re', 1, companyPlutoRe.outClaimsNetPrimaryInsurer.size()
        assertEquals('correct aggregate gross claim for company Pluto re',
                claimG600P.ultimate + 0.5d * claimC150M.ultimate, companyPlutoRe.outClaimsGross[0].ultimate)
        assertEquals('correct aggregate gross claim for primary insurer Pluto re',
                claimG600P.ultimate, companyPlutoRe.outClaimsGrossPrimaryInsurer[0].ultimate)
        assertEquals('correct aggregate gross claim for reinsurer Pluto re',
                0.5d * claimC150M.ultimate, companyPlutoRe.outClaimsGrossReinsurer[0].ultimate)
        assertEquals 'number of ceded claims for company pluto re', 1, companyPlutoRe.outClaimsCeded.size()
        assertEquals('correct aggregate ceded claim for company pluto re', 0d,
                companyPlutoRe.outClaimsCeded[0].ultimate)
        assertEquals 'number of net claims for company Pluto re', 1, companyPlutoRe.outClaimsNet.size()
        assertEquals('correct aggregate net claim for companypluto re', claimG600P.ultimate + 0.5d * claimC150M.ultimate,
                companyPlutoRe.outClaimsNet[0].ultimate)
        assertEquals('correct aggregate net claim for primary insurer pluto re', claimG600P.ultimate,
                companyPlutoRe.outClaimsNetPrimaryInsurer[0].ultimate)
    }

    void testUnderwritingInfo() {
        parmCompanyVenusRe.selectedComponent = companyVenusRe
        parmCompanyMarsRe.selectedComponent = companyMarsRe
        parmCompanyPlutoRe.selectedComponent = companyPlutoRe

        // inUnderwritingInfoGross
        UnderwritingInfo uwInfo1 = new UnderwritingInfo(premiumWritten: 10000, sumInsured: 30000, numberOfPolicies: 3000,
                lineOfBusiness: motorVenusRe, commission: 10)
        UnderwritingInfo uwInfo2 = new UnderwritingInfo(premiumWritten: 20000, sumInsured: 50000, numberOfPolicies: 4000,
                lineOfBusiness: motorMarsRe)
        UnderwritingInfo uwInfo3 = new UnderwritingInfo(premiumWritten: 5000, sumInsured: 10000, numberOfPolicies: 2000,
                lineOfBusiness: motorVenusRe)
        UnderwritingInfo uwInfo4 = new UnderwritingInfo(premiumWritten: 50000, sumInsured: 8000, numberOfPolicies: 15000,
                lineOfBusiness: motorPlutoRe)
        UnderwritingInfo uwInfo8 = new UnderwritingInfo(premiumWritten: 1800000, sumInsured: 200000,
                numberOfPolicies: 30000, lineOfBusiness: accidentMarsRe)

        // inUnderwritingInfoCeded
        UnderwritingInfo uwInfo5 = new UnderwritingInfo(premiumWritten: 1000, sumInsured: 3000, numberOfPolicies: 3000,
                lineOfBusiness: motorVenusRe, reinsuranceContract: motorVenusReQuotaShare, commission: -200)
        UnderwritingInfo uwInfo6 = new UnderwritingInfo(premiumWritten: 2000, lineOfBusiness: motorMarsRe, commission: -100)
        UnderwritingInfo uwInfo7 = new UnderwritingInfo(premiumWritten: 2500, sumInsured: 5000, numberOfPolicies: 2000,
                lineOfBusiness: motorVenusRe, reinsuranceContract: motorVenusReQuotaShare, commission: -300)
        UnderwritingInfo uwInfo9 = new UnderwritingInfo(premiumWritten: 18000, sumInsured: 3000,
                numberOfPolicies: 20000, lineOfBusiness: accidentMarsRe, reinsuranceContract: accidentMarsReStopLoss, commission: -100)

        //Venus Re
        companyVenusRe.inUnderwritingInfoGross << uwInfo1 << uwInfo2 << uwInfo3 << uwInfo4 << uwInfo8
        companyVenusRe.inUnderwritingInfoCeded << uwInfo5 << uwInfo6 << uwInfo7 << uwInfo9
        companyVenusRe.doCalculation(Company.PHASE_AGGREGATION)
        assertEquals 'number of gross underwriting Info for company venus re', 1, companyVenusRe.outUnderwritingInfoGross.size()
        assertEquals 'number of gross underwriting Info for primary insurer venus re', 1, companyVenusRe.outUnderwritingInfoGrossPrimaryInsurer.size()
        assertEquals 'number of gross underwriting Info for reinsurer venus re', 1, companyVenusRe.outUnderwritingInfoGrossReinsurer.size()
        assertEquals 'number of net underwriting Info for primary insurer venus re', 1, companyVenusRe.outUnderwritingInfoNetPrimaryInsurer.size()
        assertEquals('correct aggregated gross premium written for company venus re',
                uwInfo1.premiumWritten + uwInfo3.premiumWritten + 0.3d * uwInfo9.premiumWritten,
                companyVenusRe.outUnderwritingInfoGross[0].premiumWritten)
        assertEquals('correct aggregated gross premium written for primary insurer venus re',
                uwInfo1.premiumWritten + uwInfo3.premiumWritten,
                companyVenusRe.outUnderwritingInfoGrossPrimaryInsurer[0].premiumWritten)
        assertEquals('correct aggregated gross premium written for reinsurer venus re', 0.3d * uwInfo9.premiumWritten,
                companyVenusRe.outUnderwritingInfoGrossReinsurer[0].premiumWritten)
        assertEquals('correct aggregated commission in gross UI for company venus re',
                uwInfo1.commission + uwInfo3.commission + 0.3d * uwInfo9.commission,
                companyVenusRe.outUnderwritingInfoGross[0].commission)
        assertEquals('correct aggregated commission in gross UI for primary insurer venus re',
                uwInfo1.commission + uwInfo3.commission,
                companyVenusRe.outUnderwritingInfoGrossPrimaryInsurer[0].commission)
        assertEquals('correct aggregated commission in gross UI for reinsurer venus re', 0.3d * uwInfo9.commission,
                companyVenusRe.outUnderwritingInfoGrossReinsurer[0].commission)
        assertEquals('correct aggregated gross sum Insured for company venus re',
                (uwInfo1.sumInsured * uwInfo1.numberOfPolicies + uwInfo3.sumInsured * uwInfo3.numberOfPolicies + 0.3d * uwInfo9.sumInsured * uwInfo9.numberOfPolicies) / (uwInfo9.numberOfPolicies + uwInfo1.numberOfPolicies + uwInfo3.numberOfPolicies),
                companyVenusRe.outUnderwritingInfoGross[0].sumInsured)
        assertEquals('correct aggregated gross sum Insured for primary insurer venus re',
                (uwInfo1.sumInsured * uwInfo1.numberOfPolicies + uwInfo3.sumInsured * uwInfo3.numberOfPolicies) / (uwInfo1.numberOfPolicies + uwInfo3.numberOfPolicies),
                companyVenusRe.outUnderwritingInfoGrossPrimaryInsurer[0].sumInsured)
        assertEquals('correct aggregated gross sum Insured for reinsurer venus re', 0.3d * uwInfo9.sumInsured,
                companyVenusRe.outUnderwritingInfoGrossReinsurer[0].sumInsured)
        assertEquals('correct aggregated gross no of policies for company venus re',
                uwInfo1.numberOfPolicies + uwInfo3.numberOfPolicies + uwInfo9.numberOfPolicies,
                companyVenusRe.outUnderwritingInfoGross[0].numberOfPolicies)
        assertEquals('correct aggregated gross no of policies for primary insurer venus re',
                uwInfo1.numberOfPolicies + uwInfo3.numberOfPolicies,
                companyVenusRe.outUnderwritingInfoGrossPrimaryInsurer[0].numberOfPolicies)
        assertEquals('correct aggregated gross no of policies for reinsurer venus re',
                uwInfo9.numberOfPolicies, companyVenusRe.outUnderwritingInfoGrossReinsurer[0].numberOfPolicies)
        assertEquals 'number of ceded underwriting Info for company venus re', 1, companyVenusRe.outUnderwritingInfoCeded.size()
        assertEquals('correct aggregated ceded premium written for company venus re', uwInfo5.premiumWritten + uwInfo7.premiumWritten,
                companyVenusRe.outUnderwritingInfoCeded[0].premiumWritten)
        assertEquals('correct aggregated (ceded) commission for company venus re', uwInfo5.commission + uwInfo7.commission,
                companyVenusRe.outUnderwritingInfoCeded[0].commission)
        assertEquals('correct aggregated ceded sum Insured for company venus re',
                (uwInfo5.sumInsured * uwInfo5.numberOfPolicies + uwInfo7.sumInsured * uwInfo7.numberOfPolicies) / (uwInfo5.numberOfPolicies + uwInfo7.numberOfPolicies),
                companyVenusRe.outUnderwritingInfoCeded[0].sumInsured)
        assertEquals('correct aggregated ceded no of policies for company venus re',
                uwInfo5.numberOfPolicies + uwInfo7.numberOfPolicies, companyVenusRe.outUnderwritingInfoCeded[0].numberOfPolicies)
        assertEquals 'number of net underwriting Info for company venus re', 1, companyVenusRe.outUnderwritingInfoNet.size()
        assertEquals('correct aggregated net premium written for company venus re',
                uwInfo1.premiumWritten + uwInfo3.premiumWritten + 0.3d * uwInfo9.premiumWritten - (uwInfo5.premiumWritten + uwInfo7.premiumWritten),
                companyVenusRe.outUnderwritingInfoNet[0].premiumWritten)
        assertEquals('correct aggregated net premium written for primary insurer venus re',
                uwInfo1.premiumWritten + uwInfo3.premiumWritten - (uwInfo5.premiumWritten + uwInfo7.premiumWritten),
                companyVenusRe.outUnderwritingInfoNetPrimaryInsurer[0].premiumWritten)
        assertEquals('correct aggregated commission in net UI for company venus re',
                uwInfo1.commission + uwInfo3.commission + 0.3d * uwInfo9.commission - (uwInfo5.commission + uwInfo7.commission),
                companyVenusRe.outUnderwritingInfoNet[0].commission)
        assertEquals('correct aggregated commission in net UI written for primary insurer venus re',
                uwInfo1.commission + uwInfo3.commission - (uwInfo5.commission + uwInfo7.commission),
                companyVenusRe.outUnderwritingInfoNetPrimaryInsurer[0].commission)
        assertEquals('correct aggregated net no of policies for company venus re',
                uwInfo1.numberOfPolicies + uwInfo3.numberOfPolicies + uwInfo9.numberOfPolicies, companyVenusRe.outUnderwritingInfoNet[0].numberOfPolicies)
        assertEquals('correct aggregated net no of policies for primary insurer venus re',
                uwInfo1.numberOfPolicies + uwInfo3.numberOfPolicies, companyVenusRe.outUnderwritingInfoNetPrimaryInsurer[0].numberOfPolicies)
        assertEquals('correct aggregated net sum Insured for company venus re',
                (uwInfo1.sumInsured * uwInfo1.numberOfPolicies + uwInfo3.sumInsured * uwInfo3.numberOfPolicies + 0.3d * uwInfo9.sumInsured * uwInfo9.numberOfPolicies - (uwInfo5.sumInsured * uwInfo5.numberOfPolicies + uwInfo7.sumInsured * uwInfo7.numberOfPolicies)) / (uwInfo9.numberOfPolicies + uwInfo1.numberOfPolicies + uwInfo3.numberOfPolicies),
                companyVenusRe.outUnderwritingInfoNet[0].sumInsured)
        assertEquals('correct aggregated net sum Insured for primary insurer venus re',
                (uwInfo1.sumInsured * uwInfo1.numberOfPolicies + uwInfo3.sumInsured * uwInfo3.numberOfPolicies - (uwInfo5.sumInsured * uwInfo5.numberOfPolicies + uwInfo7.sumInsured * uwInfo7.numberOfPolicies)) / (uwInfo1.numberOfPolicies + uwInfo3.numberOfPolicies),
                companyVenusRe.outUnderwritingInfoNetPrimaryInsurer[0].sumInsured)

        // Mars Re
        companyMarsRe.inUnderwritingInfoGross << uwInfo1 << uwInfo2 << uwInfo3 << uwInfo4 << uwInfo8
        companyMarsRe.inUnderwritingInfoCeded << uwInfo5 << uwInfo6 << uwInfo7 << uwInfo9
        companyMarsRe.doCalculation(Company.PHASE_AGGREGATION)
        assertEquals 'number of gross underwriting Info for company mars re', 1, companyMarsRe.outUnderwritingInfoGross.size()
        assertEquals 'number of gross underwriting Info for primary insurer mars re', 1, companyMarsRe.outUnderwritingInfoGrossPrimaryInsurer.size()
        assertEquals 'number of gross underwriting Info for reinsurer mars re', 1, companyMarsRe.outUnderwritingInfoGrossReinsurer.size()
        assertEquals 'number of net underwriting Info for primary insurer mars re', 1, companyMarsRe.outUnderwritingInfoNetPrimaryInsurer.size()
        assertEquals('correct aggregated gross premium written for company mars re',
                uwInfo2.premiumWritten + uwInfo5.premiumWritten + uwInfo7.premiumWritten + uwInfo8.premiumWritten,
                companyMarsRe.outUnderwritingInfoGross[0].premiumWritten)
        assertEquals('correct aggregated gross premium written for primary insurer mars re',
                uwInfo2.premiumWritten + uwInfo8.premiumWritten,
                companyMarsRe.outUnderwritingInfoGrossPrimaryInsurer[0].premiumWritten)
        assertEquals('correct aggregated gross premium written for reinsurer mars re',
                uwInfo5.premiumWritten + uwInfo7.premiumWritten, companyMarsRe.outUnderwritingInfoGrossReinsurer[0].premiumWritten)
        assertEquals('correct aggregated commission in gross UI for company mars re',
                uwInfo2.commission + uwInfo8.commission + uwInfo5.commission + uwInfo7.commission,
                companyMarsRe.outUnderwritingInfoGross[0].commission)
        assertEquals('correct aggregated commission in gross UI for primary insurer mars re',
                uwInfo2.commission + uwInfo8.commission, companyMarsRe.outUnderwritingInfoGrossPrimaryInsurer[0].commission)
        assertEquals('correct aggregated commission in gross UI for reinsurer mars re', uwInfo5.commission + uwInfo7.commission,
                companyMarsRe.outUnderwritingInfoGrossReinsurer[0].commission)
        assertEquals('correct aggregated gross number of policies for company mars re',
                uwInfo2.numberOfPolicies + uwInfo5.numberOfPolicies + uwInfo7.numberOfPolicies + uwInfo8.numberOfPolicies,
                companyMarsRe.outUnderwritingInfoGross[0].numberOfPolicies)
        assertEquals('correct aggregated gross number of policies for primary insurer mars re',
                uwInfo2.numberOfPolicies + uwInfo8.numberOfPolicies,
                companyMarsRe.outUnderwritingInfoGrossPrimaryInsurer[0].numberOfPolicies)
        assertEquals('correct aggregated gross number of policies for reinsurer mars re',
                uwInfo5.numberOfPolicies + uwInfo7.numberOfPolicies,
                companyMarsRe.outUnderwritingInfoGrossReinsurer[0].numberOfPolicies)
        assertEquals('correct aggregated gross sum Insured for company mars re',
                (uwInfo2.sumInsured * uwInfo2.numberOfPolicies + uwInfo5.sumInsured * uwInfo5.numberOfPolicies + uwInfo7.sumInsured * uwInfo7.numberOfPolicies + uwInfo8.sumInsured * uwInfo8.numberOfPolicies) / (uwInfo2.numberOfPolicies + uwInfo5.numberOfPolicies + uwInfo7.numberOfPolicies + uwInfo8.numberOfPolicies),
                companyMarsRe.outUnderwritingInfoGross[0].sumInsured)
        assertEquals('correct aggregated gross sum Insured for primary insurer mars re',
                (uwInfo2.sumInsured * uwInfo2.numberOfPolicies + uwInfo8.sumInsured * uwInfo8.numberOfPolicies) / (uwInfo2.numberOfPolicies + uwInfo8.numberOfPolicies),
                companyMarsRe.outUnderwritingInfoGrossPrimaryInsurer[0].sumInsured)
        assertEquals('correct aggregated gross sum Insured for reinsurer mars re',
                (uwInfo5.sumInsured * uwInfo5.numberOfPolicies + uwInfo7.sumInsured * uwInfo7.numberOfPolicies) / (uwInfo5.numberOfPolicies + uwInfo7.numberOfPolicies),
                companyMarsRe.outUnderwritingInfoGrossReinsurer[0].sumInsured)
        assertEquals 'number of ceded underwriting Info for company mars re', 1, companyMarsRe.outUnderwritingInfoCeded.size()
        assertEquals('correct aggregated ceded premium written for company mars re', uwInfo6.premiumWritten + uwInfo9.premiumWritten,
                companyMarsRe.outUnderwritingInfoCeded[0].premiumWritten)
        assertEquals('correct aggregated (ceded) commission for company mars re', uwInfo6.commission + uwInfo9.commission,
                companyMarsRe.outUnderwritingInfoCeded[0].commission)
        assertEquals('correct aggregated ceded number of policies for company mars re', uwInfo6.numberOfPolicies + uwInfo9.numberOfPolicies,
                companyMarsRe.outUnderwritingInfoCeded[0].numberOfPolicies)
        assertEquals('correct aggregated ceded sum Insured for company mars re',
                (uwInfo6.sumInsured * uwInfo6.numberOfPolicies + uwInfo9.sumInsured * uwInfo9.numberOfPolicies) / (uwInfo6.numberOfPolicies + uwInfo9.numberOfPolicies),
                companyMarsRe.outUnderwritingInfoCeded[0].sumInsured)
        assertEquals 'number of net underwriting Info for company mars re', 1, companyMarsRe.outUnderwritingInfoNet.size()
        assertEquals('correct aggregated net premium written for company mars re',
                uwInfo2.premiumWritten + uwInfo5.premiumWritten + uwInfo7.premiumWritten + uwInfo8.premiumWritten - uwInfo6.premiumWritten - uwInfo9.premiumWritten,
                companyMarsRe.outUnderwritingInfoNet[0].premiumWritten)
        assertEquals('correct aggregated net premium written for primary insurer mars re',
                uwInfo2.premiumWritten + uwInfo8.premiumWritten - uwInfo6.premiumWritten - uwInfo9.premiumWritten,
                companyMarsRe.outUnderwritingInfoNetPrimaryInsurer[0].premiumWritten)
        assertEquals('correct aggregated commission in net UI for company mars re',
                uwInfo2.commission + uwInfo8.commission - (uwInfo6.commission + uwInfo9.commission) + uwInfo5.commission + uwInfo7.commission,
                companyMarsRe.outUnderwritingInfoNet[0].commission)
        assertEquals('correct aggregated commission in net UI written for primary insurer mars re',
                uwInfo2.commission + uwInfo8.commission - (uwInfo6.commission + uwInfo9.commission),
                companyMarsRe.outUnderwritingInfoNetPrimaryInsurer[0].commission)
        assertEquals('correct aggregated net number of policies for company mars re',
                uwInfo2.numberOfPolicies + uwInfo5.numberOfPolicies + uwInfo7.numberOfPolicies + uwInfo8.numberOfPolicies,
                companyMarsRe.outUnderwritingInfoNet[0].numberOfPolicies)
        assertEquals('correct aggregated net number of policies for primary insurer mars re',
                uwInfo2.numberOfPolicies + uwInfo8.numberOfPolicies,
                companyMarsRe.outUnderwritingInfoNetPrimaryInsurer[0].numberOfPolicies)
        assertEquals('correct aggregated net sum Insured for company mars re',
                (uwInfo2.sumInsured * uwInfo2.numberOfPolicies + uwInfo5.sumInsured * uwInfo5.numberOfPolicies + uwInfo7.sumInsured * uwInfo7.numberOfPolicies + uwInfo8.sumInsured * uwInfo8.numberOfPolicies - uwInfo6.sumInsured * uwInfo6.numberOfPolicies - uwInfo9.sumInsured * uwInfo9.numberOfPolicies) / (uwInfo8.numberOfPolicies + uwInfo2.numberOfPolicies + uwInfo5.numberOfPolicies + uwInfo7.numberOfPolicies),
                companyMarsRe.outUnderwritingInfoNet[0].sumInsured)
        assertEquals('correct aggregated net sum Insured for primary insurer mars re',
                (uwInfo2.sumInsured * uwInfo2.numberOfPolicies + uwInfo8.sumInsured * uwInfo8.numberOfPolicies - uwInfo6.sumInsured * uwInfo6.numberOfPolicies - uwInfo9.sumInsured * uwInfo9.numberOfPolicies) / (uwInfo8.numberOfPolicies + uwInfo2.numberOfPolicies),
                companyMarsRe.outUnderwritingInfoNetPrimaryInsurer[0].sumInsured)

        // pluto re
        companyPlutoRe.inUnderwritingInfoGross << uwInfo1 << uwInfo2 << uwInfo3 << uwInfo4 << uwInfo8
        companyPlutoRe.inUnderwritingInfoCeded << uwInfo5 << uwInfo6 << uwInfo7 << uwInfo9
        companyPlutoRe.doCalculation(Company.PHASE_AGGREGATION)
        assertEquals 'number of gross underwriting Info for company pluto re', 1, companyPlutoRe.outUnderwritingInfoGross.size()
        assertEquals 'number of gross underwriting Info for primary insurer pluto re', 1, companyPlutoRe.outUnderwritingInfoGrossPrimaryInsurer.size()
        assertEquals 'number of gross underwriting Info for reinsurer pluto re', 1, companyPlutoRe.outUnderwritingInfoGrossReinsurer.size()
        assertEquals 'number of net underwriting Info for primary insurer pluto re', 1, companyPlutoRe.outUnderwritingInfoNetPrimaryInsurer.size()
        assertEquals('correct aggregated gross premium written for company pluto re',
                uwInfo4.premiumWritten + 0.5d * uwInfo9.premiumWritten,
                companyPlutoRe.outUnderwritingInfoGross[0].premiumWritten)
        assertEquals('correct aggregated gross premium written for primary insurer pluto re',
                uwInfo4.premiumWritten, companyPlutoRe.outUnderwritingInfoGrossPrimaryInsurer[0].premiumWritten)
        assertEquals('correct aggregated gross premium written for reinsurer pluto re',
                0.5d * uwInfo9.premiumWritten, companyPlutoRe.outUnderwritingInfoGrossReinsurer[0].premiumWritten)
        assertEquals('correct aggregated commission in gross UI for company pluto re',
                uwInfo4.commission + 0.5d * uwInfo9.commission, companyPlutoRe.outUnderwritingInfoGross[0].commission)
        assertEquals('correct aggregated commission in gross UI for primary insurer pluto re',
                uwInfo4.commission, companyPlutoRe.outUnderwritingInfoGrossPrimaryInsurer[0].commission)
        assertEquals('correct aggregated commission in gross UI for reinsurer pluto re', 0.5d * uwInfo9.commission,
                companyPlutoRe.outUnderwritingInfoGrossReinsurer[0].commission)
        assertEquals('correct aggregated gross sum Insured for company pluto re',
                (uwInfo4.sumInsured * uwInfo4.numberOfPolicies + 0.5d * uwInfo9.sumInsured * uwInfo9.numberOfPolicies) / (uwInfo4.numberOfPolicies + uwInfo9.numberOfPolicies),
                companyPlutoRe.outUnderwritingInfoGross[0].sumInsured)
        assertEquals('correct aggregated gross sum Insured for primary insurer pluto re', uwInfo4.sumInsured,
                companyPlutoRe.outUnderwritingInfoGrossPrimaryInsurer[0].sumInsured)
        assertEquals('correct aggregated gross sum Insured for reinsurer pluto re',
                0.5d * uwInfo9.sumInsured, companyPlutoRe.outUnderwritingInfoGrossReinsurer[0].sumInsured)
        assertEquals('correct aggregated gross no of policies for company pluto re',
                uwInfo4.numberOfPolicies + uwInfo9.numberOfPolicies,
                companyPlutoRe.outUnderwritingInfoGross[0].numberOfPolicies)
        assertEquals('correct aggregated gross no of policies for primary insurer pluto re',
                uwInfo4.numberOfPolicies, companyPlutoRe.outUnderwritingInfoGrossPrimaryInsurer[0].numberOfPolicies)
        assertEquals('correct aggregated gross no of policies for reinsurer pluto re',
                uwInfo9.numberOfPolicies, companyPlutoRe.outUnderwritingInfoGrossReinsurer[0].numberOfPolicies)
        assertEquals 'number of ceded underwriting Info for company pluto re', 1, companyPlutoRe.outUnderwritingInfoCeded.size()
        assertEquals('correct aggregated ceded premium written for company pluto re', 0, companyPlutoRe.outUnderwritingInfoCeded[0].premiumWritten)
        assertEquals('correct aggregated (ceded) commission for company pluto re', 0, companyPlutoRe.outUnderwritingInfoCeded[0].commission)
        assertEquals('correct aggregated ceded sum Insured for company pluto re', 0, companyPlutoRe.outUnderwritingInfoCeded[0].sumInsured)
        assertEquals('correct aggregated ceded no of policies for company pluto re',
                0, companyPlutoRe.outUnderwritingInfoCeded[0].numberOfPolicies)
        assertEquals 'number of net underwriting Info for company pluto re', 1, companyPlutoRe.outUnderwritingInfoNet.size()
        assertEquals('correct aggregated net premium written for company pluto re', uwInfo4.premiumWritten + 0.5d * uwInfo9.premiumWritten,
                companyPlutoRe.outUnderwritingInfoNet[0].premiumWritten)
        assertEquals('correct aggregated net premium written for primary insurer  pluto re', uwInfo4.premiumWritten,
                companyPlutoRe.outUnderwritingInfoNetPrimaryInsurer[0].premiumWritten)
        assertEquals('correct aggregated commission in net UI for company pluto re',
                uwInfo4.commission + 0.5d * uwInfo9.commission,
                companyPlutoRe.outUnderwritingInfoNet[0].commission)
        assertEquals('correct aggregated commission in net UI written for primary insurer venus re',
                uwInfo4.commission, companyPlutoRe.outUnderwritingInfoNetPrimaryInsurer[0].commission)
        assertEquals('correct aggregated net no of policies for company pluto re', uwInfo4.numberOfPolicies + uwInfo9.numberOfPolicies,
                companyPlutoRe.outUnderwritingInfoNet[0].numberOfPolicies)
        assertEquals('correct aggregated net no of policies for primary insurer pluto re', uwInfo4.numberOfPolicies,
                companyPlutoRe.outUnderwritingInfoNetPrimaryInsurer[0].numberOfPolicies)
        assertEquals('correct aggregated net sum Insured for company pluto re',
                (uwInfo4.sumInsured * uwInfo4.numberOfPolicies + 0.5d * uwInfo9.sumInsured * uwInfo9.numberOfPolicies) / (uwInfo4.numberOfPolicies + uwInfo9.numberOfPolicies),
                companyPlutoRe.outUnderwritingInfoNet[0].sumInsured)
        assertEquals('correct aggregated net sum Insured for primary insurer pluto re',
                uwInfo4.sumInsured,
                companyPlutoRe.outUnderwritingInfoNetPrimaryInsurer[0].sumInsured)

    }

    void testFinacialResults() {

        parmCompanyVenusRe.selectedComponent = companyVenusRe
        parmCompanyMarsRe.selectedComponent = companyMarsRe
        parmCompanyPlutoRe.selectedComponent = companyPlutoRe

        // FinancialResults
        Claim almResult100V = new Claim(ultimate: 100, origin: loanVenusRe)
        Claim almResult500M = new Claim(ultimate: 500, origin: loanMarsRe)
        Claim almResult200V = new Claim(ultimate: 200, origin: loanVenusRe)
        Claim almResult600P = new Claim(ultimate: 600, origin: loanPlutoRe)
        Claim almResult300M = new Claim(ultimate: 300, origin: loan2MarsRe)


        companyVenusRe.inFinancialResults << almResult100V << almResult500M << almResult200V << almResult600P << almResult300M
        companyVenusRe.doCalculation(Company.PHASE_AGGREGATION)
        assertEquals 'number of alm results for company venus re', 1, companyVenusRe.outFinancialResults.size()
        assertEquals('correct aggregated financial result for company venus re', almResult100V.ultimate + almResult200V.ultimate,
                companyVenusRe.outFinancialResults[0].ultimate)


        companyMarsRe.inFinancialResults << almResult100V << almResult500M << almResult200V << almResult600P << almResult300M
        companyMarsRe.doCalculation(Company.PHASE_AGGREGATION)
        assertEquals 'number of alm results for company mars re', 1, companyMarsRe.outFinancialResults.size()
        assertEquals('correct aggregate financial result for company mars re',
                almResult500M.ultimate + almResult300M.ultimate, companyMarsRe.outFinancialResults[0].ultimate)


        companyPlutoRe.inFinancialResults << almResult100V << almResult500M << almResult200V << almResult600P << almResult300M
        companyPlutoRe.doCalculation(Company.PHASE_AGGREGATION)
        assertEquals 'number of alm results for company Pluto re', 1, companyPlutoRe.outFinancialResults.size()
        assertEquals('correct aggregate financial result for company Pluto re',
                almResult600P.ultimate, companyPlutoRe.outFinancialResults[0].ultimate)

    }

}

