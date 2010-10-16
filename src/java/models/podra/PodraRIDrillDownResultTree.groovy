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
                    }
                }
            }
        }
    }
}
