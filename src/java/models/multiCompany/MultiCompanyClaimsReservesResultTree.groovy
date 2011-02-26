package models.multiCompany

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = MultiCompanyModel
displayName = "Claims and Reserve Generators"
language = "en"

mappings = {
    "MultiCompany" {
        "groupedByAttribute" {
            "claimsGenerators" {
                "paid" "MultiCompany:claimsGenerators:outClaimsLeanDevelopment:paid", {
                    "[%claimsGenerator%]" "MultiCompany:claimsGenerators:[%claimsGenerator%]:outClaimsLeanDevelopment:paid"
                }
                "incurred" "MultiCompany:claimsGenerators:outClaimsLeanDevelopment:incurred", {
                    "[%claimsGenerator%]" "MultiCompany:claimsGenerators:[%claimsGenerator%]:outClaimsLeanDevelopment:incurred"
                }
                "reserved" "MultiCompany:claimsGenerators:outClaimsLeanDevelopment:reserved", {
                    "[%claimsGenerator%]" "MultiCompany:claimsGenerators:[%claimsGenerator%]:outClaimsLeanDevelopment:reserved"
                }
            }
            "reserveGenerators" {
                "paid" "MultiCompany:reserveGenerators:outClaimsLeanDevelopment:paid", {
                    "[%reserveGenerator%]" "MultiCompany:reserveGenerators:[%reserveGenerator%]:outClaimsLeanDevelopment:paid"
                }
                "incurred" "MultiCompany:reserveGenerators:outClaimsLeanDevelopment:incurred", {
                    "[%reserveGenerator%]" "MultiCompany:reserveGenerators:[%reserveGenerator%]:outClaimsLeanDevelopment:incurred"
                }
                "reserved" "MultiCompany:reserveGenerators:outClaimsLeanDevelopment:reserved", {
                    "[%reserveGenerator%]" "MultiCompany:reserveGenerators:[%reserveGenerator%]:outClaimsLeanDevelopment:reserved"
                }
            }
        }
        "groupedByGenerator" {
            "claimsGenerators" {
                "[%claimsGenerator%]" {
                    "paid" "MultiCompany:claimsGenerators:[%claimsGenerator%]:outClaimsLeanDevelopment:paid"
                    "incurred" "MultiCompany:claimsGenerators:[%claimsGenerator%]:outClaimsLeanDevelopment:incurred"
                    "reserved" "MultiCompany:claimsGenerators:[%claimsGenerator%]:outClaimsLeanDevelopment:reserved"
                }
            }
            "reserveGenerators" {
                "[%reserveGenerator%]" {
                    "paid" "MultiCompany:reserveGenerators:[%reserveGenerator%]:outClaimsLeanDevelopment:paid"
                    "incurred" "MultiCompany:reserveGenerators:[%reserveGenerator%]:outClaimsLeanDevelopment:incurred"
                    "reserved" "MultiCompany:reserveGenerators:[%reserveGenerator%]:outClaimsLeanDevelopment:reserved"
                }
            }
        }
    }
}