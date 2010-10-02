package models.podra

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = PodraModel
displayName = "Strukturen aufgeschlüsselt"
language = "de"
mappings = [
        "Podra:[%structure%]:Schaden:bezahlt": "Podra:structures:[%structure%]:outClaimsNet:paid",
        "Podra:[%structure%]:Schaden:bezahlt:brutto": "Podra:structures:[%structure%]:outClaimsGross:paid",
        "Podra:[%structure%]:Schaden:bezahlt:brutto:Sparte:[%segment%]": "Podra:structures:[%structure%]:linesOfBusiness:[%segment%]:outClaimsGross:paid",
        "Podra:[%structure%]:Schaden:bezahlt:brutto:Risiko:[%peril%]": "Podra:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsGross:paid",
        "Podra:[%structure%]:Schaden:bezahlt:zediert": "Podra:structures:[%structure%]:outClaimsCeded:paid",
        "Podra:[%structure%]:Schaden:bezahlt:zediert:Sparte:[%segment%]": "Podra:structures:[%structure%]:linesOfBusiness:[%segment%]:outClaimsCeded:paid",
        "Podra:[%structure%]:Schaden:bezahlt:zediert:Risiko:[%peril%]": "Podra:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsCeded:paid",
        "Podra:[%structure%]:Schaden:bezahlt:zediert:Vertrag:[%contract%]": "Podra:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsCeded:paid",
        "Podra:[%structure%]:Schaden:bezahlt:zediert:Vertrag:[%contract%]:Sparte:[%segment%]": "Podra:structures:[%structure%]:linesOfBusiness:[%segment%]:reinsuranceContracts:[%contract%]:outClaimsCeded:paid",
        "Podra:[%structure%]:Schaden:bezahlt:zediert:Vertrag:[%contract%]:Risiko:[%peril%]": "Podra:structures:[%structure%]:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:paid",

        "Podra:[%structure%]:Schaden:eingetreten": "Podra:structures:[%structure%]:outClaimsNet:incurred",
        "Podra:[%structure%]:Schaden:eingetreten:brutto": "Podra:structures:[%structure%]:outClaimsGross:incurred",
        "Podra:[%structure%]:Schaden:eingetreten:brutto:Sparte:[%segment%]": "Podra:structures:[%structure%]:linesOfBusiness:[%segment%]:outClaimsGross:incurred",
        "Podra:[%structure%]:Schaden:eingetreten:brutto:Risiko:[%peril%]": "Podra:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsGross:incurred",
        "Podra:[%structure%]:Schaden:eingetreten:zediert": "Podra:structures:[%structure%]:outClaimsCeded:incurred",
        "Podra:[%structure%]:Schaden:eingetreten:zediert:Sparte:[%segment%]": "Podra:structures:[%structure%]:linesOfBusiness:[%segment%]:outClaimsCeded:incurred",
        "Podra:[%structure%]:Schaden:eingetreten:zediert:Risiko:[%peril%]": "Podra:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsCeded:incurred",
        "Podra:[%structure%]:Schaden:eingetreten:zediert:Vertrag:[%contract%]": "Podra:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsCeded:incurred",
        "Podra:[%structure%]:Schaden:eingetreten:zediert:Vertrag:[%contract%]:Sparte:[%segment%]": "Podra:structures:[%structure%]:linesOfBusiness:[%segment%]:reinsuranceContracts:[%contract%]:outClaimsCeded:incurred",
        "Podra:[%structure%]:Schaden:eingetreten:zediert:Vertrag:[%contract%]:Risiko:[%peril%]": "Podra:structures:[%structure%]:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:incurred",
                             
        "Podra:[%structure%]:Schaden:reserviert": "Podra:structures:[%structure%]:outClaimsNet:reserved",
        "Podra:[%structure%]:Schaden:reserviert:brutto": "Podra:structures:[%structure%]:outClaimsGross:reserved",
        "Podra:[%structure%]:Schaden:reserviert:brutto:Sparte:[%segment%]": "Podra:structures:[%structure%]:linesOfBusiness:[%segment%]:outClaimsGross:reserved",
        "Podra:[%structure%]:Schaden:reserviert:brutto:Risiko:[%peril%]": "Podra:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsGross:reserved",
        "Podra:[%structure%]:Schaden:reserviert:zediert": "Podra:structures:[%structure%]:outClaimsCeded:reserved",
        "Podra:[%structure%]:Schaden:reserviert:zediert:Sparte:[%segment%]": "Podra:structures:[%structure%]:linesOfBusiness:[%segment%]:outClaimsCeded:reserved",
        "Podra:[%structure%]:Schaden:reserviert:zediert:Risiko:[%peril%]": "Podra:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsCeded:reserved",
        "Podra:[%structure%]:Schaden:reserviert:zediert:Vertrag:[%contract%]": "Podra:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsCeded:reserved",
        "Podra:[%structure%]:Schaden:reserviert:zediert:Vertrag:[%contract%]:Sparte:[%segment%]": "Podra:structures:[%structure%]:linesOfBusiness:[%segment%]:reinsuranceContracts:[%contract%]:outClaimsCeded:reserved",
        "Podra:[%structure%]:Schaden:reserviert:zediert:Vertrag:[%contract%]:Risiko:[%peril%]": "Podra:structures:[%structure%]:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:reserved",

        "Podra:[%structure%]:Zeichnungsinformation:Prämie": "Podra:structures:[%structure%]:outUnderwritingInfoNet:premium",
        "Podra:[%structure%]:Zeichnungsinformation:Prämie:brutto": "Podra:structures:[%structure%]:outUnderwritingInfoGross:premium",
        "Podra:[%structure%]:Zeichnungsinformation:Prämie:brutto:Sparte:[%segment%]": "Podra:structures:[%structure%]:linesOfBusiness:[%segment%]:outUnderwritingInfoGross:premium",
        "Podra:[%structure%]:Zeichnungsinformation:Prämie:zediert": "Podra:structures:[%structure%]:outUnderwritingInfoCeded:premium",
        "Podra:[%structure%]:Zeichnungsinformation:Prämie:zediert:Sparte:[%segment%]": "Podra:structures:[%structure%]:linesOfBusiness:[%segment%]:outUnderwritingInfoCeded:premium",
        "Podra:[%structure%]:Zeichnungsinformation:Prämie:zediert:Vertrag:[%contract%]": "Podra:structures:[%structure%]:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premium",

        "Podra:[%structure%]:Zeichnungsinformation:Provision": "Podra:structures:[%structure%]:outUnderwritingInfoNet:commission",
        "Podra:[%structure%]:Zeichnungsinformation:Provision:brutto": "Podra:structures:[%structure%]:outUnderwritingInfoGross:commission",
        "Podra:[%structure%]:Zeichnungsinformation:Provision:brutto:Sparte:[%segment%]": "Podra:structures:[%structure%]:linesOfBusiness:[%segment%]:outUnderwritingInfoGross:commission",
        "Podra:[%structure%]:Zeichnungsinformation:Provision:zediert": "Podra:structures:[%structure%]:outUnderwritingInfoCeded:commission",
        "Podra:[%structure%]:Zeichnungsinformation:Provision:zediert:Sparte:[%segment%]": "Podra:structures:[%structure%]:linesOfBusiness:[%segment%]:outUnderwritingInfoCeded:commission",
        "Podra:[%structure%]:Zeichnungsinformation:Provision:zediert:Vertrag:[%contract%]": "Podra:structures:[%structure%]:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:commission",
]