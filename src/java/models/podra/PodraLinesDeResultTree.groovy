package models.podra

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = PodraModel
displayName = "de: Lines of Business"
language = "de"

mappings = [
        "Podra:claims:net:incurred": "Podra:linesOfBusiness:outClaimsDevelopmentLeanNet:incurred",
        "Podra:claims:net:incurred:[%subcomponents%]": "Podra:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanNet:incurred",
        "Podra:claims:net:paid": "Podra:linesOfBusiness:outClaimsDevelopmentLeanNet:paid",
        "Podra:claims:net:paid:[%subcomponents%]": "Podra:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanNet:paid",
        "Podra:claims:net:reserved": "Podra:linesOfBusiness:outClaimsDevelopmentLeanNet:reserved",
        "Podra:claims:net:reserved:[%subcomponents%]": "Podra:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanNet:reserved",
        "Podra:claims:gross:incurred": "Podra:linesOfBusiness:outClaimsDevelopmentLeanGross:incurred",
        "Podra:claims:gross:incurred:[%subcomponents%]": "Podra:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanGross:incurred",
        "Podra:claims:gross:paid": "Podra:linesOfBusiness:outClaimsDevelopmentLeanGross:paid",
        "Podra:claims:gross:paid:[%subcomponents%]": "Podra:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanGross:paid",
        "Podra:claims:gross:reserved": "Podra:linesOfBusiness:outClaimsDevelopmentLeanGross:reserved",
        "Podra:claims:gross:reserved:[%subcomponents%]": "Podra:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanGross:reserved",
        "Podra:claims:ceded:incurred": "Podra:linesOfBusiness:outClaimsDevelopmentLeanCeded:incurred",
        "Podra:claims:ceded:incurred:[%subcomponents%]": "Podra:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanCeded:incurred",
        "Podra:claims:ceded:paid": "Podra:linesOfBusiness:outClaimsDevelopmentLeanCeded:paid",
        "Podra:claims:ceded:paid:[%subcomponents%]": "Podra:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanCeded:paid",
        "Podra:claims:ceded:reserved": "Podra:linesOfBusiness:outClaimsDevelopmentLeanCeded:reserved",
        "Podra:claims:ceded:reserved:[%subcomponents%]": "Podra:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanCeded:reserved",

        "Podra:underwritingInfo:premium:net": "Podra.linesOfBusiness:outUnderwritingInfoNet.premiumWritten",
        "Podra:underwritingInfo:premium:net:[%subcomponents%]": "Podra.linesOfBusiness:[%subcomponents%]:outUnderwritingInfoNet.commission",
        "Podra:underwritingInfo:commission:net": "Podra.linesOfBusiness:outUnderwritingInfoNet.premiumWritten",
        "Podra:underwritingInfo:commission:net:[%subcomponents%]": "Podra.linesOfBusiness:[%subcomponents%]:outUnderwritingInfoNet.commission",
        "Podra:underwritingInfo:premium:gross": "Podra.linesOfBusiness:outUnderwritingInfoGross.premiumWritten",
        "Podra:underwritingInfo:premium:gross:[%subcomponents%]": "Podra.linesOfBusiness:[%subcomponents%]:outUnderwritingInfoGross.commission",
        "Podra:underwritingInfo:commission:gross": "Podra.linesOfBusiness:outUnderwritingInfoGross.premiumWritten",
        "Podra:underwritingInfo:commission:gross:[%subcomponents%]": "Podra.linesOfBusiness:[%subcomponents%]:outUnderwritingInfoGross.commission",
        "Podra:underwritingInfo:premium:ceded": "Podra.linesOfBusiness:outUnderwritingInfoCeded.premiumWritten",
        "Podra:underwritingInfo:premium:ceded:[%subcomponents%]": "Podra.linesOfBusiness:[%subcomponents%]:outUnderwritingInfoCeded.commission",
        "Podra:underwritingInfo:commission:ceded": "Podra.linesOfBusiness:outUnderwritingInfoCeded.premiumWritten",
        "Podra:underwritingInfo:commission:ceded:[%subcomponents%]": "Podra.linesOfBusiness:[%subcomponents%]:outUnderwritingInfoCeded.commission",
]