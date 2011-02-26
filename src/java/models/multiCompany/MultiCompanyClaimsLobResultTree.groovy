package models.multiCompany

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = MultiCompanyModel
displayName = "Claims by Segments"
language = "en"

mappings = {
    "MultiCompany" {
         "claims" {
            "paid" "MultiCompany:linesOfBusiness:outClaimsDevelopmentLeanNet:paid", {
                "[%lineOfBusiness%]" "MultiCompany:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanNet:paid", {
                    "gross" "MultiCompany:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanGross:paid", {
                        "[%claimsGenerator%]" "MultiCompany:linesOfBusiness:[%lineOfBusiness%]:claimsGenerators:[%claimsGenerator%]:outClaimsDevelopmentLeanGross:paid"
                    }
                    "ceded" "MultiCompany:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanCeded:paid", {
                        "[%contract%]" "MultiCompany:linesOfBusiness:[%lineOfBusiness%]:reinsuranceContracts:[%contract%]:outClaimsDevelopmentLeanCeded:paid"
                    }
                }
            }
            "incurred" "MultiCompany:linesOfBusiness:outClaimsDevelopmentLeanNet:incurred", {
                "[%lineOfBusiness%]" "MultiCompany:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanNet:incurred", {
                    "gross" "MultiCompany:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanGross:incurred", {
                        "[%claimsGenerator%]" "MultiCompany:linesOfBusiness:[%lineOfBusiness%]:claimsGenerators:[%claimsGenerator%]:outClaimsDevelopmentLeanGross:incurred"
                    }
                    "ceded" "MultiCompany:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanCeded:incurred", {
                        "[%contract%]" "MultiCompany:linesOfBusiness:[%lineOfBusiness%]:reinsuranceContracts:[%contract%]:outClaimsDevelopmentLeanCeded:incurred"
                    }
                }
            }
            "reserved" "MultiCompany:linesOfBusiness:outClaimsDevelopmentLeanNet:reserved", {
                "[%lineOfBusiness%]" "MultiCompany:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanNet:reserved", {
                    "gross" "MultiCompany:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanGross:reserved", {
                        "[%claimsGenerator%]" "MultiCompany:linesOfBusiness:[%lineOfBusiness%]:claimsGenerators:[%claimsGenerator%]:outClaimsDevelopmentLeanGross:reserved"
                    }
                    "ceded" "MultiCompany:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanCeded:reserved", {
                        "[%contract%]" "MultiCompany:linesOfBusiness:[%lineOfBusiness%]:reinsuranceContracts:[%contract%]:outClaimsDevelopmentLeanCeded:reserved"
                    }
                }
            }
        }
    }
}