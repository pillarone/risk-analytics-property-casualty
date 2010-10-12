package models.podra

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = PodraModel
displayName = "Claims by Lines of Business (drill down)"
language = "en"

mappings = {
    Podra {
        "claims" {
            "paid" "Podra:linesOfBusiness:outClaimsDevelopmentLeanNet:paid", {
                "[%lineOfBusiness%]" "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanNet:paid", {
                    "gross" "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanGross:paid", {
                        "[%claimsGenerator%]" "Podra:linesOfBusiness:[%lineOfBusiness%]:claimsGenerators:[%claimsGenerator%]:outClaimsDevelopmentLeanGross:paid", {}
                    }
                    "ceded" "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanCeded:paid", {
                        "[%contract%]" "Podra:linesOfBusiness:[%lineOfBusiness%]:reinsuranceContracts:[%contract%]:outClaimsDevelopmentLeanCeded:paid", {}
                    }
                }
            }
            "incurred" "Podra:linesOfBusiness:outClaimsDevelopmentLeanNet:incurred", {
                "[%lineOfBusiness%]" "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanNet:incurred", {
                    "gross" "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanGross:incurred", {
                        "[%claimsGenerator%]" "Podra:linesOfBusiness:[%lineOfBusiness%]:claimsGenerators:[%claimsGenerator%]:outClaimsDevelopmentLeanGross:incurred"
                    }
                    "ceded" "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanCeded:incurred", {
                        "[%contract%]" "Podra:linesOfBusiness:[%lineOfBusiness%]:reinsuranceContracts:[%contract%]:outClaimsDevelopmentLeanCeded:incurred"
                    }
                }
            }
            "reserved" "Podra:linesOfBusiness:outClaimsDevelopmentLeanNet:reserved", {
                "[%lineOfBusiness%]" "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanNet:reserved", {
                    "gross" "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanGross:reserved", {
                        "[%claimsGenerator%]" "Podra:linesOfBusiness:[%lineOfBusiness%]:claimsGenerators:[%claimsGenerator%]:outClaimsDevelopmentLeanGross:reserved"
                    }
                    "ceded" "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanCeded:reserved", {
                        "[%contract%]" "Podra:linesOfBusiness:[%lineOfBusiness%]:reinsuranceContracts:[%contract%]:outClaimsDevelopmentLeanCeded:reserved"
                    }
                }
            }
        }
    }
}
