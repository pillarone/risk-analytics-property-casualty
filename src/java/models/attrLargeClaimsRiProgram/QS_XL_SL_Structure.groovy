package models.attrLargeClaimsRiProgram

model = QS_XL_SL_Model
periodCount = 3

company {
    Fire {
        components {
            claimsGenerator
            attritionalClaimsGenerator
            wxl
            quotaShare
            aggregator
            stopLoss
        }
    }
}

