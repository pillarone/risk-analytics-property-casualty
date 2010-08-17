package models.podra

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = PodraModel
displayName = "Lines of Business"

mappings = [
        "Podra:claims:incurred": "Podra:linesOfBusiness:outClaimsDevelopmentLeanGross:incurred",
        "Podra:claims:incurred:motorHull": "Podra:linesOfBusiness:subMotorHull:outClaimsDevelopmentLeanGross:incurred",
        "Podra:claims:incurred:motorThirdPartyLiability": "Podra:linesOfBusiness:subMotorThirdPartyLiability:outClaimsDevelopmentLeanGross:incurred",
        "Podra:claims:incurred:personalAccident": "Podra:linesOfBusiness:subPersonalAccident:outClaimsDevelopmentLeanGross:incurred",
        "Podra:claims:incurred:property": "Podra:linesOfBusiness:subProperty:outClaimsDevelopmentLeanGross:incurred",
        "Podra:claims:paid": "Podra:linesOfBusiness:outClaimsDevelopmentLeanGross:paid",
        "Podra:claims:paid:motorHull": "Podra:linesOfBusiness:subMotorHull:outClaimsDevelopmentLeanGross:paid",
        "Podra:claims:paid:motorThirdPartyLiability": "Podra:linesOfBusiness:subMotorThirdPartyLiability:outClaimsDevelopmentLeanGross:paid",
        "Podra:claims:paid:personalAccident": "Podra:linesOfBusiness:subPersonalAccident:outClaimsDevelopmentLeanGross:paid",
        "Podra:claims:paid:property": "Podra:linesOfBusiness:subProperty:outClaimsDevelopmentLeanGross:paid",
]