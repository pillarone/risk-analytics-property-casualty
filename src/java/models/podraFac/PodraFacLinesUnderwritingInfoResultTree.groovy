package models.podraFac

import models.podraFac.PodraFacModel

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
model = PodraFacModel
displayName = "Premium and Commission Splitting"
language = "en"

mappings = {
    "PodraFAC" {
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
