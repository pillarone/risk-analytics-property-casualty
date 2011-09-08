package models.podraFac

import models.podraFac.PodraFacModel

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = PodraFacModel
displayName = "Claims by Segments (drill down)"
language = "en"

mappings = {
    "PodraFac" {
        "claims" {
            "paid" "PodraFac:linesOfBusiness:outClaimsDevelopmentLeanNet:paid", {
                "[%lineOfBusiness%]" "PodraFac:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanNet:paid", {
                    "gross" "PodraFac:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanGross:paid", {
                        "[%claimsGenerator%]" "PodraFac:linesOfBusiness:[%lineOfBusiness%]:claimsGenerators:[%claimsGenerator%]:outClaimsDevelopmentLeanGross:paid"
                    }
                    "ceded" "PodraFac:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanCeded:paid", {
                        "[%contract%]" "PodraFac:linesOfBusiness:[%lineOfBusiness%]:reinsuranceContracts:[%contract%]:outClaimsDevelopmentLeanCeded:paid"
                    }
                }
            }
            "incurred" "PodraFac:linesOfBusiness:outClaimsDevelopmentLeanNet:incurred", {
                "[%lineOfBusiness%]" "PodraFac:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanNet:incurred", {
                    "gross" "PodraFac:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanGross:incurred", {
                        "[%claimsGenerator%]" "PodraFac:linesOfBusiness:[%lineOfBusiness%]:claimsGenerators:[%claimsGenerator%]:outClaimsDevelopmentLeanGross:incurred"
                    }
                    "ceded" "PodraFac:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanCeded:incurred", {
                        "[%contract%]" "PodraFac:linesOfBusiness:[%lineOfBusiness%]:reinsuranceContracts:[%contract%]:outClaimsDevelopmentLeanCeded:incurred"
                    }
                }
            }
            "reserved" "PodraFac:linesOfBusiness:outClaimsDevelopmentLeanNet:reserved", {
                "[%lineOfBusiness%]" "PodraFac:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanNet:reserved", {
                    "gross" "PodraFac:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanGross:reserved", {
                        "[%claimsGenerator%]" "PodraFac:linesOfBusiness:[%lineOfBusiness%]:claimsGenerators:[%claimsGenerator%]:outClaimsDevelopmentLeanGross:reserved"
                    }
                    "ceded" "PodraFac:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanCeded:reserved", {
                        "[%contract%]" "PodraFac:linesOfBusiness:[%lineOfBusiness%]:reinsuranceContracts:[%contract%]:outClaimsDevelopmentLeanCeded:reserved"
                    }
                }
            }
        }
    }
}
