package models.podra

import org.pillarone.riskanalytics.core.parameterization.ComboBoxMatrixMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.util.GroovyUtils
import org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase
import org.pillarone.riskanalytics.domain.pc.constants.FrequencySeverityClaimType
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.domain.pc.constants.ReinsuranceContractBase
import org.pillarone.riskanalytics.domain.pc.constants.ReinsuranceContractPremiumBase
import org.pillarone.riskanalytics.domain.pc.constants.StopLossContractBase
import org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType
import org.pillarone.riskanalytics.domain.pc.generators.copulas.CopulaStrategyFactory
import org.pillarone.riskanalytics.domain.pc.generators.copulas.PerilCopulaType
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.PremiumAllocationType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.CoverAttributeStrategyType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.limit.LimitStrategyType
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker
import org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker
import org.pillarone.riskanalytics.domain.pc.generators.copulas.CopulaStrategyFactory
import org.pillarone.riskanalytics.domain.pc.generators.copulas.PerilCopulaType

model=models.podra.PodraModel
periodCount=1
displayName='CapitalEagle PEAK'
applicationVersion='1.1.2'
components {
    linesOfBusiness {
        subProperty {
            subReservesFilter {
                parmPortions[0]=new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([[]]),["Reserves","Portion of Claims"], ConstraintsFactory.getConstraints('RESERVE_PORTION'))
            }
            subClaimsFilter {
                parmPortions[0]=new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([["subPropertyAttritional", "subPropertyLarge", "subPropertyStorm", "subPropertyFlood", "subPropertyEarthquake"], [1.0, 1.0, 1.0, 1.0, 1.0]]),["Claims Generator","Portion of Claims"], ConstraintsFactory.getConstraints('PERIL_PORTION'))
            }
            subUnderwritingFilter {
                parmPortions[0]=new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([["subProperty"], [1.0]]),["Underwriting","Portion"], ConstraintsFactory.getConstraints('UNDERWRITING_PORTION'))
            }
        }
        subMotorHull {
            subUnderwritingFilter {
                parmPortions[0]=new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([["subMotorHull"], [1.0]]),["Underwriting","Portion"], ConstraintsFactory.getConstraints('UNDERWRITING_PORTION'))
            }
            subReservesFilter {
                parmPortions[0]=new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([[]]),["Reserves","Portion of Claims"], ConstraintsFactory.getConstraints('RESERVE_PORTION'))
            }
            subClaimsFilter {
                parmPortions[0]=new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([["subMotorHullAttritional", "subMotorHullCat"], [1.0, 1.0]]),["Claims Generator","Portion of Claims"], ConstraintsFactory.getConstraints('PERIL_PORTION'))
            }
        }
        subPersonalAccident {
            subReservesFilter {
                parmPortions[0]=new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([[]]),["Reserves","Portion of Claims"], ConstraintsFactory.getConstraints('RESERVE_PORTION'))
            }
            subUnderwritingFilter {
                parmPortions[0]=new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([["subPersonalAccident"], [1.0]]),["Underwriting","Portion"], ConstraintsFactory.getConstraints('UNDERWRITING_PORTION'))
            }
            subClaimsFilter {
                parmPortions[0]=new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([["subPersonalAccidentAttritional", "subPersonalAccidentLarge", "subPersonalAccidentCat"], [1.0, 1.0, 1.0]]),["Claims Generator","Portion of Claims"], ConstraintsFactory.getConstraints('PERIL_PORTION'))
            }
        }
        subMotorThirdPartyLiability {
            subUnderwritingFilter {
                parmPortions[0]=new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([["subMotorThirdPartyLiability"], [1.0]]),["Underwriting","Portion"], ConstraintsFactory.getConstraints('UNDERWRITING_PORTION'))
            }
            subReservesFilter {
                parmPortions[0]=new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([[]]),["Reserves","Portion of Claims"], ConstraintsFactory.getConstraints('RESERVE_PORTION'))
            }
            subClaimsFilter {
                parmPortions[0]=new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([["subMotorThirdPartyLiabilityAttritional", "subMotorThirdPartyLiabilityLarge"], [1.0, 1.0]]),["Claims Generator","Portion of Claims"], ConstraintsFactory.getConstraints('PERIL_PORTION'))
            }
        }
    }
    claimsGenerators {
        subMotorThirdPartyLiabilityLarge {
            parmPeriodPaymentPortion[0]=1.0
            parmClaimsModel[0]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.FREQUENCY_SEVERITY, ["frequencyBase":FrequencyBase.ABSOLUTE,"frequencyDistribution":DistributionType.getStrategy(DistributionType.POISSON, [lambda:4.874]),"frequencyModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),"claimsSizeBase":Exposure.ABSOLUTE,"claimsSizeDistribution":DistributionType.getStrategy(DistributionType.PARETO, [alpha:1.416, beta:1000000.0]),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.CENSORED, ["min":1000000.0,"max":1.0E8,]),"produceClaim":FrequencySeverityClaimType.SINGLE,])
            parmUnderwritingInformation[0]=new ComboBoxTableMultiDimensionalParameter(GroovyUtils.toList([["subMotorThirdPartyLiability"]]),["Underwriting Information"], IUnderwritingInfoMarker)
            parmAssociateExposureInfo[0]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
        }
        subPropertyLarge {
            parmClaimsModel[0]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.FREQUENCY_SEVERITY, ["frequencyBase":FrequencyBase.ABSOLUTE,"frequencyDistribution":DistributionType.getStrategy(DistributionType.POISSON, [lambda:13.32]),"frequencyModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),"claimsSizeBase":Exposure.ABSOLUTE,"claimsSizeDistribution":DistributionType.getStrategy(DistributionType.PARETO, [alpha:1.76, beta:70000.0]),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.TRUNCATED, ["min":70000.0,"max":1000000.0,]),"produceClaim":FrequencySeverityClaimType.SINGLE,])
            parmPeriodPaymentPortion[0]=1.0
            parmAssociateExposureInfo[0]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
            parmUnderwritingInformation[0]=new ComboBoxTableMultiDimensionalParameter(GroovyUtils.toList([["subProperty"]]),["Underwriting Information"], IUnderwritingInfoMarker)
        }
        subPersonalAccidentAttritional {
            parmUnderwritingInformation[0]=new ComboBoxTableMultiDimensionalParameter(GroovyUtils.toList([["subPersonalAccident"]]),["Underwriting Information"], IUnderwritingInfoMarker)
            parmPeriodPaymentPortion[0]=1.0
            parmClaimsModel[0]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase":Exposure.PREMIUM_WRITTEN,"claimsSizeDistribution":DistributionType.getStrategy(DistributionType.LOGNORMAL, [mean:0.39, stDev:0.09]),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])
            parmAssociateExposureInfo[0]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
        }
        subPropertyEarthquake {
            parmPeriodPaymentPortion[0]=1.0
            parmUnderwritingInformation[0]=new ComboBoxTableMultiDimensionalParameter(GroovyUtils.toList([["subProperty"]]),["Underwriting Information"], IUnderwritingInfoMarker)
            parmClaimsModel[0]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.PML, ["pmlData":new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([['[3.98, 4.22, 4.47, 4.73, 5.01, 5.31, 5.62, 5.96, 6.31, 6.68, 7.08, 7.5, 7.94, 8.41, 8.91, 9.44, 10.0, 10.59, 11.22, 11.89, 12.59, 13.34, 14.13, 14.96, 15.85, 16.79, 17.78, 18.84, 19.95, 21.13, 22.39, 23.71, 25.12, 26.61, 28.18, 29.85, 31.62, 33.5, 35.48, 37.58, 39.81, 42.17, 44.67, 47.32, 50.12, 53.09, 56.23, 59.57, 63.1, 66.83, 70.79, 74.99, 79.43, 84.14, 89.13, 94.41, 100.0, 105.93, 112.2, 118.85, 125.89, 133.35, 141.25, 149.62, 158.49, 167.88, 177.83, 188.36, 199.53, 211.35, 223.87, 237.14, 251.19, 266.07, 281.84, 298.54, 316.23, 334.97, 354.81, 375.84, 398.11, 421.7, 446.68, 473.15, 501.19, 530.88, 562.34, 595.66, 630.96, 668.34, 707.95, 749.89, 794.33, 841.4, 891.25, 944.06, 1000.0, 1059.25, 1122.02, 1188.5, 1258.93, 1333.52, 1412.54, 1496.24, 1584.89, 1678.8, 1778.28, 1883.65, 1995.26, 2113.49, 2238.72, 2371.37, 2511.89, 2660.73, 2818.38, 2985.38, 3162.28, 3349.65, 3548.13, 3758.37, 3981.07, 4216.97, 4466.84, 4731.51, 5011.87, 5308.84, 5623.41, 5956.62, 6309.57, 6683.44, 7079.46, 7498.94, 7943.28, 8413.95, 8912.51, 9440.61, 10000.0, 10592.54, 11220.18, 11885.02, 12589.25, 13335.21, 14125.38, 14962.36, 15848.93, 16788.04, 17782.79, 18836.49, 19952.62, 21134.89, 22387.21, 23713.74, 25118.86, 26607.25, 28183.83, 29853.83, 31622.78, 33496.54, 35481.34, 37583.74, 39810.72, 42169.65, 44668.36, 47315.13, 50118.72, 53088.44, 56234.13, 59566.21, 63095.73, 66834.39, 70794.58, 74989.42, 79432.82, 84139.51, 89125.09, 94406.09, 99996.0]'], ['[451.0, 1209.0, 2508.0, 3796.0, 5980.0, 8617.0, 12910.0, 18243.0, 23539.0, 31179.0, 39827.0, 49393.0, 64133.0, 80773.0, 102612.0, 127314.0, 147050.0, 178020.0, 211280.0, 248664.0, 289048.0, 325566.0, 367729.0, 421080.0, 476459.0, 512126.0, 585945.0, 633543.0, 684595.0, 736229.0, 814346.0, 875593.0, 929098.0, 1010220.0, 1082556.0, 1151545.0, 1212730.0, 1282312.0, 1340475.0, 1373550.0, 1446636.0, 1530409.0, 1600221.0, 1714380.0, 1781753.0, 1891171.0, 1954633.0, 2087725.0, 2183757.0, 2226506.0, 2280945.0, 2484200.0, 2680796.0, 2819675.0, 2952107.0, 3097584.0, 3325599.0, 3581142.0, 3770686.0, 4002727.0, 4241679.0, 4584637.0, 4902730.0, 5215397.0, 5445900.0, 5854831.0, 6291774.0, 6679606.0, 7155568.0, 7779063.0, 8226285.0, 8765133.0, 9183573.0, 9766056.0, 1.0370772E7, 1.0916588E7, 1.1474154E7, 1.2159919E7, 1.2585596E7, 1.3319924E7, 1.395653E7, 1.4457834E7, 1.5153596E7, 1.5842293E7, 1.6444255E7, 1.7130229E7, 1.7794898E7, 1.8395243E7, 1.9022827E7, 1.9794255E7, 2.0442166E7, 2.1291406E7, 2.2259054E7, 2.3127006E7, 2.3968377E7, 2.4903918E7, 2.5698933E7, 2.6276056E7, 2.6838448E7, 2.7609819E7, 2.8771718E7, 2.9463956E7, 3.0467936E7, 3.1186208E7, 3.1710825E7, 3.2662309E7, 3.3556621E7, 3.453602E7, 3.5268476E7, 3.6676564E7, 3.74541E7, 3.8873536E7, 3.9837829E7, 4.1230677E7, 4.2778829E7, 4.4343422E7, 4.5807388E7, 4.8113124E7, 5.0171987E7, 5.2141152E7, 5.4618614E7, 5.631754E7, 5.791445E7, 6.0313305E7, 6.2620225E7, 6.420093E7, 6.7746122E7, 6.9805639E7, 7.2748482E7, 7.5242148E7, 7.720614E7, 7.9063847E7, 8.088051E7, 8.4912999E7, 8.73534E7, 8.9768859E7, 9.2709551E7, 9.4386212E7, 9.7536321E7, 9.9566571E7, 1.01827646E8, 1.05525667E8, 1.06592137E8, 1.07742238E8, 1.08747898E8, 1.1318071E8, 1.14465165E8, 1.16968307E8, 1.19262981E8, 1.20802539E8, 1.21050197E8, 1.24842396E8, 1.27052994E8, 1.27934909E8, 1.29867178E8, 1.31784189E8, 1.32774756E8, 1.33886935E8, 1.3825144E8, 1.38310407E8, 1.38791732E8, 1.38791732E8, 1.39385309E8, 1.4184017E8, 1.4455547E8, 1.47748879E8, 1.48885343E8, 1.48885343E8, 1.5038614E8, 1.50941252E8, 1.50941252E8, 1.54810978E8, 1.54810978E8, 1.56603419E8, 1.56603419E8, 1.56603419E8, 1.58106229E8]']]),["return period","maximum claim"], ConstraintsFactory.getConstraints('DOUBLE')),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),"produceClaim":FrequencySeverityClaimType.AGGREGATED_EVENT,])
            parmAssociateExposureInfo[0]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
        }
        subMotorHullCat {
            parmAssociateExposureInfo[0]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
            parmUnderwritingInformation[0]=new ComboBoxTableMultiDimensionalParameter(GroovyUtils.toList([["subMotorHull"]]),["Underwriting Information"], IUnderwritingInfoMarker)
            parmPeriodPaymentPortion[0]=1.0
            parmClaimsModel[0]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.FREQUENCY_SEVERITY, ["frequencyBase":FrequencyBase.ABSOLUTE,"frequencyDistribution":DistributionType.getStrategy(DistributionType.POISSON, [lambda:1.0]),"frequencyModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),"claimsSizeBase":Exposure.ABSOLUTE,"claimsSizeDistribution":DistributionType.getStrategy(DistributionType.PARETO, [alpha:0.523, beta:500000.0]),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.TRUNCATED, ["min":500000.0,"max":2.0E7,]),"produceClaim":FrequencySeverityClaimType.SINGLE,])
        }
        subMotorThirdPartyLiabilityAttritional {
            parmAssociateExposureInfo[0]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
            parmClaimsModel[0]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase":Exposure.PREMIUM_WRITTEN,"claimsSizeDistribution":DistributionType.getStrategy(DistributionType.LOGNORMAL, [mean:0.823, stDev:0.07]),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])
            parmPeriodPaymentPortion[0]=1.0
            parmUnderwritingInformation[0]=new ComboBoxTableMultiDimensionalParameter(GroovyUtils.toList([["subMotorThirdPartyLiability"]]),["Underwriting Information"], IUnderwritingInfoMarker)
        }
        subPropertyStorm {
            parmClaimsModel[0]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.PML, ["pmlData":new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([['[0.79, 0.84, 0.89, 0.94, 1.0, 1.06, 1.12, 1.19, 1.26, 1.33, 1.41, 1.5, 1.59, 1.68, 1.78, 1.88, 2.0, 2.11, 2.24, 2.37, 2.51, 2.66, 2.82, 2.99, 3.16, 3.35, 3.55, 3.76, 3.98, 4.22, 4.47, 4.73, 5.01, 5.31, 5.62, 5.96, 6.31, 6.68, 7.08, 7.5, 7.94, 8.41, 8.91, 9.44, 10.0, 10.59, 11.22, 11.89, 12.59, 13.34, 14.13, 14.96, 15.85, 16.79, 17.78, 18.84, 19.95, 21.14, 22.39, 23.71, 25.12, 26.61, 28.18, 29.85, 31.62, 33.5, 35.48, 37.58, 39.81, 42.17, 44.67, 47.32, 50.12, 53.09, 56.23, 59.57, 63.1, 66.83, 70.8, 74.99, 79.43, 84.14, 89.13, 94.41, 100.0, 105.93, 112.2, 118.85, 125.89, 133.35, 141.25, 149.62, 158.49, 167.88, 177.83, 188.37, 199.53, 211.35, 223.87, 237.14, 251.19, 266.07, 281.84, 298.54, 316.23, 334.97, 354.81, 375.84, 398.11, 421.7, 446.68, 473.15, 501.19, 530.88, 562.34, 595.66, 630.96, 668.34, 707.95, 749.89, 794.33, 841.4, 891.25, 944.06, 1000.0, 1059.25, 1122.02, 1188.5, 1258.93, 1333.52, 1412.54, 1496.24, 1584.89, 1678.8, 1778.28, 1883.65, 1995.26, 2113.49, 2238.72, 2371.37, 2511.89, 2660.73, 2818.38, 2985.38, 3162.28, 3349.65, 3548.13, 3758.37, 3981.07, 4216.97, 4466.84, 4731.51, 5011.87, 5308.84, 5623.41]'], ['[2873.0, 10447.0, 17343.0, 24010.0, 58671.0, 60181.0, 61793.0, 335671.0, 616161.0, 702204.0, 1074861.0, 1357758.0, 1553631.0, 1688832.0, 2718983.0, 2927412.0, 3055664.0, 3124713.0, 3215061.0, 4931541.0, 5141169.0, 5849673.0, 6264607.0, 8099737.0, 9205446.0, 9758798.0, 1.170484E7, 1.2438493E7, 1.2576434E7, 1.2722416E7, 1.2877059E7, 1.3168863E7, 1.4003938E7, 1.5829668E7, 1.6269669E7, 1.6284532E7, 1.630024E7, 1.6957964E7, 1.7046419E7, 1.7140235E7, 1.7253146E7, 1.7587179E7, 1.8472304E7, 1.9012269E7, 1.9583936E7, 2.0757502E7, 2.3438231E7, 2.5802377E7, 2.6090989E7, 2.625604E7, 2.6680193E7, 2.6901774E7, 2.7136592E7, 2.7489141E7, 2.8895857E7, 3.209496E7, 3.2123961E7, 3.215465E7, 3.2187156E7, 3.3876155E7, 3.4078178E7, 3.5066331E7, 3.5579368E7, 3.8077559E7, 4.0391997E7, 4.3368005E7, 4.8650852E7, 4.9720081E7, 5.0422574E7, 5.2773751E7, 5.7367827E7, 5.8994002E7, 6.0893402E7, 6.101439E7, 6.114259E7, 6.348567E7, 6.3717695E7, 6.3963392E7, 6.4843558E7, 6.8609795E7, 7.0191283E7, 7.0363321E7, 7.4277083E7, 7.5981093E7, 7.6940527E7, 7.8248237E7, 8.1561317E7, 8.3234639E7, 8.602304E7, 8.6424721E7, 8.6850257E7, 9.1187619E7, 1.03391182E8, 1.05744218E8, 1.0703049E8, 1.0798556E8, 1.08866756E8, 1.1191745E8, 1.18015403E8, 1.20054957E8, 1.23430453E8, 1.40124629E8, 1.42930882E8, 1.43129089E8, 1.53795284E8, 1.6335755E8, 1.65454766E8, 1.67676244E8, 1.68759519E8, 1.69877727E8, 1.71345739E8, 1.73815089E8, 1.76140683E8, 1.7757706E8, 1.82256951E8, 1.84824769E8, 1.87544709E8, 1.90425866E8, 1.97748365E8, 2.05461036E8, 2.06771182E8, 2.08158962E8, 2.09628977E8, 2.11186091E8, 2.12835464E8, 2.14582581E8, 2.16433191E8, 2.18393486E8, 2.20469923E8, 2.22669402E8, 2.3216558E8, 2.46616424E8, 2.61923463E8, 2.6853736E8, 2.68934836E8, 2.69355866E8, 2.69801842E8, 2.70274246E8, 2.7077464E8, 2.71304686E8, 2.71866134E8, 2.72460855E8, 2.73090815E8, 2.73758102E8, 2.74464928E8, 2.75213633E8, 2.76006706E8, 2.76846769E8, 2.77736611E8, 2.78679177E8, 2.79677595E8, 2.80735174E8, 2.81855414E8, 2.83042035E8, 2.84298969E8]']]),["return period","maximum claim"], ConstraintsFactory.getConstraints('DOUBLE')),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),"produceClaim":FrequencySeverityClaimType.AGGREGATED_EVENT,])
            parmUnderwritingInformation[0]=new ComboBoxTableMultiDimensionalParameter(GroovyUtils.toList([["subProperty"]]),["Underwriting Information"], IUnderwritingInfoMarker)
            parmPeriodPaymentPortion[0]=1.0
            parmAssociateExposureInfo[0]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
        }
        subMotorHullAttritional {
            parmAssociateExposureInfo[0]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
            parmPeriodPaymentPortion[0]=1.0
            parmClaimsModel[0]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase":Exposure.PREMIUM_WRITTEN,"claimsSizeDistribution":DistributionType.getStrategy(DistributionType.LOGNORMAL, [mean:0.8, stDev:0.054]),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])
            parmUnderwritingInformation[0]=new ComboBoxTableMultiDimensionalParameter(GroovyUtils.toList([["subMotorHull"]]),["Underwriting Information"], IUnderwritingInfoMarker)
        }
        subPropertyFlood {
            parmClaimsModel[0]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.PML, ["pmlData":new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([['[2.0, 2.11, 2.24, 2.37, 2.51, 2.66, 2.82, 2.99, 3.16, 3.35, 3.55, 3.76, 3.98, 4.22, 4.47, 4.73, 5.01, 5.31, 5.62, 5.96, 6.31, 6.68, 7.08, 7.5, 7.94, 8.41, 8.91, 9.44, 10.0, 10.59, 11.22, 11.89, 12.59, 13.34, 14.13, 14.96, 15.85, 16.79, 17.78, 18.84, 19.95, 21.13, 22.39, 23.71, 25.12, 26.61, 28.18, 29.85, 31.62, 33.5, 35.48, 37.58, 39.81, 42.17, 44.67, 47.32, 50.12, 53.09, 56.23, 59.57, 63.1, 66.83, 70.79, 74.99, 79.43, 84.14, 89.13, 94.41, 100.0, 105.93, 112.2, 118.85, 125.89, 133.35, 141.25, 149.62, 158.49, 167.88, 177.83, 188.36, 199.53]'], ['[8120.0, 23569.0, 39309.0, 46847.0, 54618.0, 60407.0, 66539.0, 111114.0, 158960.0, 163775.0, 168875.0, 181017.0, 214191.0, 241909.0, 242258.0, 242627.0, 243019.0, 256373.0, 345909.0, 440750.0, 548395.0, 693257.0, 696979.0, 720253.0, 748553.0, 778530.0, 810283.0, 942161.0, 1003773.0, 1043425.0, 1074207.0, 1106812.0, 1141349.0, 1177932.0, 1216684.0, 1257731.0, 1355358.0, 1542437.0, 1965461.0, 2332336.0, 2668517.0, 2759900.0, 2844992.0, 2935127.0, 3047600.0, 3361577.0, 3694159.0, 4095404.0, 4536098.0, 5002904.0, 5497371.0, 6021136.0, 6575937.0, 6902460.0, 7224199.0, 7565001.0, 7910795.0, 7912902.0, 7915134.0, 7917499.0, 7920004.0, 7997673.0, 9771711.0, 1.1650867E7, 1.3641371E7, 1.5749819E7, 1.79832E7, 2.0348917E7, 2.2854813E7, 2.3254346E7, 2.3677554E7, 2.4125838E7, 2.4600685E7, 2.5103668E7, 2.5636454E7, 2.6200811E7, 2.6798607E7, 2.7431825E7, 2.8102564E7, 2.8813046E7, 2.9565628E7]']]),["return period","maximum claim"], ConstraintsFactory.getConstraints('DOUBLE')),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),"produceClaim":FrequencySeverityClaimType.AGGREGATED_EVENT,])
            parmPeriodPaymentPortion[0]=1.0
            parmUnderwritingInformation[0]=new ComboBoxTableMultiDimensionalParameter(GroovyUtils.toList([["subProperty"]]),["Underwriting Information"], IUnderwritingInfoMarker)
            parmAssociateExposureInfo[0]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
        }
        subPersonalAccidentLarge {
            parmPeriodPaymentPortion[0]=1.0
            parmClaimsModel[0]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.FREQUENCY_SEVERITY, ["frequencyBase":FrequencyBase.ABSOLUTE,"frequencyDistribution":DistributionType.getStrategy(DistributionType.POISSON, [lambda:3.626]),"frequencyModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),"claimsSizeBase":Exposure.ABSOLUTE,"claimsSizeDistribution":DistributionType.getStrategy(DistributionType.PARETO, [alpha:1.23, beta:200000.0]),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.TRUNCATED, ["min":200000.0,"max":3000000.0,]),"produceClaim":FrequencySeverityClaimType.SINGLE,])
            parmUnderwritingInformation[0]=new ComboBoxTableMultiDimensionalParameter(GroovyUtils.toList([["subPersonalAccident"]]),["Underwriting Information"], IUnderwritingInfoMarker)
            parmAssociateExposureInfo[0]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
        }
        subPersonalAccidentCat {
            parmClaimsModel[0]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.FREQUENCY_SEVERITY, ["frequencyBase":FrequencyBase.ABSOLUTE,"frequencyDistribution":DistributionType.getStrategy(DistributionType.POISSON, [lambda:0.5]),"frequencyModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),"claimsSizeBase":Exposure.ABSOLUTE,"claimsSizeDistribution":DistributionType.getStrategy(DistributionType.PARETO, [alpha:1.1, beta:200000.0]),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.TRUNCATED, ["min":200000.0,"max":3000000.0,]),"produceClaim":FrequencySeverityClaimType.AGGREGATED_EVENT,])
            parmUnderwritingInformation[0]=new ComboBoxTableMultiDimensionalParameter(GroovyUtils.toList([["subPersonalAccident"]]),["Underwriting Information"], IUnderwritingInfoMarker)
            parmPeriodPaymentPortion[0]=1.0
            parmAssociateExposureInfo[0]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
        }
        subPropertyAttritional {
            parmClaimsModel[0]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeBase":Exposure.PREMIUM_WRITTEN,"claimsSizeDistribution":DistributionType.getStrategy(DistributionType.LOGNORMAL, [mean:0.563, stDev:0.1137]),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])
            parmPeriodPaymentPortion[0]=1.0
            parmUnderwritingInformation[0]=new ComboBoxTableMultiDimensionalParameter(GroovyUtils.toList([["subProperty"]]),["Underwriting Information"], IUnderwritingInfoMarker)
            parmAssociateExposureInfo[0]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
        }
    }
    underwritingSegments {
        subPersonalAccident {
            parmUnderwritingInformation[0]=new TableMultiDimensionalParameter(GroovyUtils.toList([[0.0], [0.0], [5.3906E7], [0.0]]),["maximum sum insured","average sum insured","premium","number of policies"])
        }
        subProperty {
            parmUnderwritingInformation[0]=new TableMultiDimensionalParameter(GroovyUtils.toList([[0], [0], [72939000], [0]]),["maximum sum insured","average sum insured","premium","number of policies"])
        }
        subMotorThirdPartyLiability {
            parmUnderwritingInformation[0]=new TableMultiDimensionalParameter(GroovyUtils.toList([[0.0], [0.0], [1.89242E8], [0.0]]),["maximum sum insured","average sum insured","premium","number of policies"])
        }
        subMotorHull {
            parmUnderwritingInformation[0]=new TableMultiDimensionalParameter(GroovyUtils.toList([[0.0], [0.0], [9.7014E7], [0.0]]),["maximum sum insured","average sum insured","premium","number of policies"])
        }
    }
    dependencies {
        subAttritionals {
            parmCopulaStrategy[0]=CopulaStrategyFactory.getCopulaStrategy(PerilCopulaType.NORMAL, ["dependencyMatrix":new ComboBoxMatrixMultiDimensionalParameter(GroovyUtils.toList([[1.0, 0.25, 0.25, 0.25], [0.25, 1.0, 0.5, 0.25], [0.25, 0.5, 1.0, 0.25], [0.25, 0.25, 0.25, 1.0]]),["subPropertyAttritional","subMotorThirdPartyLiabilityAttritional","subMotorHullAttritional","subPersonalAccidentAttritional"],IPerilMarker),])
        }
    }
    reinsurance {
		subContracts {
			subMotorHullCxl {
				parmInuringPriority[0]=1
				parmCommissionStrategy[0]=CommissionStrategyType.getStrategy(CommissionStrategyType.NOCOMMISSION, [:])
				parmContractStrategy[0]=ReinsuranceContractType.getStrategy(ReinsuranceContractType.WXL, ["premiumBase":PremiumBase.GNPI,"premium":0.01,"premiumAllocation":PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),"reinstatementPremiums":new TableMultiDimensionalParameter(GroovyUtils.toList([[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]]),["Reinstatement Premium"]),"attachmentPoint":1.0E7,"limit":1.0E7,"aggregateDeductible":0.0,"aggregateLimit":1.0E8,"coveredByReinsurer":1.0,])
				parmBasedOn[0]=ReinsuranceContractBase.NET
				parmPremiumBase[0]=ReinsuranceContractPremiumBase.COMPLETESEGMENT
				parmCover[0]=CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.LINESOFBUSINESS, ["lines":new ComboBoxTableMultiDimensionalParameter(GroovyUtils.toList([["subMotorHull"]]),["Covered Segments"], ISegmentMarker),])
			}
			subPersonalAccidentWxl {
				parmContractStrategy[0]=ReinsuranceContractType.getStrategy(ReinsuranceContractType.WXL, ["premiumBase":PremiumBase.GNPI,"premium":0.0017,"premiumAllocation":PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),"reinstatementPremiums":new TableMultiDimensionalParameter(GroovyUtils.toList([[1.0, 1.0]]),["Reinstatement Premium"]),"attachmentPoint":2000000.0,"limit":1000000.0,"aggregateDeductible":0.0,"aggregateLimit":3000000.0,"coveredByReinsurer":1.0,])
				parmCommissionStrategy[0]=CommissionStrategyType.getStrategy(CommissionStrategyType.NOCOMMISSION, [:])
				parmCover[0]=CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.LINESOFBUSINESS, ["lines":new ComboBoxTableMultiDimensionalParameter(GroovyUtils.toList([["subPersonalAccident"]]),["Covered Segments"], ISegmentMarker),])
				parmInuringPriority[0]=1
				parmBasedOn[0]=ReinsuranceContractBase.NET
				parmPremiumBase[0]=ReinsuranceContractPremiumBase.COMPLETESEGMENT
			}
			subPropertyCxl {
				parmCover[0]=CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.LINESOFBUSINESS, ["lines":new ComboBoxTableMultiDimensionalParameter(GroovyUtils.toList([["subProperty"]]),["Covered Segments"], ISegmentMarker),])
				parmBasedOn[0]=ReinsuranceContractBase.NET
				parmCommissionStrategy[0]=CommissionStrategyType.getStrategy(CommissionStrategyType.NOCOMMISSION, [:])
				parmPremiumBase[0]=ReinsuranceContractPremiumBase.COMPLETESEGMENT
				parmInuringPriority[0]=0
				parmContractStrategy[0]=ReinsuranceContractType.getStrategy(ReinsuranceContractType.CXL, ["premiumBase":PremiumBase.GNPI,"premium":0.1287,"premiumAllocation":PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),"reinstatementPremiums":new TableMultiDimensionalParameter(GroovyUtils.toList([[1.0]]),["Reinstatement Premium"]),"attachmentPoint":1.0E7,"limit":1.7E8,"aggregateDeductible":0.0,"aggregateLimit":3.4E8,"coveredByReinsurer":1.0,])
			}
			subMotorThirdPartyLiabilityWxl {
				parmBasedOn[0]=ReinsuranceContractBase.NET
				parmCover[0]=CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.LINESOFBUSINESS, ["lines":new ComboBoxTableMultiDimensionalParameter(GroovyUtils.toList([["subMotorThirdPartyLiability"]]),["Covered Segments"], ISegmentMarker),])
				parmCommissionStrategy[0]=CommissionStrategyType.getStrategy(CommissionStrategyType.NOCOMMISSION, [:])
				parmInuringPriority[0]=1
				parmContractStrategy[0]=ReinsuranceContractType.getStrategy(ReinsuranceContractType.WXL, ["premiumBase":PremiumBase.GNPI,"premium":0.0049,"premiumAllocation":PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),"reinstatementPremiums":new TableMultiDimensionalParameter(GroovyUtils.toList([[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]]),["Reinstatement Premium"]),"attachmentPoint":5000000.0,"limit":9.5E7,"aggregateDeductible":0.0,"aggregateLimit":9.5E8,"coveredByReinsurer":1.0,])
				parmPremiumBase[0]=ReinsuranceContractPremiumBase.COMPLETESEGMENT
			}
			subPersonalAccidentCxl {
				parmCommissionStrategy[0]=CommissionStrategyType.getStrategy(CommissionStrategyType.NOCOMMISSION, [:])
				parmInuringPriority[0]=2
				parmContractStrategy[0]=ReinsuranceContractType.getStrategy(ReinsuranceContractType.CXL, ["premiumBase":PremiumBase.GNPI,"premium":0.0018,"premiumAllocation":PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),"reinstatementPremiums":new TableMultiDimensionalParameter(GroovyUtils.toList([[1.0]]),["Reinstatement Premium"]),"attachmentPoint":2000000.0,"limit":1.0E7,"aggregateDeductible":0.0,"aggregateLimit":2.0E7,"coveredByReinsurer":1.0,])
				parmCover[0]=CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.LINESOFBUSINESS, ["lines":new ComboBoxTableMultiDimensionalParameter(GroovyUtils.toList([["subPersonalAccident"]]),["Covered Segments"], ISegmentMarker),])
				parmPremiumBase[0]=ReinsuranceContractPremiumBase.COMPLETESEGMENT
				parmBasedOn[0]=ReinsuranceContractBase.NET
			}
		}
	}
}
comments=[]
