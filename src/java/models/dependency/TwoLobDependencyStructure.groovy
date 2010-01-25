package models.dependency

model = TwoLobDependencyModel
periodCount = 1

company {
    /*Dependencies {*/
        Frequencies {
            components {
                frequencyGeneratorEvent
                frequencyGeneratorLarge
                independentFrequencyGeneratorHull
                independentFrequencyGeneratorFire
            }
        }
        Severities {
            components {
                copulaEvent
                copulaLarge
                copulaAttritional
            }
        }
    //}
    Reinsurance {
        components {
            cxl
        }
    }
}