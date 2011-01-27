package models.podra

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
model = PodraModel
displayName = "Premium and Commission Splitting"
language = "en"

mappings = {
    "Podra" {
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
                    "fixed premium" "Podra:linesOfBusiness:outUnderwritingInfoNet:fixedPremium"
                    "variable premium" "Podra:linesOfBusiness:outUnderwritingInfoNet:variablePremium"
                    "[%subcomponents%]" "Podra:linesOfBusiness:[%subcomponents%]:outUnderwritingInfoCeded:premium", {
                        "fixed premium" "Podra:linesOfBusiness:[%subcomponents%]:outUnderwritingInfoNet:fixedPremium"
                        "variable premium" "Podra:linesOfBusiness:[%subcomponents%]:outUnderwritingInfoNet:variablePremium"
                    }
                }
            }
            "commission" "Podra:linesOfBusiness:outUnderwritingInfoCeded:commission", {
                "[%subcomponents%]" "Podra:linesOfBusiness:[%subcomponents%]:outUnderwritingInfoCeded:commission"
            }
        }
    }
}
