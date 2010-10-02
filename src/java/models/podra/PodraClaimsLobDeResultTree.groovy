package models.podra

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = PodraModel
displayName = "Schäden nach Sparten (aufgeschlüsselt)"
language = "de"
mappings = [
        "Podra:Schäden:bezahlt": "Podra:linesOfBusiness:outClaimsDevelopmentLeanNet:paid",
        "Podra:Schäden:bezahlt:[%lineOfBusiness%]": "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanNet:paid",
        "Podra:Schäden:bezahlt:[%lineOfBusiness%]:brutto": "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanGross:paid",
        "Podra:Schäden:bezahlt:[%lineOfBusiness%]:brutto:[%claimsGenerator%]": "Podra:linesOfBusiness:[%lineOfBusiness%]:claimsGenerators:[%claimsGenerator%]:outClaimsDevelopmentLeanGross:paid",
        "Podra:Schäden:bezahlt:[%lineOfBusiness%]:zediert": "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanCeded:paid",
        "Podra:Schäden:bezahlt:[%lineOfBusiness%]:zediert:[%contract%]": "Podra:linesOfBusiness:[%lineOfBusiness%]:reinsuranceContracts:[%contract%]:outClaimsDevelopmentLeanCeded:paid",

        "Podra:Schäden:eingetreten": "Podra:linesOfBusiness:outClaimsDevelopmentLeanNet:incurred",
        "Podra:Schäden:eingetreten:[%lineOfBusiness%]": "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanNet:incurred",
        "Podra:Schäden:eingetreten:[%lineOfBusiness%]:brutto": "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanGross:incurred",
        "Podra:Schäden:eingetreten:[%lineOfBusiness%]:brutto:[%claimsGenerator%]": "Podra:linesOfBusiness:[%lineOfBusiness%]:claimsGenerators:[%claimsGenerator%]:outClaimsDevelopmentLeanGross:incurred",
        "Podra:Schäden:eingetreten:[%lineOfBusiness%]:zediert": "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanCeded:incurred",
        "Podra:Schäden:eingetreten:[%lineOfBusiness%]:zediert:[%contract%]": "Podra:linesOfBusiness:[%lineOfBusiness%]:reinsuranceContracts:[%contract%]:outClaimsDevelopmentLeanCeded:incurred",

        "Podra:Schäden:reserviert": "Podra:linesOfBusiness:outClaimsDevelopmentLeanNet:reserved",
        "Podra:Schäden:reserviert:[%lineOfBusiness%]": "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanNet:reserved",
        "Podra:Schäden:reserviert:[%lineOfBusiness%]:brutto": "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanGross:reserved",
        "Podra:Schäden:reserviert:[%lineOfBusiness%]:brutto:[%claimsGenerator%]": "Podra:linesOfBusiness:[%lineOfBusiness%]:claimsGenerators:[%claimsGenerator%]:outClaimsDevelopmentLeanGross:reserved",
        "Podra:Schäden:reserviert:[%lineOfBusiness%]:zediert": "Podra:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanCeded:reserved",
        "Podra:Schäden:reserviert:[%lineOfBusiness%]:zediert:[%contract%]": "Podra:linesOfBusiness:[%lineOfBusiness%]:reinsuranceContracts:[%contract%]:outClaimsDevelopmentLeanCeded:reserved",

]