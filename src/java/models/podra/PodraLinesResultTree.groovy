package models.podra

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = PodraModel
displayName = "Lines of Business"

mappings = [
        "Podra:linesOfBusiness:outClaimsDevelopmentLeanGross:incurred":"Podra:claims:incurred",
        "Podra:linesOfBusiness:subMotorHull:outClaimsDevelopmentLeanGross:incurred":"Podra:claims:incurred:motorHull",
        "Podra:linesOfBusiness:subMotorThirdPartyLiability:outClaimsDevelopmentLeanGross:incurred":"Podra:claims:incurred:motorThirdPartyLiability",
        "Podra:linesOfBusiness:subPersonalAccident:outClaimsDevelopmentLeanGross:incurred":"Podra:claims:incurred:personalAccident",
        "Podra:linesOfBusiness:subProperty:outClaimsDevelopmentLeanGross:incurred":"Podra:claims:incurred:property",
        "Podra:linesOfBusiness:outClaimsDevelopmentLeanGross:paid":"Podra:claims:paid",
        "Podra:linesOfBusiness:subMotorHull:outClaimsDevelopmentLeanGross:paid":"Podra:claims:paid:motorHull",
        "Podra:linesOfBusiness:subMotorThirdPartyLiability:outClaimsDevelopmentLeanGross:paid":"Podra:claims:paid:motorThirdPartyLiability",
        "Podra:linesOfBusiness:subPersonalAccident:outClaimsDevelopmentLeanGross:paid":"Podra:claims:paid:personalAccident",
        "Podra:linesOfBusiness:subProperty:outClaimsDevelopmentLeanGross:paid":"Podra:claims:paid:property",
]