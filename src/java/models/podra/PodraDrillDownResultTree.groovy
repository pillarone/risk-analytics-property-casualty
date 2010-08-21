package models.podra

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = PodraModel
displayName = "Drill down"
mappings = [
        "Podra:claims:paid:[%subcomponents%]": "Podra:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanNet:paid",
        "Podra:claims:paid:[%subcomponents%]:gross": "Podra:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanGross:paid",
        "Podra:claims:paid:[%subcomponents%]:gross:Single": "Podra:claimsGenerators:[%subcomponents%]Single:outClaimsLeanDevelopment:paid",
        "Podra:claims:paid:[%subcomponents%]:gross:Attritional": "Podra:claimsGenerators:[%subcomponents%]Attritional:outClaimsLeanDevelopment:paid",
        "Podra:claims:paid:[%subcomponents%]:gross:Reserves": "Podra:reserveGenerators:[%subcomponents%]Reserves:outClaimsLeanDevelopment:paid",
        "Podra:claims:paid:[%subcomponents%]:ceded": "Podra:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanCeded:paid",
        "Podra:claims:paid:[%subcomponents%]:ceded:motorHullQuotaShare": "Podra:reinsurance:subContracts:[%subcomponents%]QuotaShare:outClaimsDevelopmentLeanCeded:paid",
        "Podra:claims:paid:[%subcomponents%]:ceded:motorHullQuotaShare:Single": "Podra:reinsurance:subContracts:[%subcomponents%]QuotaShare:[%subcomponents%]Single:outClaimsDevelopmentLeanCeded:paid",
        "Podra:claims:paid:[%subcomponents%]:ceded:motorHullQuotaShare:Attrtional": "Podra:reinsurance:subContracts:[%subcomponents%]QuotaShare:[%subcomponents%]Attritional:outClaimsDevelopmentLeanCeded:paid",
        "Podra:claims:paid:[%subcomponents%]:ceded:motorHullQuotaShare:Reserves": "Podra:reinsurance:subContracts:[%subcomponents%]QuotaShare:[%subcomponents%]Reserves:outClaimsDevelopmentLeanCeded:paid",
        "Podra:claims:paid:[%subcomponents%]:ceded:motorHullWXL": "Podra:reinsurance:subContracts:[%subcomponents%]Wxl:outClaimsDevelopmentLeanCeded:paid",
        "Podra:claims:paid:[%subcomponents%]:ceded:motorHullWXL:Single": "Podra:reinsurance:subContracts:[%subcomponents%]Wxl:[%subcomponents%]Single:outClaimsDevelopmentLeanCeded:paid",
        "Podra:claims:paid:[%subcomponents%]:ceded:motorHullWXL:Attritional": "Podra:reinsurance:subContracts:[%subcomponents%]Wxl:[%subcomponents%]Attritional:outClaimsDevelopmentLeanCeded:paid",
        "Podra:claims:paid:[%subcomponents%]:ceded:motorHullWXL:Reserves": "Podra:reinsurance:subContracts:[%subcomponents%]Wxl:[%subcomponents%]Reserves:outClaimsDevelopmentLeanCeded:paid",
        
        "Podra:claims:incurred:[%subcomponents%]": "Podra:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanNet:incurred",
        "Podra:claims:incurred:[%subcomponents%]:gross": "Podra:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanGross:incurred",
        "Podra:claims:incurred:[%subcomponents%]:gross:Single": "Podra:claimsGenerators:[%subcomponents%]Single:outClaimsLeanDevelopment:incurred",
        "Podra:claims:incurred:[%subcomponents%]:gross:Attritional": "Podra:claimsGenerators:[%subcomponents%]Attritional:outClaimsLeanDevelopment:incurred",
        "Podra:claims:incurred:[%subcomponents%]:gross:Reserves": "Podra:reserveGenerators:[%subcomponents%]Reserves:outClaimsLeanDevelopment:incurred",
        "Podra:claims:incurred:[%subcomponents%]:ceded": "Podra:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanCeded:incurred",
        "Podra:claims:incurred:[%subcomponents%]:ceded:motorHullQuotaShare": "Podra:reinsurance:subContracts:[%subcomponents%]QuotaShare:outClaimsDevelopmentLeanCeded:incurred",
        "Podra:claims:incurred:[%subcomponents%]:ceded:motorHullQuotaShare:Single": "Podra:reinsurance:subContracts:[%subcomponents%]QuotaShare:[%subcomponents%]Single:outClaimsDevelopmentLeanCeded:incurred",
        "Podra:claims:incurred:[%subcomponents%]:ceded:motorHullQuotaShare:Attrtional": "Podra:reinsurance:subContracts:[%subcomponents%]QuotaShare:[%subcomponents%]Attritional:outClaimsDevelopmentLeanCeded:incurred",
        "Podra:claims:incurred:[%subcomponents%]:ceded:motorHullQuotaShare:Reserves": "Podra:reinsurance:subContracts:[%subcomponents%]QuotaShare:[%subcomponents%]Reserves:outClaimsDevelopmentLeanCeded:incurred",
        "Podra:claims:incurred:[%subcomponents%]:ceded:motorHullWXL": "Podra:reinsurance:subContracts:[%subcomponents%]Wxl:outClaimsDevelopmentLeanCeded:incurred",
        "Podra:claims:incurred:[%subcomponents%]:ceded:motorHullWXL:Single": "Podra:reinsurance:subContracts:[%subcomponents%]Wxl:[%subcomponents%]Single:outClaimsDevelopmentLeanCeded:incurred",
        "Podra:claims:incurred:[%subcomponents%]:ceded:motorHullWXL:Attritional": "Podra:reinsurance:subContracts:[%subcomponents%]Wxl:[%subcomponents%]Attritional:outClaimsDevelopmentLeanCeded:incurred",
        "Podra:claims:incurred:[%subcomponents%]:ceded:motorHullWXL:Reserves": "Podra:reinsurance:subContracts:[%subcomponents%]Wxl:[%subcomponents%]Reserves:outClaimsDevelopmentLeanCeded:incurred",
        
        "Podra:claims:reserved:[%subcomponents%]": "Podra:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanNet:reserved",
        "Podra:claims:reserved:[%subcomponents%]:gross": "Podra:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanGross:reserved",
        "Podra:claims:reserved:[%subcomponents%]:gross:Single": "Podra:claimsGenerators:[%subcomponents%]Single:outClaimsLeanDevelopment:reserved",
        "Podra:claims:reserved:[%subcomponents%]:gross:Attritional": "Podra:claimsGenerators:[%subcomponents%]Attritional:outClaimsLeanDevelopment:reserved",
        "Podra:claims:reserved:[%subcomponents%]:gross:Reserves": "Podra:reserveGenerators:[%subcomponents%]Reserves:outClaimsLeanDevelopment:reserved",
        "Podra:claims:reserved:[%subcomponents%]:ceded": "Podra:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanCeded:reserved",
        "Podra:claims:reserved:[%subcomponents%]:ceded:motorHullQuotaShare": "Podra:reinsurance:subContracts:[%subcomponents%]QuotaShare:outClaimsDevelopmentLeanCeded:reserved",
        "Podra:claims:reserved:[%subcomponents%]:ceded:motorHullQuotaShare:Single": "Podra:reinsurance:subContracts:[%subcomponents%]QuotaShare:[%subcomponents%]Single:outClaimsDevelopmentLeanCeded:reserved",
        "Podra:claims:reserved:[%subcomponents%]:ceded:motorHullQuotaShare:Attrtional": "Podra:reinsurance:subContracts:[%subcomponents%]QuotaShare:[%subcomponents%]Attritional:outClaimsDevelopmentLeanCeded:reserved",
        "Podra:claims:reserved:[%subcomponents%]:ceded:motorHullQuotaShare:Reserves": "Podra:reinsurance:subContracts:[%subcomponents%]QuotaShare:[%subcomponents%]Reserves:outClaimsDevelopmentLeanCeded:reserved",
        "Podra:claims:reserved:[%subcomponents%]:ceded:motorHullWXL": "Podra:reinsurance:subContracts:[%subcomponents%]Wxl:outClaimsDevelopmentLeanCeded:reserved",
        "Podra:claims:reserved:[%subcomponents%]:ceded:motorHullWXL:Single": "Podra:reinsurance:subContracts:[%subcomponents%]Wxl:[%subcomponents%]Single:outClaimsDevelopmentLeanCeded:reserved",
        "Podra:claims:reserved:[%subcomponents%]:ceded:motorHullWXL:Attritional": "Podra:reinsurance:subContracts:[%subcomponents%]Wxl:[%subcomponents%]Attritional:outClaimsDevelopmentLeanCeded:reserved",
        "Podra:claims:reserved:[%subcomponents%]:ceded:motorHullWXL:Reserves": "Podra:reinsurance:subContracts:[%subcomponents%]Wxl:[%subcomponents%]Reserves:outClaimsDevelopmentLeanCeded:reserved",
]