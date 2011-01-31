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
