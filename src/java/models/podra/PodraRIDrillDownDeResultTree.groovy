package models.podra

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = PodraModel
displayName = "Vertragsdaten aufgeschlüsselt"
language = "de"
mappings = [
        "Podra:Rückversicherungsvertrag:Übersicht:[%contract%]": "Podra:reinsurance:subContracts:[%contract%]:outContractFinancials:result",
        "Podra:Rückversicherungsvertrag:Übersicht:[%contract%]:Prämie": "Podra:reinsurance:subContracts:[%contract%]:outContractFinancials:premium",
        "Podra:Rückversicherungsvertrag:Übersicht:[%contract%]:Provision": "Podra:reinsurance:subContracts:[%contract%]:outContractFinancials:commission",
        "Podra:Rückversicherungsvertrag:Übersicht:[%contract%]:Schaden": "Podra:reinsurance:subContracts:[%contract%]:outContractFinancials:claim",
        "Podra:Rückversicherungsvertrag:Schadenquote:[%contract%]": "Podra:reinsurance:subContracts:[%contract%]:outContractFinancials:cededLossRatio",
        "Podra:Rückversicherungsvertrag:Schaden:eingetreten:[%contract%]": "Podra:reinsurance:subContracts:[%contract%]:outClaimsDevelopmentLeanNet:incurred",
        "Podra:Rückversicherungsvertrag:Schaden:eingetreten:zediert:[%contract%]": "Podra:reinsurance:subContracts:[%contract%]:outClaimsDevelopmentLeanCeded:incurred",
        "Podra:Rückversicherungsvertrag:Schaden:eingetreten:zediert:[%contract%]:pro Segment:[%lineOfBusiness%]": "Podra:reinsurance:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanCeded:incurred",
        "Podra:Rückversicherungsvertrag:Schaden:eingetreten:zediert:[%contract%]:pro Risiko:[%peril%]": "Podra:reinsurance:subContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsDevelopmentLeanCeded:incurred",
        "Podra:Rückversicherungsvertrag:Schaden:eingetreten:brutto:[%contract%]": "Podra:reinsurance:subContracts:[%contract%]:outClaimsDevelopmentLeanGross:incurred",
        "Podra:Rückversicherungsvertrag:Schaden:eingetreten:brutto:[%contract%]:pro Segment:[%lineOfBusiness%]": "Podra:reinsurance:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanGross:incurred",
        "Podra:Rückversicherungsvertrag:Schaden:eingetreten:brutto:[%contract%]:pro Risiko:[%peril%]": "Podra:reinsurance:subContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsDevelopmentLeanGross:incurred",
        "Podra:Rückversicherungsvertrag:Schaden:bezahlt:[%contract%]": "Podra:reinsurance:subContracts:[%contract%]:outClaimsDevelopmentLeanNet:paid",
        "Podra:Rückversicherungsvertrag:Schaden:bezahlt:zediert:[%contract%]": "Podra:reinsurance:subContracts:[%contract%]:outClaimsDevelopmentLeanCeded:paid",
        "Podra:Rückversicherungsvertrag:Schaden:bezahlt:zediert:[%contract%]:pro Segment:[%lineOfBusiness%]": "Podra:reinsurance:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanCeded:paid",
        "Podra:Rückversicherungsvertrag:Schaden:bezahlt:zediert:[%contract%]:pro Risiko:[%peril%]": "Podra:reinsurance:subContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsDevelopmentLeanCeded:paid",
        "Podra:Rückversicherungsvertrag:Schaden:bezahlt:brutto:[%contract%]": "Podra:reinsurance:subContracts:[%contract%]:outClaimsDevelopmentLeanGross:paid",
        "Podra:Rückversicherungsvertrag:Schaden:bezahlt:brutto:[%contract%]:pro Segment:[%lineOfBusiness%]": "Podra:reinsurance:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanGross:paid",
        "Podra:Rückversicherungsvertrag:Schaden:bezahlt:brutto:[%contract%]:pro Risiko:[%peril%]": "Podra:reinsurance:subContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsDevelopmentLeanGross:paid",
        "Podra:Rückversicherungsvertrag:Schaden:reserviert:[%contract%]": "Podra:reinsurance:subContracts:[%contract%]:outClaimsDevelopmentLeanNet:reserved",
        "Podra:Rückversicherungsvertrag:Schaden:reserviert:zediert:[%contract%]": "Podra:reinsurance:subContracts:[%contract%]:outClaimsDevelopmentLeanCeded:reserved",
        "Podra:Rückversicherungsvertrag:Schaden:reserviert:zediert:[%contract%]:pro Segment:[%lineOfBusiness%]": "Podra:reinsurance:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanCeded:reserved",
        "Podra:Rückversicherungsvertrag:Schaden:reserviert:zediert:[%contract%]:pro Risiko:[%peril%]": "Podra:reinsurance:subContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsDevelopmentLeanCeded:reserved",
        "Podra:Rückversicherungsvertrag:Schaden:reserviert:brutto:[%contract%]": "Podra:reinsurance:subContracts:[%contract%]:outClaimsDevelopmentLeanGross:reserved",
        "Podra:Rückversicherungsvertrag:Schaden:reserviert:brutto:[%contract%]:pro Segment:[%lineOfBusiness%]": "Podra:reinsurance:subContracts:[%contract%]:linesOfBusiness:[%lineOfBusiness%]:outClaimsDevelopmentLeanGross:reserved",
        "Podra:Rückversicherungsvertrag:Schaden:reserviert:brutto:[%contract%]:pro Risiko:[%peril%]": "Podra:reinsurance:subContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsDevelopmentLeanGross:reserved",
]