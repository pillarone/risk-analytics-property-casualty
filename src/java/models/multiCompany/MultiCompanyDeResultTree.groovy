package models.multiCompany

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = MultiCompanyModel
displayName = "Firmen"
language = "de"
mappings = [
        "Gruppen Modell:[%company%]": "MultiCompany:companies:[%company%]:outFinancialResults:ultimate",
        "Gruppen Modell:[%company%]:Netto:Prämie": "MultiCompany:companies:[%company%]:outUnderwritingInfoNet:premium",
        "Gruppen Modell:[%company%]:Netto:Provision": "MultiCompany:companies:[%company%]:outUnderwritingInfoNet:commission",
        "Gruppen Modell:[%company%]:Netto:Schäden bezahlt": "MultiCompany:companies:[%company%]:outClaimsLeanDevelopmentNet:paid",
        "Gruppen Modell:[%company%]:Brutto:Prämie": "MultiCompany:companies:[%company%]:outUnderwritingInfoGross:premium",
        "Gruppen Modell:[%company%]:Brutto:Provision": "MultiCompany:companies:[%company%]:outUnderwritingInfoGross:commission",
        "Gruppen Modell:[%company%]:Brutto:Schäden bezahlt": "MultiCompany:companies:[%company%]:outClaimsLeanDevelopmentGross:paid",
        "Gruppen Modell:[%company%]:Zediert:Prämie": "MultiCompany:companies:[%company%]:outUnderwritingInfoCeded:premium",
        "Gruppen Modell:[%company%]:Zediert:Provision": "MultiCompany:companies:[%company%]:outUnderwritingInfoCeded:commission",
        "Gruppen Modell:[%company%]:Zediert:Schäden bezahlt": "MultiCompany:companies:[%company%]:outClaimsLeanDevelopmentCeded:paid",
]