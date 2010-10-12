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
                    // todo(msp): the following three lines are missing in the result tree view
                    "premium" "Podra:reinsurance:subContracts:[%contract%]:outContractFinancials:premium", {}
                    "commission" "Podra:reinsurance:subContracts:[%contract%]:outContractFinancials:commission", {}
                    "claim" "Podra:reinsurance:subContracts:[%contract%]:outContractFinancials:claim", {}
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
                                // todo(msp): the following line throw an exception if enabled, fix! RSTB L126 as currentNode is null
//                                "bySegments" {
//                                    "[%lineOfBusiness%]" "Podra:reinsurance:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanCeded:incurred", {}
//                                }
//                                "byPerils" {
//                                    "[%peril%]" "Podra:reinsurance:subContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsDevelopmentLeanCeded:incurred", {}
//                                }
                            }
                        }
                        "gross" {
                            "[%contract%]" "Podra:reinsurance:subContracts:[%contract%]:outClaimsDevelopmentLeanGross:incurred", {
                                // todo(msp): the following line throw an exception if enabled, fix! RSTB L126 as currentNode is null
//                                "bySegments" {
//                                    "[%lineOfBusiness%]" "Podra:reinsurance:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanGross:incurred", {}
//                                }
//                                "byPerils" {
//                                    "[%peril%]" "Podra:reinsurance:subContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsDevelopmentLeanGross:incurred", {}
//                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
//mappings = [
//        "Podra:reinsuranceContracts:Financials:[%contract%]": "Podra:reinsurance:subContracts:[%contract%]:outContractFinancials:result",
//        "Podra:reinsuranceContracts:Financials:[%contract%]:premium": "Podra:reinsurance:subContracts:[%contract%]:outContractFinancials:premium",
//        "Podra:reinsuranceContracts:Financials:[%contract%]:commission": "Podra:reinsurance:subContracts:[%contract%]:outContractFinancials:commission",
//        "Podra:reinsuranceContracts:Financials:[%contract%]:claim": "Podra:reinsurance:subContracts:[%contract%]:outContractFinancials:claim",
//        "Podra:reinsuranceContracts:LossRatio:[%contract%]": "Podra:reinsurance:subContracts:[%contract%]:outContractFinancials:cededLossRatio",
//        "Podra:reinsuranceContracts:DrillDown:claim:incurred:[%contract%]": "Podra:reinsurance:subContracts:[%contract%]:outClaimsDevelopmentLeanNet:incurred",
//        "Podra:reinsuranceContracts:DrillDown:claim:incurred:ceded:[%contract%]": "Podra:reinsurance:subContracts:[%contract%]:outClaimsDevelopmentLeanCeded:incurred",
//        "Podra:reinsuranceContracts:DrillDown:claim:incurred:ceded:[%contract%]:bySegments:[%lineOfBusiness%]": "Podra:reinsurance:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanCeded:incurred",
//        "Podra:reinsuranceContracts:DrillDown:claim:incurred:ceded:[%contract%]:byPerils:[%peril%]": "Podra:reinsurance:subContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsDevelopmentLeanCeded:incurred",
//        "Podra:reinsuranceContracts:DrillDown:claim:incurred:gross:[%contract%]": "Podra:reinsurance:subContracts:[%contract%]:outClaimsDevelopmentLeanGross:incurred",
//        "Podra:reinsuranceContracts:DrillDown:claim:incurred:gross:[%contract%]:bySegments:[%lineOfBusiness%]": "Podra:reinsurance:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanGross:incurred",
//        "Podra:reinsuranceContracts:DrillDown:claim:incurred:gross:[%contract%]:byPerils:[%peril%]": "Podra:reinsurance:subContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsDevelopmentLeanGross:incurred",
//        "Podra:reinsuranceContracts:DrillDown:claim:paid:[%contract%]": "Podra:reinsurance:subContracts:[%contract%]:outClaimsDevelopmentLeanNet:paid",
//        "Podra:reinsuranceContracts:DrillDown:claim:paid:ceded:[%contract%]": "Podra:reinsurance:subContracts:[%contract%]:outClaimsDevelopmentLeanCeded:paid",
//        "Podra:reinsuranceContracts:DrillDown:claim:paid:ceded:[%contract%]:bySegments:[%lineOfBusiness%]": "Podra:reinsurance:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanCeded:paid",
//        "Podra:reinsuranceContracts:DrillDown:claim:paid:ceded:[%contract%]:byPerils:[%peril%]": "Podra:reinsurance:subContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsDevelopmentLeanCeded:paid",
//        "Podra:reinsuranceContracts:DrillDown:claim:paid:gross:[%contract%]": "Podra:reinsurance:subContracts:[%contract%]:outClaimsDevelopmentLeanGross:paid",
//        "Podra:reinsuranceContracts:DrillDown:claim:paid:gross:[%contract%]:bySegments:[%lineOfBusiness%]": "Podra:reinsurance:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanGross:paid",
//        "Podra:reinsuranceContracts:DrillDown:claim:paid:gross:[%contract%]:byPerils:[%peril%]": "Podra:reinsurance:subContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsDevelopmentLeanGross:paid",
//        "Podra:reinsuranceContracts:DrillDown:claim:reserved:[%contract%]": "Podra:reinsurance:subContracts:[%contract%]:outClaimsDevelopmentLeanNet:reserved",
//        "Podra:reinsuranceContracts:DrillDown:claim:reserved:ceded:[%contract%]": "Podra:reinsurance:subContracts:[%contract%]:outClaimsDevelopmentLeanCeded:reserved",
//        "Podra:reinsuranceContracts:DrillDown:claim:reserved:ceded:[%contract%]:bySegments:[%lineOfBusiness%]": "Podra:reinsurance:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanCeded:reserved",
//        "Podra:reinsuranceContracts:DrillDown:claim:reserved:ceded:[%contract%]:byPerils:[%peril%]": "Podra:reinsurance:subContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsDevelopmentLeanCeded:reserved",
//        "Podra:reinsuranceContracts:DrillDown:claim:reserved:gross:[%contract%]": "Podra:reinsurance:subContracts:[%contract%]:outClaimsDevelopmentLeanGross:reserved",
//        "Podra:reinsuranceContracts:DrillDown:claim:reserved:gross:[%contract%]:bySegments:[%lineOfBusiness%]": "Podra:reinsurance:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanGross:reserved",
//        "Podra:reinsuranceContracts:DrillDown:claim:reserved:gross:[%contract%]:byPerils:[%peril%]": "Podra:reinsurance:subContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsDevelopmentLeanGross:reserved",
//]