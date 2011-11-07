package models.podraFac

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = PodraFacModel
displayName = "Segments"
language = "en"

mappings = {
    "PodraFac" {
        "claims" {
            "net" {
                "incurred" "PodraFac:linesOfBusiness:outClaimsDevelopmentLeanNet:incurred", {
                    "[%subcomponents%]" "PodraFac:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanNet:incurred"
                }
                "paid" "PodraFac:linesOfBusiness:outClaimsDevelopmentLeanNet:paid", {
                    "[%subcomponents%]" "PodraFac:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanNet:paid"
                }
                "reserved" "PodraFac:linesOfBusiness:outClaimsDevelopmentLeanNet:reserved", {
                    "[%subcomponents%]" "PodraFac:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanNet:reserved"
                }
            }
            "gross" {
                "incurred" "PodraFac:linesOfBusiness:outClaimsDevelopmentLeanGross:incurred", {
                    "[%subcomponents%]" "PodraFac:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanGross:incurred"
                }
                "paid" "PodraFac:linesOfBusiness:outClaimsDevelopmentLeanGross:paid", {
                    "[%subcomponents%]" "PodraFac:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanGross:paid"
                }
                "reserved" "PodraFac:linesOfBusiness:outClaimsDevelopmentLeanGross:reserved", {
                    "[%subcomponents%]" "PodraFac:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanGross:reserved"
                }
            }
            "ceded" {
                "incurred" "PodraFac:linesOfBusiness:outClaimsDevelopmentLeanCeded:incurred", {
                    "[%subcomponents%]" "PodraFac:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanCeded:incurred"
                }
                "paid" "PodraFac:linesOfBusiness:outClaimsDevelopmentLeanCeded:paid", {
                    "[%subcomponents%]" "PodraFac:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanCeded:paid"
                }
                "reserved" "PodraFac:linesOfBusiness:outClaimsDevelopmentLeanCeded:reserved", {
                    "[%subcomponents%]" "PodraFac:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanCeded:reserved"
                }
            }
        }
        "underwriting" {
            "net" {
                "premium" "PodraFac:linesOfBusiness:outUnderwritingInfoNet:premium", {
                    "[%subcomponents%]" "PodraFac:linesOfBusiness:[%subcomponents%]:outUnderwritingInfoNet:premium"
                }
                "commission" "PodraFac:linesOfBusiness:outUnderwritingInfoNet:commission", {
                    "[%subcomponents%]" "PodraFac:linesOfBusiness:[%subcomponents%]:outUnderwritingInfoNet:commission"
                }
            }
            "gross" {
                "premium" "PodraFac:linesOfBusiness:outUnderwritingInfoGross:premium", {
                    "[%subcomponents%]" "PodraFac:linesOfBusiness:[%subcomponents%]:outUnderwritingInfoGross:premium"
                }
            }
            "ceded" {
                "premium" "PodraFac:linesOfBusiness:outUnderwritingInfoCeded:premium", {
                    "fixedPremium" "PodraFac:linesOfBusiness:outUnderwritingInfoCeded:fixedPremium"
                    "variablePremium" "PodraFac:linesOfBusiness:outUnderwritingInfoCeded:variablePremium"
                    "[%subcomponents%]" "PodraFac:linesOfBusiness:[%subcomponents%]:outUnderwritingInfoCeded:premium", {
                        "fixedPremium" "PodraFac:linesOfBusiness:[%subcomponents%]:outUnderwritingInfoCeded:fixedPremium"
                        "variablePremium" "PodraFac:linesOfBusiness:[%subcomponents%]:outUnderwritingInfoCeded:variablePremium"
                    }
                }
                "commission" "PodraFac:linesOfBusiness:outUnderwritingInfoCeded:commission", {
                    "fixedCommission" "PodraFac:linesOfBusiness:outUnderwritingInfoCeded:fixedCommission"
                    "variableCommission" "PodraFac:linesOfBusiness:outUnderwritingInfoCeded:variableCommission"
                    "[%subcomponents%]" "PodraFac:linesOfBusiness:[%subcomponents%]:outUnderwritingInfoCeded:commission", {
                        "fixedCommission" "PodraFac:linesOfBusiness:[%subcomponents%]:outUnderwritingInfoCeded:fixedCommission"
                        "variableCommission" "PodraFac:linesOfBusiness:[%subcomponents%]:outUnderwritingInfoCeded:variableCommission"
                    }
                }
            }
        }
    }
}