package models.onelobsurplus

model = OneLobSurplusModel
periodCount = 2

company {
    LoB {
        components {
            underwriting
            attritionalClaimsGenerator
            frequencyGenerator
            singleClaimsGenerator
            quotaShare
            surplus
            wxl
        }
    }
}

