package models.podra

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = PodraModel
displayName = "Drill down"
mappings = [
        "Podra:claims:incurred:motorHull": "Podra:linesOfBusiness:subMotorHull:outClaimsDevelopmentLeanNet:incurred",
        "Podra:claims:incurred:motorHull:gross": "Podra:linesOfBusiness:subMotorHull:outClaimsDevelopmentLeanGross:incurred",
        "Podra:claims:incurred:motorHull:gross:Single": "Podra:claimsGenerators:subMotorHullSingle:outClaimsLeanDevelopment:incurred",
        "Podra:claims:incurred:motorHull:gross:Attritional": "Podra:claimsGenerators:subMotorHullAttritional:outClaimsLeanDevelopment:incurred",
        "Podra:claims:incurred:motorHull:gross:Reserves": "Podra:reserveGenerators:subMotorHullReserves:outClaimsLeanDevelopment:incurred",
        "Podra:claims:incurred:motorHull:ceded": "Podra:linesOfBusiness:subMotorHull:outClaimsDevelopmentLeanCeded:incurred",
        "Podra:claims:incurred:motorHull:ceded:motorHullQuotaShare": "Podra:reinsurance:subContracts:subMotorHullQuotaShare:outClaimsDevelopmentLeanCeded:incurred",
        "Podra:claims:incurred:motorHull:ceded:motorHullQuotaShare:Single": "Podra:reinsurance:subContracts:subMotorHullQuotaShare:subMotorHullSingle:outClaimsDevelopmentLeanCeded:incurred",
        "Podra:claims:incurred:motorHull:ceded:motorHullQuotaShare:Attrtional": "Podra:reinsurance:subContracts:subMotorHullQuotaShare:subMotorHullAttritional:outClaimsDevelopmentLeanCeded:incurred",
        "Podra:claims:incurred:motorHull:ceded:motorHullQuotaShare:Reserves": "Podra:reinsurance:subContracts:subMotorHullQuotaShare:subMotorHullReserves:outClaimsDevelopmentLeanCeded:incurred",
        "Podra:claims:incurred:motorHull:ceded:motorHullWXL": "Podra:reinsurance:subContracts:subMotorHullWxl:outClaimsDevelopmentLeanCeded:incurred",
        "Podra:claims:incurred:motorHull:ceded:motorHullWXL:Single": "Podra:reinsurance:subContracts:subMotorHullWxl:subMotorHullSingle:outClaimsDevelopmentLeanCeded:incurred",
        "Podra:claims:incurred:motorHull:ceded:motorHullWXL:Attritional": "Podra:reinsurance:subContracts:subMotorHullWxl:subMotorHullAttritional:outClaimsDevelopmentLeanCeded:incurred",
        "Podra:claims:incurred:motorHull:ceded:motorHullWXL:Reserves": "Podra:reinsurance:subContracts:subMotorHullWxl:subMotorHullReserves:outClaimsDevelopmentLeanCeded:incurred",
        "Podra:claims:incurred:motorThirdPartyLiability:Single": "Podra:claimsGenerators:subMotorThirdPartyLiabilitySingle:outClaimsLeanDevelopment:incurred",
        "Podra:claims:incurred:personalAccident:Single": "Podra:claimsGenerators:subPersonalAccidentSingle:outClaimsLeanDevelopment:incurred",
        "Podra:claims:paid:motorHull": "Podra:linesOfBusiness:subMotorHull:outClaimsDevelopmentLeanNet:paid",
        "Podra:claims:paid:motorHull:gross": "Podra:linesOfBusiness:subMotorHull:outClaimsDevelopmentLeanGross:paid",
        "Podra:claims:paid:motorHull:gross:Single": "Podra:claimsGenerators:subMotorHullSingle:outClaimsLeanDevelopment:paid",
        "Podra:claims:paid:motorHull:gross:Attritional": "Podra:claimsGenerators:subMotorHullAttritional:outClaimsLeanDevelopment:paid",
        "Podra:claims:paid:motorHull:gross:Reserves": "Podra:reserveGenerators:subMotorHullReserves:outClaimsLeanDevelopment:paid",
        "Podra:claims:paid:motorHull:ceded": "Podra:linesOfBusiness:subMotorHull:outClaimsDevelopmentLeanCeded:paid",
        "Podra:claims:paid:motorHull:ceded:motorHullQuotaShare": "Podra:reinsurance:subContracts:subMotorHullQuotaShare:outClaimsDevelopmentLeanCeded:paid",
        "Podra:claims:paid:motorHull:ceded:motorHullQuotaShare:Single": "Podra:reinsurance:subContracts:subMotorHullQuotaShare:subMotorHullSingle:outClaimsDevelopmentLeanCeded:paid",
        "Podra:claims:paid:motorHull:ceded:motorHullQuotaShare:Attrtional": "Podra:reinsurance:subContracts:subMotorHullQuotaShare:subMotorHullAttritional:outClaimsDevelopmentLeanCeded:paid",
        "Podra:claims:paid:motorHull:ceded:motorHullQuotaShare:Reserves": "Podra:reinsurance:subContracts:subMotorHullQuotaShare:subMotorHullReserves:outClaimsDevelopmentLeanCeded:paid",
        "Podra:claims:paid:motorHull:ceded:motorHullWXL": "Podra:reinsurance:subContracts:subMotorHullWxl:outClaimsDevelopmentLeanCeded:paid",
        "Podra:claims:paid:motorHull:ceded:motorHullWXL:Single": "Podra:reinsurance:subContracts:subMotorHullWxl:subMotorHullSingle:outClaimsDevelopmentLeanCeded:paid",
        "Podra:claims:paid:motorHull:ceded:motorHullWXL:Attritional": "Podra:reinsurance:subContracts:subMotorHullWxl:subMotorHullAttritional:outClaimsDevelopmentLeanCeded:paid",
        "Podra:claims:paid:motorHull:ceded:motorHullWXL:Reserves": "Podra:reinsurance:subContracts:subMotorHullWxl:subMotorHullReserves:outClaimsDevelopmentLeanCeded:paid",
        "Podra:claims:paid:motorThirdPartyLiability:gross:Single": "Podra:claimsGenerators:subMotorThirdPartyLiabilitySingle:outClaimsLeanDevelopment:paid",
        "Podra:claims:paid:personalAccident:gross:Single": "Podra:claimsGenerators:subPersonalAccidentSingle:outClaimsLeanDevelopment:paid",
]