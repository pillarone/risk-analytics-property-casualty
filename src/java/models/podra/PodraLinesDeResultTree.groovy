package models.podra

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = PodraModel
displayName = "Sparten"
language = "de"

mappings = [
        "Podra:Schäden:netto:eingetreten": "Podra:linesOfBusiness:outClaimsDevelopmentLeanNet:incurred",
        "Podra:Schäden:netto:eingetreten:[%subcomponents%]": "Podra:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanNet:incurred",
        "Podra:Schäden:netto:bezahlt": "Podra:linesOfBusiness:outClaimsDevelopmentLeanNet:paid",
        "Podra:Schäden:netto:bezahlt:[%subcomponents%]": "Podra:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanNet:paid",
        "Podra:Schäden:netto:reserviert": "Podra:linesOfBusiness:outClaimsDevelopmentLeanNet:reserved",
        "Podra:Schäden:netto:reserviert:[%subcomponents%]": "Podra:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanNet:reserved",
        "Podra:Schäden:brutto:eingetreten": "Podra:linesOfBusiness:outClaimsDevelopmentLeanGross:incurred",
        "Podra:Schäden:brutto:eingetreten:[%subcomponents%]": "Podra:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanGross:incurred",
        "Podra:Schäden:brutto:bezahlt": "Podra:linesOfBusiness:outClaimsDevelopmentLeanGross:paid",
        "Podra:Schäden:brutto:bezahlt:[%subcomponents%]": "Podra:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanGross:paid",
        "Podra:Schäden:brutto:reserviert": "Podra:linesOfBusiness:outClaimsDevelopmentLeanGross:reserved",
        "Podra:Schäden:brutto:reserviert:[%subcomponents%]": "Podra:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanGross:reserved",
        "Podra:Schäden:zediert:eingetreten": "Podra:linesOfBusiness:outClaimsDevelopmentLeanCeded:incurred",
        "Podra:Schäden:zediert:eingetreten:[%subcomponents%]": "Podra:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanCeded:incurred",
        "Podra:Schäden:zediert:bezahlt": "Podra:linesOfBusiness:outClaimsDevelopmentLeanCeded:paid",
        "Podra:Schäden:zediert:bezahlt:[%subcomponents%]": "Podra:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanCeded:paid",
        "Podra:Schäden:zediert:reserviert": "Podra:linesOfBusiness:outClaimsDevelopmentLeanCeded:reserved",
        "Podra:Schäden:zediert:reserviert:[%subcomponents%]": "Podra:linesOfBusiness:[%subcomponents%]:outClaimsDevelopmentLeanCeded:reserved",

        "Podra:Zeichnungsinformation:Prämie:netto": "Podra.linesOfBusiness:outUnderwritingInfoNet.premiumWritten",
        "Podra:Zeichnungsinformation:Prämie:netto:[%subcomponents%]": "Podra.linesOfBusiness:[%subcomponents%]:outUnderwritingInfoNet.commission",
        "Podra:Zeichnungsinformation:Provision:netto": "Podra.linesOfBusiness:outUnderwritingInfoNet.premiumWritten",
        "Podra:Zeichnungsinformation:Provision:netto:[%subcomponents%]": "Podra.linesOfBusiness:[%subcomponents%]:outUnderwritingInfoNet.commission",
        "Podra:Zeichnungsinformation:Prämie:brutto": "Podra.linesOfBusiness:outUnderwritingInfoGross.premiumWritten",
        "Podra:Zeichnungsinformation:Prämie:brutto:[%subcomponents%]": "Podra.linesOfBusiness:[%subcomponents%]:outUnderwritingInfoGross.commission",
        "Podra:Zeichnungsinformation:Provision:brutto": "Podra.linesOfBusiness:outUnderwritingInfoGross.premiumWritten",
        "Podra:Zeichnungsinformation:Provision:brutto:[%subcomponents%]": "Podra.linesOfBusiness:[%subcomponents%]:outUnderwritingInfoGross.commission",
        "Podra:Zeichnungsinformation:Prämie:zediert": "Podra.linesOfBusiness:outUnderwritingInfoCeded.premiumWritten",
        "Podra:Zeichnungsinformation:Prämie:zediert:[%subcomponents%]": "Podra.linesOfBusiness:[%subcomponents%]:outUnderwritingInfoCeded.commission",
        "Podra:Zeichnungsinformation:Provision:zediert": "Podra.linesOfBusiness:outUnderwritingInfoCeded.premiumWritten",
        "Podra:Zeichnungsinformation:Provision:zediert:[%subcomponents%]": "Podra.linesOfBusiness:[%subcomponents%]:outUnderwritingInfoCeded.commission",
]