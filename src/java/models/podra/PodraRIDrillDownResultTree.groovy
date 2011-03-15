package models.podra

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = PodraModel
displayName = "Reinsurance Contracts (Drill down)"
language = "en"

mappings = {
    "Podra" {
        "reinsuranceContracts" {
            "Financials" {
                "[%contract%]" "Podra:reinsurance:subContracts:[%contract%]:outContractFinancials:result", {
                    "premium" "Podra:reinsurance:subContracts:[%contract%]:outContractFinancials:cededPremium"
                    "commission" "Podra:reinsurance:subContracts:[%contract%]:outContractFinancials:cededCommission"
                    "claim" "Podra:reinsurance:subContracts:[%contract%]:outContractFinancials:cededClaim"
                }
            }
            "LossRatio" {
                "[%contract%]" "Podra:reinsurance:subContracts:[%contract%]:outContractFinancials:cededLossRatio"
            }
            "DrillDown" {
                "claim" {
                    "incurred" {
                        "[%contract%]" "Podra:reinsurance:subContracts:[%contract%]:outClaimsDevelopmentLeanNet:incurred", {
                        }
                        "ceded" {
                            "[%contract%]" "Podra:reinsurance:subContracts:[%contract%]:outClaimsDevelopmentLeanCeded:incurred", {
                                "bySegments" {
                                    "[%lineOfBusiness%]" "Podra:reinsurance:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanCeded:incurred"
                                }
                                "byPerils" {
                                    "[%peril%]" "Podra:reinsurance:subContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsDevelopmentLeanCeded:incurred"
                                }
                            }
                        }
                        "gross" {
                            "[%contract%]" "Podra:reinsurance:subContracts:[%contract%]:outClaimsDevelopmentLeanGross:incurred", {
                                "bySegments" {
                                    "[%lineOfBusiness%]" "Podra:reinsurance:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanGross:incurred"
                                }
                                "byPerils" {
                                    "[%peril%]" "Podra:reinsurance:subContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsDevelopmentLeanGross:incurred"
                                }
                            }
                        }
                        "net" {
                            "[%contract%]" "Podra:reinsurance:subContracts:[%contract%]:outClaimsDevelopmentLeanNet:incurred", {
                                "bySegments" {
                                    "[%lineOfBusiness%]" "Podra:reinsurance:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanNet:incurred"
                                }
                                "byPerils" {
                                    "[%peril%]" "Podra:reinsurance:subContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsDevelopmentLeanNet:incurred"
                                }
                            }
                        }
                    }
                }
                "underwriting" {
                    "premium" {
                        "[%contract%]" "Podra:reinsurance:subContracts:[%contract%]:outNetAfterCoverUnderwritingInfo:premium", {
                        }
                        "ceded" {
                            "[%contract%]" "Podra:reinsurance:subContracts:[%contract%]:outCoverUnderwritingInfo:premium", {
                                "fixedPremium" "Podra:reinsurance:subContracts:[%contract%]:outCoverUnderwritingInfo:fixedPremium"
                                "variablePremium" "Podra:reinsurance:subContracts:[%contract%]:outCoverUnderwritingInfo:variablePremium"
                                "bySegments" {
                                    "[%lineOfBusiness%]" "Podra:reinsurance:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outCoverUnderwritingInfo:premium", {
                                        "fixedPremium" "Podra:reinsurance:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outCoverUnderwritingInfo:fixedPremium"
                                        "variablePremium" "Podra:reinsurance:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outCoverUnderwritingInfo:variablePremium"
                                    }
                                }
                                "byPerils" {
                                    "[%peril%]" "Podra:reinsurance:subContracts:[%contract%]:claimsGenerators:[%peril%]:outCoverUnderwritingInfo:premium", {
                                        "fixedPremium" "Podra:reinsurance:subContracts:[%contract%]:claimsGenerators:[%peril%]:outCoverUnderwritingInfo:fixedPremium"
                                        "variablePremium" "Podra:reinsurance:subContracts:[%contract%]:claimsGenerators:[%peril%]:outCoverUnderwritingInfo:variablePremium"
                                    }
                                }
                            }
                        }
                        "gross" {
                            "[%contract%]" "Podra:reinsurance:subContracts:[%contract%]:outUnderwritingInfo:premium", {
                                "bySegments" {
                                    "[%lineOfBusiness%]" "Podra:reinsurance:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outUnderwritingInfo:premium"
                                }
                                "byPerils" {
                                    "[%peril%]" "Podra:reinsurance:subContracts:[%contract%]:claimsGenerators:[%peril%]:outUnderwritingInfo:premium"
                                }
                            }
                        }
                    }
                    "commission" {
                        "[%contract%]" "Podra:reinsurance:subContracts:[%contract%]:outNetAfterCoverUnderwritingInfo:commission", {
                        }
                        "ceded" {
                            "[%contract%]" "Podra:reinsurance:subContracts:[%contract%]:outCoverUnderwritingInfo:commission", {
                                "fixedCommission" "Podra:reinsurance:subContracts:[%contract%]:outCoverUnderwritingInfo:fixedCommission"
                                "variableCommission" "Podra:reinsurance:subContracts:[%contract%]:outCoverUnderwritingInfo:variableCommission"
                                "bySegments" {
                                    "[%lineOfBusiness%]" "Podra:reinsurance:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outCoverUnderwritingInfo:commission", {
                                        "fixedCommission" "Podra:reinsurance:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outCoverUnderwritingInfo:fixedCommission"
                                        "variableCommission" "Podra:reinsurance:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outCoverUnderwritingInfo:variableCommission"
                                    }
                                }
                                "byPerils" {
                                    "[%peril%]" "Podra:reinsurance:subContracts:[%contract%]:claimsGenerators:[%peril%]:outCoverUnderwritingInfo:commission", {
                                        "fixedCommission" "Podra:reinsurance:subContracts:[%contract%]:claimsGenerators:[%peril%]:outCoverUnderwritingInfo:fixedCommission"
                                        "variableCommission" "Podra:reinsurance:subContracts:[%contract%]:claimsGenerators:[%peril%]:outCoverUnderwritingInfo:variableCommission"
                                    }
                                }
                            }
                        }
                        "gross" {
                            "[%contract%]" "Podra:reinsurance:subContracts:[%contract%]:outUnderwritingInfo:commission", {
                                "bySegments" {
                                    "[%lineOfBusiness%]" "Podra:reinsurance:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outUnderwritingInfo:commission"
                                }
                                "byPerils" {
                                    "[%peril%]" "Podra:reinsurance:subContracts:[%contract%]:claimsGenerators:[%peril%]:outUnderwritingInfo:commission"
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
