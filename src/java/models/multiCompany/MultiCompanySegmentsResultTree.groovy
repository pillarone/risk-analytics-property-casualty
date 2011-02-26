package models.multiCompany

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = MultiCompanyModel
displayName = "Segments Claims and Premium"
language = "en"

mappings = {
    "MultiCompany" {
        "claims" {
            "net" {
                "incurred" "MultiCompany:linesOfBusiness:outClaimsDevelopmentLeanNet:incurred", {
                    "[%subcomponents%]" "MultiCompany:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanNet:incurred"
                }
                "paid" "MultiCompany:linesOfBusiness:outClaimsDevelopmentLeanNet:paid", {
                    "[%subcomponents%]" "MultiCompany:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanNet:paid"
                }
                "reserved" "MultiCompany:linesOfBusiness:outClaimsDevelopmentLeanNet:reserved", {
                    "[%subcomponents%]" "MultiCompany:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanNet:reserved"
                }
            }
            "gross" {
                "incurred" "MultiCompany:linesOfBusiness:outClaimsDevelopmentLeanGross:incurred", {
                    "[%subcomponents%]" "MultiCompany:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanGross:incurred"
                }
                "paid" "MultiCompany:linesOfBusiness:outClaimsDevelopmentLeanGross:paid", {
                    "[%subcomponents%]" "MultiCompany:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanGross:paid"
                }
                "reserved" "MultiCompany:linesOfBusiness:outClaimsDevelopmentLeanGross:reserved", {
                    "[%subcomponents%]" "MultiCompany:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanGross:reserved"
                }
            }
            "ceded" {
                "incurred" "MultiCompany:linesOfBusiness:outClaimsDevelopmentLeanCeded:incurred", {
                    "[%subcomponents%]" "MultiCompany:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanCeded:incurred"
                }
                "paid" "MultiCompany:linesOfBusiness:outClaimsDevelopmentLeanCeded:paid", {
                    "[%subcomponents%]" "MultiCompany:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanCeded:paid"
                }
                "reserved" "MultiCompany:linesOfBusiness:outClaimsDevelopmentLeanCeded:reserved", {
                    "[%subcomponents%]" "MultiCompany:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanCeded:reserved"
                }
            }
        }
        "underwriting" {
            "net" {
                "premium" "MultiCompany:linesOfBusiness:outUnderwritingInfoNet:premium", {
                    "[%subcomponents%]" "MultiCompany:linesOfBusiness:[%subcomponents%]:outUnderwritingInfoNet:premium"
                }
                "commission" "MultiCompany:linesOfBusiness:outUnderwritingInfoNet:commission", {
                    "[%subcomponents%]" "MultiCompany:linesOfBusiness:[%subcomponents%]:outUnderwritingInfoNet:commission"
                }
            }
            "gross" {
                "premium" "MultiCompany:linesOfBusiness:outUnderwritingInfoGross:premium", {
                    "[%subcomponents%]" "MultiCompany:linesOfBusiness:[%subcomponents%]:outUnderwritingInfoGross:premium"
                }
            }
            "ceded" {
                "premium" "MultiCompany:linesOfBusiness:outUnderwritingInfoCeded:premium", {
                    "fixedPremium" "MultiCompany:linesOfBusiness:outUnderwritingInfoCeded:fixedPremium"
                    "variablePremium" "MultiCompany:linesOfBusiness:outUnderwritingInfoCeded:variablePremium"
                    "[%subcomponents%]" "MultiCompany:linesOfBusiness:[%subcomponents%]:outUnderwritingInfoCeded:premium", {
                        "fixedPremium" "MultiCompany:linesOfBusiness:[%subcomponents%]:outUnderwritingInfoCeded:fixedPremium"
                        "variablePremium" "MultiCompany:linesOfBusiness:[%subcomponents%]:outUnderwritingInfoCeded:variablePremium"
                    }
                }
                "commission" "MultiCompany:linesOfBusiness:outUnderwritingInfoCeded:commission", {
                    "fixedCommission" "MultiCompany:linesOfBusiness:outUnderwritingInfoCeded:fixedCommission"
                    "variableCommission" "MultiCompany:linesOfBusiness:outUnderwritingInfoCeded:variableCommission"
                    "[%subcomponents%]" "MultiCompany:linesOfBusiness:[%subcomponents%]:outUnderwritingInfoCeded:commission", {
                        "fixedCommission" "MultiCompany:linesOfBusiness:[%subcomponents%]:outUnderwritingInfoCeded:fixedCommission"
                        "variableCommission" "MultiCompany:linesOfBusiness:[%subcomponents%]:outUnderwritingInfoCeded:variableCommission"
                    }
                }
            }
        }
    }
}