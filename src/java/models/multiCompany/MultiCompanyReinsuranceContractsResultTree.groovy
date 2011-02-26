package models.multiCompany

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = MultiCompanyModel
displayName = "Reinsurance Contracts (Drill Down)"
language = "en"

mappings = {
    "MultiCompany" {
        "reinsuranceContracts" {
            "Financials" {
                "[%contract%]" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:outContractFinancials:result", {
                    "premium" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:outContractFinancials:cededPremium"
                    "commission" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:outContractFinancials:cededCommission"
                    "claim" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:outContractFinancials:cededClaim"
                }
            }
            "LossRatio" {
                "[%contract%]" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:outContractFinancials:cededLossRatio"
            }
            "DrillDown" {
                "claim" {
                    "incurred" {
                        "[%contract%]" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:outClaimsDevelopmentLeanNet:incurred", {
                        }
                        "ceded" {
                            "[%contract%]" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:outClaimsDevelopmentLeanCeded:incurred", {
                                "bySegments" {
                                    "[%lineOfBusiness%]" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanCeded:incurred"
                                }
                                "byPerils" {
                                    "[%peril%]" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsDevelopmentLeanCeded:incurred"
                                }
                            }
                        }
                        "gross" {
                            "[%contract%]" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:outClaimsDevelopmentLeanGross:incurred", {
                                "bySegments" {
                                    "[%lineOfBusiness%]" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanGross:incurred"
                                }
                                "byPerils" {
                                    "[%peril%]" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsDevelopmentLeanGross:incurred"
                                }
                            }
                        }
                    }
                }
                "underwriting" {
                    "premium" {
                        "[%contract%]" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:outNetAfterCoverUnderwritingInfo:premium", {
                        }
                        "ceded" {
                            "[%contract%]" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:outCoverUnderwritingInfo:premium", {
                                "fixedPremium" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:outCoverUnderwritingInfo:fixedPremium"
                                "variablePremium" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:outCoverUnderwritingInfo:variablePremium"
                                "bySegments" {
                                    "[%lineOfBusiness%]" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outCoverUnderwritingInfo:premium", {
                                        "fixedPremium" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outCoverUnderwritingInfo:fixedPremium"
                                        "variablePremium" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outCoverUnderwritingInfo:variablePremium"
                                    }
                                }
                                "byPerils" {
                                    "[%peril%]" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:claimsGenerators:[%peril%]:outCoverUnderwritingInfo:premium", {
                                        "fixedPremium" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:claimsGenerators:[%peril%]:outCoverUnderwritingInfo:fixedPremium"
                                        "variablePremium" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:claimsGenerators:[%peril%]:outCoverUnderwritingInfo:variablePremium"
                                    }
                                }
                            }
                        }
                        "gross" {
                            "[%contract%]" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:outUnderwritingInfo:premium", {
                                "bySegments" {
                                    "[%lineOfBusiness%]" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outUnderwritingInfo:premium"
                                }
                                "byPerils" {
                                    "[%peril%]" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:claimsGenerators:[%peril%]:outUnderwritingInfo:premium"
                                }
                            }
                        }
                    }
                    "commission" {
                        "[%contract%]" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:outNetAfterCoverUnderwritingInfo:commission", {
                        }
                        "ceded" {
                            "[%contract%]" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:outCoverUnderwritingInfo:commission", {
                                "fixedCommission" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:outCoverUnderwritingInfo:fixedCommission"
                                "variableCommission" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:outCoverUnderwritingInfo:variableCommission"
                                "bySegments" {
                                    "[%lineOfBusiness%]" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outCoverUnderwritingInfo:commission", {
                                        "fixedCommission" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outCoverUnderwritingInfo:fixedCommission"
                                        "variableCommission" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outCoverUnderwritingInfo:variableCommission"
                                    }
                                }
                                "byPerils" {
                                    "[%peril%]" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:claimsGenerators:[%peril%]:outCoverUnderwritingInfo:commission", {
                                        "fixedCommission" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:claimsGenerators:[%peril%]:outCoverUnderwritingInfo:fixedCommission"
                                        "variableCommission" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:claimsGenerators:[%peril%]:outCoverUnderwritingInfo:variableCommission"
                                    }
                                }
                            }
                        }
                        "gross" {
                            "[%contract%]" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:outUnderwritingInfo:commission", {
                                "bySegments" {
                                    "[%lineOfBusiness%]" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outUnderwritingInfo:commission"
                                }
                                "byPerils" {
                                    "[%peril%]" "MultiCompany:reinsuranceMarket:subContracts:[%contract%]:claimsGenerators:[%peril%]:outUnderwritingInfo:commission"
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}