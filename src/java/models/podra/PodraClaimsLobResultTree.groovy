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
                        // todo(msp): the following line is not displayed in the result view
                        "[%claimsGenerator%]" "Podra:linesOfBusiness:[%lineOfBusiness%]:claimsGenerators:[%claimsGenerator%]:outClaimsDevelopmentLeanGross:paid", {}
                    }
                    "ceded" "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanCeded:paid", {
                        // todo(msp): the following line is not displayed in the result view
                        "[%contract%]" "Podra:linesOfBusiness:[%lineOfBusiness%]:reinsuranceContracts:[%contract%]:outClaimsDevelopmentLeanCeded:paid", {}
                    }
                }
            }
            "incurred" "Podra:linesOfBusiness:outClaimsDevelopmentLeanNet:incurred", {
                "[%lineOfBusiness%]" "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanNet:incurred", {
                    "gross" "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanGross:incurred", {
                        // todo(msp): the following line is not displayed in the result view
                        "[%claimsGenerator%]" "Podra:linesOfBusiness:[%lineOfBusiness%]:claimsGenerators:[%claimsGenerator%]:outClaimsDevelopmentLeanGross:incurred"
                    }
                    "ceded" "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanCeded:incurred", {
                        // todo(msp): the following line is not displayed in the result view
                        "[%contract%]" "Podra:linesOfBusiness:[%lineOfBusiness%]:reinsuranceContracts:[%contract%]:outClaimsDevelopmentLeanCeded:incurred"
                    }
                }
            }
            "reserved" "Podra:linesOfBusiness:outClaimsDevelopmentLeanNet:reserved", {
                "[%lineOfBusiness%]" "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanNet:reserved", {
                    "gross" "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanGross:reserved", {
                        // todo(msp): the following line is not displayed in the result view
                        "[%claimsGenerator%]" "Podra:linesOfBusiness:[%lineOfBusiness%]:claimsGenerators:[%claimsGenerator%]:outClaimsDevelopmentLeanGross:reserved"
                    }
                    "ceded" "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanCeded:reserved", {
                        // todo(msp): the following line is not displayed in the result view
                        "[%contract%]" "Podra:linesOfBusiness:[%lineOfBusiness%]:reinsuranceContracts:[%contract%]:outClaimsDevelopmentLeanCeded:reserved"
                    }
                }
            }
        }
    }
}
//mappings = [
//        "Podra:claims:paid": "Podra:linesOfBusiness:outClaimsDevelopmentLeanNet:paid",
//        "Podra:claims:paid:[%lineOfBusiness%]": "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanNet:paid",
//        "Podra:claims:paid:[%lineOfBusiness%]:gross": "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanGross:paid",
//        "Podra:claims:paid:[%lineOfBusiness%]:gross:[%claimsGenerator%]": "Podra:linesOfBusiness:[%lineOfBusiness%]:claimsGenerators:[%claimsGenerator%]:outClaimsDevelopmentLeanGross:paid",
//        "Podra:claims:paid:[%lineOfBusiness%]:ceded": "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanCeded:paid",
//        "Podra:claims:paid:[%lineOfBusiness%]:ceded:[%contract%]": "Podra:linesOfBusiness:[%lineOfBusiness%]:reinsuranceContracts:[%contract%]:outClaimsDevelopmentLeanCeded:paid",
//
//        "Podra:claims:incurred": "Podra:linesOfBusiness:outClaimsDevelopmentLeanNet:incurred",
//        "Podra:claims:incurred:[%lineOfBusiness%]": "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanNet:incurred",
//        "Podra:claims:incurred:[%lineOfBusiness%]:gross": "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanGross:incurred",
//        "Podra:claims:incurred:[%lineOfBusiness%]:gross:[%claimsGenerator%]": "Podra:linesOfBusiness:[%lineOfBusiness%]:claimsGenerators:[%claimsGenerator%]:outClaimsDevelopmentLeanGross:incurred",
//        "Podra:claims:incurred:[%lineOfBusiness%]:ceded": "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanCeded:incurred",
//        "Podra:claims:incurred:[%lineOfBusiness%]:ceded:[%contract%]": "Podra:linesOfBusiness:[%lineOfBusiness%]:reinsuranceContracts:[%contract%]:outClaimsDevelopmentLeanCeded:incurred",
//
//        "Podra:claims:reserved": "Podra:linesOfBusiness:outClaimsDevelopmentLeanNet:reserved",
//        "Podra:claims:reserved:[%lineOfBusiness%]": "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanNet:reserved",
//        "Podra:claims:reserved:[%lineOfBusiness%]:gross": "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanGross:reserved",
//        "Podra:claims:reserved:[%lineOfBusiness%]:gross:[%claimsGenerator%]": "Podra:linesOfBusiness:[%lineOfBusiness%]:claimsGenerators:[%claimsGenerator%]:outClaimsDevelopmentLeanGross:reserved",
//        "Podra:claims:reserved:[%lineOfBusiness%]:ceded": "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanCeded:reserved",
//        "Podra:claims:reserved:[%lineOfBusiness%]:ceded:[%contract%]": "Podra:linesOfBusiness:[%lineOfBusiness%]:reinsuranceContracts:[%contract%]:outClaimsDevelopmentLeanCeded:reserved",
//
//]