package models.podraFac

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = PodraFacModel
displayName = "Reinsurance Contracts (Drill down)"
language = "en"

mappings = {
    "PodraFac" {
        "reinsuranceContracts" {
            "Financials" {
                "[%contract%]" "PodraFac:reinsurance:subContracts:[%contract%]:outContractFinancials:result", {
                    "premium" "PodraFac:reinsurance:subContracts:[%contract%]:outContractFinancials:cededPremium"
                    "commission" "PodraFac:reinsurance:subContracts:[%contract%]:outContractFinancials:cededCommission"
                    "claim" "PodraFac:reinsurance:subContracts:[%contract%]:outContractFinancials:cededClaim"
                }
            }
            "LossRatio" {
                "[%contract%]" "PodraFac:reinsurance:subContracts:[%contract%]:outContractFinancials:cededLossRatio"
            }
            "DrillDown" {
                "claim" {
                    "incurred" {
                        "[%contract%]" "PodraFac:reinsurance:subContracts:[%contract%]:outClaimsDevelopmentLeanNet:incurred", {
                        }
                        "ceded" {
                            "[%contract%]" "PodraFac:reinsurance:subContracts:[%contract%]:outClaimsDevelopmentLeanCeded:incurred", {
                                "bySegments" {
                                    "[%lineOfBusiness%]" "PodraFac:reinsurance:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanCeded:incurred"
                                }
                                "byPerils" {
                                    "[%peril%]" "PodraFac:reinsurance:subContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsDevelopmentLeanCeded:incurred"
                                }
                            }
                        }
                        "gross" {
                            "[%contract%]" "PodraFac:reinsurance:subContracts:[%contract%]:outClaimsDevelopmentLeanGross:incurred", {
                                "bySegments" {
                                    "[%lineOfBusiness%]" "PodraFac:reinsurance:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanGross:incurred"
                                }
                                "byPerils" {
                                    "[%peril%]" "PodraFac:reinsurance:subContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsDevelopmentLeanGross:incurred"
                                }
                            }
                        }
                        "net" {
                            "[%contract%]" "PodraFac:reinsurance:subContracts:[%contract%]:outClaimsDevelopmentLeanNet:incurred", {
                                "bySegments" {
                                    "[%lineOfBusiness%]" "PodraFac:reinsurance:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanNet:incurred"
                                }
                                "byPerils" {
                                    "[%peril%]" "PodraFac:reinsurance:subContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsDevelopmentLeanNet:incurred"
                                }
                            }
                        }
                    }
                }
                "underwriting" {
                    "premium" {
                        "[%contract%]" "PodraFac:reinsurance:subContracts:[%contract%]:outNetAfterCoverUnderwritingInfo:premium", {
                        }
                        "ceded" {
                            "[%contract%]" "PodraFac:reinsurance:subContracts:[%contract%]:outCoverUnderwritingInfo:premium", {
                                "fixedPremium" "PodraFac:reinsurance:subContracts:[%contract%]:outCoverUnderwritingInfo:fixedPremium"
                                "variablePremium" "PodraFac:reinsurance:subContracts:[%contract%]:outCoverUnderwritingInfo:variablePremium"
                                "bySegments" {
                                    "[%lineOfBusiness%]" "PodraFac:reinsurance:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outCoverUnderwritingInfo:premium", {
                                        "fixedPremium" "PodraFac:reinsurance:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outCoverUnderwritingInfo:fixedPremium"
                                        "variablePremium" "PodraFac:reinsurance:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outCoverUnderwritingInfo:variablePremium"
                                    }
                                }
                                "byPerils" {
                                    "[%peril%]" "PodraFac:reinsurance:subContracts:[%contract%]:claimsGenerators:[%peril%]:outCoverUnderwritingInfo:premium", {
                                        "fixedPremium" "PodraFac:reinsurance:subContracts:[%contract%]:claimsGenerators:[%peril%]:outCoverUnderwritingInfo:fixedPremium"
                                        "variablePremium" "PodraFac:reinsurance:subContracts:[%contract%]:claimsGenerators:[%peril%]:outCoverUnderwritingInfo:variablePremium"
                                    }
                                }
                            }
                        }
                        "gross" {
                            "[%contract%]" "PodraFac:reinsurance:subContracts:[%contract%]:outUnderwritingInfo:premium", {
                                "bySegments" {
                                    "[%lineOfBusiness%]" "PodraFac:reinsurance:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outUnderwritingInfo:premium"
                                }
                                "byPerils" {
                                    "[%peril%]" "PodraFac:reinsurance:subContracts:[%contract%]:claimsGenerators:[%peril%]:outUnderwritingInfo:premium"
                                }
                            }
                        }
                    }
                    "commission" {
                        "[%contract%]" "PodraFac:reinsurance:subContracts:[%contract%]:outNetAfterCoverUnderwritingInfo:commission", {
                        }
                        "ceded" {
                            "[%contract%]" "PodraFac:reinsurance:subContracts:[%contract%]:outCoverUnderwritingInfo:commission", {
                                "fixedCommission" "PodraFac:reinsurance:subContracts:[%contract%]:outCoverUnderwritingInfo:fixedCommission"
                                "variableCommission" "PodraFac:reinsurance:subContracts:[%contract%]:outCoverUnderwritingInfo:variableCommission"
                                "bySegments" {
                                    "[%lineOfBusiness%]" "PodraFac:reinsurance:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outCoverUnderwritingInfo:commission", {
                                        "fixedCommission" "PodraFac:reinsurance:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outCoverUnderwritingInfo:fixedCommission"
                                        "variableCommission" "PodraFac:reinsurance:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outCoverUnderwritingInfo:variableCommission"
                                    }
                                }
                                "byPerils" {
                                    "[%peril%]" "PodraFac:reinsurance:subContracts:[%contract%]:claimsGenerators:[%peril%]:outCoverUnderwritingInfo:commission", {
                                        "fixedCommission" "PodraFac:reinsurance:subContracts:[%contract%]:claimsGenerators:[%peril%]:outCoverUnderwritingInfo:fixedCommission"
                                        "variableCommission" "PodraFac:reinsurance:subContracts:[%contract%]:claimsGenerators:[%peril%]:outCoverUnderwritingInfo:variableCommission"
                                    }
                                }
                            }
                        }
                        "gross" {
                            "[%contract%]" "PodraFac:reinsurance:subContracts:[%contract%]:outUnderwritingInfo:commission", {
                                "bySegments" {
                                    "[%lineOfBusiness%]" "PodraFac:reinsurance:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outUnderwritingInfo:commission"
                                }
                                "byPerils" {
                                    "[%peril%]" "PodraFac:reinsurance:subContracts:[%contract%]:claimsGenerators:[%peril%]:outUnderwritingInfo:commission"
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
