package models.multiCompany

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = MultiCompanyModel
displayName = "de: Company View"
language = "de"
mappings = [
        "MultiCompany:[%company%]": "MultiCompany:companies:[%company%]:outFinancialResults:ultimate",
        "MultiCompany:[%company%]:net:premium": "MultiCompany:companies:[%company%]:outUnderwritingInfoNet:premium",
        "MultiCompany:[%company%]:net:commission": "MultiCompany:companies:[%company%]:outUnderwritingInfoNet:commission",
        "MultiCompany:[%company%]:net:claimsPaid": "MultiCompany:companies:[%company%]:outClaimsLeanDevelopmentNet:paid",
        "MultiCompany:[%company%]:gross:premium": "MultiCompany:companies:[%company%]:outUnderwritingInfoGross:premium",
        "MultiCompany:[%company%]:gross:commission": "MultiCompany:companies:[%company%]:outUnderwritingInfoGross:commission",
        "MultiCompany:[%company%]:gross:claimsPaid": "MultiCompany:companies:[%company%]:outClaimsLeanDevelopmentGross:paid",
        "MultiCompany:[%company%]:ceded:premium": "MultiCompany:companies:[%company%]:outUnderwritingInfoCeded:premium",
        "MultiCompany:[%company%]:ceded:commission": "MultiCompany:companies:[%company%]:outUnderwritingInfoCeded:commission",
        "MultiCompany:[%company%]:ceded:claimsPaid": "MultiCompany:companies:[%company%]:outClaimsLeanDevelopmentCeded:paid",
]