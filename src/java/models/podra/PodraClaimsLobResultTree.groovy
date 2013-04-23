package models.podra

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = PodraModel
displayName = "Claims by Segments (drill down)"
language = "en"

mappings = {
    Podra {
        "claims" {
            "paid" "Podra:linesOfBusiness:outClaimsNet:paid", {
                "[%lineOfBusiness%]" "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsNet:paid", {
                    "gross" "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsGross:paid", {
                        "[%claimsGenerator%]" "Podra:linesOfBusiness:[%lineOfBusiness%]:claimsGenerators:[%claimsGenerator%]:outClaimsGross:paid"
                    }
                    "ceded" "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsCeded:paid", {
                        "[%contract%]" "Podra:linesOfBusiness:[%lineOfBusiness%]:reinsuranceContracts:[%contract%]:outClaimsCeded:paid"
                    }
                }
            }
            "incurred" "Podra:linesOfBusiness:outClaimsNet:incurred", {
                "[%lineOfBusiness%]" "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsNet:incurred", {
                    "gross" "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsGross:incurred", {
                        "[%claimsGenerator%]" "Podra:linesOfBusiness:[%lineOfBusiness%]:claimsGenerators:[%claimsGenerator%]:outClaimsGross:incurred"
                    }
                    "ceded" "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsCeded:incurred", {
                        "[%contract%]" "Podra:linesOfBusiness:[%lineOfBusiness%]:reinsuranceContracts:[%contract%]:outClaimsCeded:incurred"
                    }
                }
            }
            "reserved" "Podra:linesOfBusiness:outClaimsNet:reserved", {
                "[%lineOfBusiness%]" "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsNet:reserved", {
                    "gross" "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsGross:reserved", {
                        "[%claimsGenerator%]" "Podra:linesOfBusiness:[%lineOfBusiness%]:claimsGenerators:[%claimsGenerator%]:outClaimsGross:reserved"
                    }
                    "ceded" "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsCeded:reserved", {
                        "[%contract%]" "Podra:linesOfBusiness:[%lineOfBusiness%]:reinsuranceContracts:[%contract%]:outClaimsCeded:reserved"
                    }
                }
            }
        }
    }
}
