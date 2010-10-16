package models.multiCompany

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = MultiCompanyModel
displayName = "Company View"
language = "en"

mappings = {
    "MultiCompany" {
        "[%company%]" "MultiCompany:companies:[%company%]:outFinancialResults:ultimate", {
            "net" {
                "premium" "MultiCompany:companies:[%company%]:outUnderwritingInfoNet:premium", {}
                "commission" "MultiCompany:companies:[%company%]:outUnderwritingInfoNet:commission", {}
                "claimsPaid" "MultiCompany:companies:[%company%]:outClaimsLeanDevelopmentNet:paid", {}
            }
            "gross" {
                "premium" "MultiCompany:companies:[%company%]:outUnderwritingInfoGross:premium", {}
                "commission" "MultiCompany:companies:[%company%]:outUnderwritingInfoGross:commission", {}
                "claimsPaid" "MultiCompany:companies:[%company%]:outClaimsLeanDevelopmentGross:paid", {}
            }
            "ceded" {
                "premium" "MultiCompany:companies:[%company%]:outUnderwritingInfoCeded:premium", {}
                "commission" "MultiCompany:companies:[%company%]:outUnderwritingInfoCeded:commission", {}
                "claimsPaid" "MultiCompany:companies:[%company%]:outClaimsLeanDevelopmentCeded:paid", {}
            }
        }
    }
}