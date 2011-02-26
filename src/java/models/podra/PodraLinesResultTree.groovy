package models.podra

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = PodraModel
displayName = "Segments"
language = "en"

mappings = {
    "Podra" {
        "claims" {
            "net" {
                "incurred" "Podra:linesOfBusiness:outClaimsDevelopmentLeanNet:incurred", {
                    "[%subcomponents%]" "Podra:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanNet:incurred"
                }
                "paid" "Podra:linesOfBusiness:outClaimsDevelopmentLeanNet:paid", {
                    "[%subcomponents%]" "Podra:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanNet:paid"
                }
                "reserved" "Podra:linesOfBusiness:outClaimsDevelopmentLeanNet:reserved", {
                    "[%subcomponents%]" "Podra:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanNet:reserved"
                }
            }
            "gross" {
                "incurred" "Podra:linesOfBusiness:outClaimsDevelopmentLeanGross:incurred", {
                    "[%subcomponents%]" "Podra:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanGross:incurred"
                }
                "paid" "Podra:linesOfBusiness:outClaimsDevelopmentLeanGross:paid", {
                    "[%subcomponents%]" "Podra:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanGross:paid"
                }
                "reserved" "Podra:linesOfBusiness:outClaimsDevelopmentLeanGross:reserved", {
                    "[%subcomponents%]" "Podra:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanGross:reserved"
                }
            }
            "ceded" {
                "incurred" "Podra:linesOfBusiness:outClaimsDevelopmentLeanCeded:incurred", {
                    "[%subcomponents%]" "Podra:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanCeded:incurred"
                }
                "paid" "Podra:linesOfBusiness:outClaimsDevelopmentLeanCeded:paid", {
                    "[%subcomponents%]" "Podra:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanCeded:paid"
                }
                "reserved" "Podra:linesOfBusiness:outClaimsDevelopmentLeanCeded:reserved", {
                    "[%subcomponents%]" "Podra:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanCeded:reserved"
                }
            }
        }
        "underwriting" {
            "net" {
                "premium" "Podra:linesOfBusiness:outUnderwritingInfoNet:premium", {
                    "[%subcomponents%]" "Podra:linesOfBusiness:[%subcomponents%]:outUnderwritingInfoNet:premium"
                }
                "commission" "Podra:linesOfBusiness:outUnderwritingInfoNet:commission", {
                    "[%subcomponents%]" "Podra:linesOfBusiness:[%subcomponents%]:outUnderwritingInfoNet:commission"
                }
            }
            "gross" {
                "premium" "Podra:linesOfBusiness:outUnderwritingInfoGross:premium", {
                    "[%subcomponents%]" "Podra:linesOfBusiness:[%subcomponents%]:outUnderwritingInfoGross:premium"
                }
            }
            "ceded" {
                "premium" "Podra:linesOfBusiness:outUnderwritingInfoCeded:premium", {
                    "fixedPremium" "Podra:linesOfBusiness:outUnderwritingInfoCeded:fixedPremium"
                    "variablePremium" "Podra:linesOfBusiness:outUnderwritingInfoCeded:variablePremium"
                    "[%subcomponents%]" "Podra:linesOfBusiness:[%subcomponents%]:outUnderwritingInfoCeded:premium", {
                        "fixedPremium" "Podra:linesOfBusiness:[%subcomponents%]:outUnderwritingInfoCeded:fixedPremium"
                        "variablePremium" "Podra:linesOfBusiness:[%subcomponents%]:outUnderwritingInfoCeded:variablePremium"
                    }
                }
                "commission" "Podra:linesOfBusiness:outUnderwritingInfoCeded:commission", {
                    "fixedCommission" "Podra:linesOfBusiness:outUnderwritingInfoCeded:fixedCommission"
                    "variableCommission" "Podra:linesOfBusiness:outUnderwritingInfoCeded:variableCommission"
                    "[%subcomponents%]" "Podra:linesOfBusiness:[%subcomponents%]:outUnderwritingInfoCeded:commission", {
                        "fixedCommission" "Podra:linesOfBusiness:[%subcomponents%]:outUnderwritingInfoCeded:fixedCommission"
                        "variableCommission" "Podra:linesOfBusiness:[%subcomponents%]:outUnderwritingInfoCeded:variableCommission"
                    }
                }
            }
        }
    }
}