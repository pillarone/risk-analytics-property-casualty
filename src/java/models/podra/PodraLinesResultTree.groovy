package models.podra

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = PodraModel
displayName = "Lines of Business"
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
                "commission" "Podra:linesOfBusiness:outUnderwritingInfoGross:commission", {
                    "[%subcomponents%]" "Podra:linesOfBusiness:[%subcomponents%]:outUnderwritingInfoGross:commission"
                }
            }
            "ceded" {
                "premium" "Podra:linesOfBusiness:outUnderwritingInfoCeded:premium", {
                    "[%subcomponents%]" "Podra:linesOfBusiness:[%subcomponents%]:outUnderwritingInfoCeded:premium"
                }
                "commission" "Podra:linesOfBusiness:outUnderwritingInfoCeded:commission", {
                    "[%subcomponents%]" "Podra:linesOfBusiness:[%subcomponents%]:outUnderwritingInfoCeded:commission"
                }
            }
        }
    }
}