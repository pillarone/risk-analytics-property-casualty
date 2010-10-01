package models.podra

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = PodraModel
displayName = "Structures (Drill down)"
language = "en"
mappings = [
        "Podra:[%structure%]:claim:paid": "Podra:structures:[%structure%]:outClaimsNet:paid",
        "Podra:[%structure%]:claim:paid:gross": "Podra:structures:[%structure%]:outClaimsGross:paid",
        "Podra:[%structure%]:claim:paid:gross:segment:[%segment%]": "Podra:structures:[%structure%]:linesOfBusiness:[%segment%]:outClaimsGross:paid",
        "Podra:[%structure%]:claim:paid:gross:peril:[%peril%]": "Podra:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsGross:paid",
        "Podra:[%structure%]:claim:paid:ceded": "Podra:structures:[%structure%]:outClaimsCeded:paid",
        "Podra:[%structure%]:claim:paid:ceded:segment:[%segment%]": "Podra:structures:[%structure%]:linesOfBusiness:[%segment%]:outClaimsCeded:paid",
        "Podra:[%structure%]:claim:paid:ceded:peril:[%peril%]": "Podra:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsCeded:paid",
        "Podra:[%structure%]:claim:paid:ceded:contract:[%contract%]": "Podra:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsCeded:paid",
        "Podra:[%structure%]:claim:paid:ceded:contract:[%contract%]:segment:[%segment%]": "Podra:structures:[%structure%]:linesOfBusiness:[%segment%]:reinsuranceContracts:[%contract%]:outClaimsCeded:paid",
        "Podra:[%structure%]:claim:paid:ceded:contract:[%contract%]:peril:[%peril%]": "Podra:structures:[%structure%]:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:paid",

        "Podra:[%structure%]:claim:incurred": "Podra:structures:[%structure%]:outClaimsNet:incurred",
        "Podra:[%structure%]:claim:incurred:gross": "Podra:structures:[%structure%]:outClaimsGross:incurred",
        "Podra:[%structure%]:claim:incurred:gross:segment:[%segment%]": "Podra:structures:[%structure%]:linesOfBusiness:[%segment%]:outClaimsGross:incurred",
        "Podra:[%structure%]:claim:incurred:gross:peril:[%peril%]": "Podra:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsGross:incurred",
        "Podra:[%structure%]:claim:incurred:ceded": "Podra:structures:[%structure%]:outClaimsCeded:incurred",
        "Podra:[%structure%]:claim:incurred:ceded:segment:[%segment%]": "Podra:structures:[%structure%]:linesOfBusiness:[%segment%]:outClaimsCeded:incurred",
        "Podra:[%structure%]:claim:incurred:ceded:peril:[%peril%]": "Podra:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsCeded:incurred",
        "Podra:[%structure%]:claim:incurred:ceded:contract:[%contract%]": "Podra:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsCeded:incurred",
        "Podra:[%structure%]:claim:incurred:ceded:contract:[%contract%]:segment:[%segment%]": "Podra:structures:[%structure%]:linesOfBusiness:[%segment%]:reinsuranceContracts:[%contract%]:outClaimsCeded:incurred",
        "Podra:[%structure%]:claim:incurred:ceded:contract:[%contract%]:peril:[%peril%]": "Podra:structures:[%structure%]:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:incurred",

        "Podra:[%structure%]:claim:reserved": "Podra:structures:[%structure%]:outClaimsNet:reserved",
        "Podra:[%structure%]:claim:reserved:gross": "Podra:structures:[%structure%]:outClaimsGross:reserved",
        "Podra:[%structure%]:claim:reserved:gross:segment:[%segment%]": "Podra:structures:[%structure%]:linesOfBusiness:[%segment%]:outClaimsGross:reserved",
        "Podra:[%structure%]:claim:reserved:gross:peril:[%peril%]": "Podra:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsGross:reserved",
        "Podra:[%structure%]:claim:reserved:ceded": "Podra:structures:[%structure%]:outClaimsCeded:reserved",
        "Podra:[%structure%]:claim:reserved:ceded:segment:[%segment%]": "Podra:structures:[%structure%]:linesOfBusiness:[%segment%]:outClaimsCeded:reserved",
        "Podra:[%structure%]:claim:reserved:ceded:peril:[%peril%]": "Podra:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsCeded:reserved",
        "Podra:[%structure%]:claim:reserved:ceded:contract:[%contract%]": "Podra:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsCeded:reserved",
        "Podra:[%structure%]:claim:reserved:ceded:contract:[%contract%]:segment:[%segment%]": "Podra:structures:[%structure%]:linesOfBusiness:[%segment%]:reinsuranceContracts:[%contract%]:outClaimsCeded:reserved",
        "Podra:[%structure%]:claim:reserved:ceded:contract:[%contract%]:peril:[%peril%]": "Podra:structures:[%structure%]:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:reserved",

        "Podra:[%structure%]:underwriting:premium": "Podra:structures:[%structure%]:outUnderwritingInfoNet:premium",
        "Podra:[%structure%]:underwriting:premium:gross": "Podra:structures:[%structure%]:outUnderwritingInfoGross:premium",
        "Podra:[%structure%]:underwriting:premium:gross:segment:[%segment%]": "Podra:structures:[%structure%]:linesOfBusiness:[%segment%]:outUnderwritingInfoGross:premium",
        "Podra:[%structure%]:underwriting:premium:ceded": "Podra:structures:[%structure%]:outUnderwritingInfoCeded:premium",
        "Podra:[%structure%]:underwriting:premium:ceded:segment:[%segment%]": "Podra:structures:[%structure%]:linesOfBusiness:[%segment%]:outUnderwritingInfoCeded:premium",
        "Podra:[%structure%]:underwriting:premium:ceded:contract:[%contract%]": "Podra:structures:[%structure%]:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premium",

        "Podra:[%structure%]:underwriting:commission": "Podra:structures:[%structure%]:outUnderwritingInfoNet:commission",
        "Podra:[%structure%]:underwriting:commission:gross": "Podra:structures:[%structure%]:outUnderwritingInfoGross:commission",
        "Podra:[%structure%]:underwriting:commission:gross:segment:[%segment%]": "Podra:structures:[%structure%]:linesOfBusiness:[%segment%]:outUnderwritingInfoGross:commission",
        "Podra:[%structure%]:underwriting:commission:ceded": "Podra:structures:[%structure%]:outUnderwritingInfoCeded:commission",
        "Podra:[%structure%]:underwriting:commission:ceded:segment:[%segment%]": "Podra:structures:[%structure%]:linesOfBusiness:[%segment%]:outUnderwritingInfoCeded:commission",
        "Podra:[%structure%]:underwriting:commission:ceded:contract:[%contract%]": "Podra:structures:[%structure%]:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:commission",
]