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
                "incurred" "Podra:linesOfBusiness:outClaimsNet:incurred", {
                    "[%subcomponents%]" "Podra:linesOfBusiness:[%subcomponents%]:outClaimsNet:incurred"
                }
                "paid" "Podra:linesOfBusiness:outClaimsNet:paid", {
                    "[%subcomponents%]" "Podra:linesOfBusiness:[%subcomponents%]:outClaimsNet:paid"
                }
                "reserved" "Podra:linesOfBusiness:outClaimsNet:reserved", {
                    "[%subcomponents%]" "Podra:linesOfBusiness:[%subcomponents%]:outClaimsNet:reserved"
                }
            }
            "gross" {
                "incurred" "Podra:linesOfBusiness:outClaimsGross:incurred", {
                    "[%subcomponents%]" "Podra:linesOfBusiness:[%subcomponents%]:outClaimsGross:incurred"
                }
                "paid" "Podra:linesOfBusiness:outClaimsGross:paid", {
                    "[%subcomponents%]" "Podra:linesOfBusiness:[%subcomponents%]:outClaimsGross:paid"
                }
                "reserved" "Podra:linesOfBusiness:outClaimsGross:reserved", {
                    "[%subcomponents%]" "Podra:linesOfBusiness:[%subcomponents%]:outClaimsGross:reserved"
                }
            }
            "ceded" {
                "incurred" "Podra:linesOfBusiness:outClaimsCeded:incurred", {
                    "[%subcomponents%]" "Podra:linesOfBusiness:[%subcomponents%]:outClaimsCeded:incurred"
                }
                "paid" "Podra:linesOfBusiness:outClaimsCeded:paid", {
                    "[%subcomponents%]" "Podra:linesOfBusiness:[%subcomponents%]:outClaimsCeded:paid"
                }
                "reserved" "Podra:linesOfBusiness:outClaimsCeded:reserved", {
                    "[%subcomponents%]" "Podra:linesOfBusiness:[%subcomponents%]:outClaimsCeded:reserved"
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