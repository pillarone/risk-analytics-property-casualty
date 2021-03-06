package models.capitalEagle

import org.pillarone.riskanalytics.core.parameterization.ComboBoxMatrixMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskAllocatorType
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.domain.pc.constants.RiskBandAllocationBase
import org.pillarone.riskanalytics.domain.pc.generators.copulas.CopulaStrategyFactory
import org.pillarone.riskanalytics.domain.pc.generators.copulas.LobCopulaType
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.pc.constants.StopLossContractBase
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.PremiumAllocationType

model = models.capitalEagle.CapitalEagleAttritionalDependenciesModel
periodCount = 1
allPeriods = 0..0
displayName = 'One Reinsurance Program (Attr MTPL, PA correlated)'
applicationVersion='1.1.1'
components {
    personalAccident {
        subRiProgram {
            subContract2 {
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.WXL, ["premiumAllocation":PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),"premiumBase": PremiumBase.GNPI, "premium": 0.0178, "reinstatementPremiums": new TableMultiDimensionalParameter([0.0, 0.0], ["Reinstatement Premium"]), "attachmentPoint": 200000.0, "limit": 2800000.0, "aggregateLimit": 8400000.0, "coveredByReinsurer": 1.0,])
                parmInuringPriority[0] = 0
            }
            subContract3 {
                parmInuringPriority[0] = 0
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.TRIVIAL, [:])
            }
            subContract1 {
                parmInuringPriority[0] = 0
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.QUOTASHARE, ["quotaShare": 0.5, "coveredByReinsurer": 1.0,])
                parmCommissionStrategy[0] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ['commission': 0.4])
            }
        }
        subClaimsGenerator {
            subAttritionalClaimsGenerator {
                parmModification[0] = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
                parmBase[0] = Exposure.PREMIUM_WRITTEN
                parmDistribution[0] = DistributionType.getStrategy(DistributionType.LOGNORMAL, ["mean": 0.39, "stDev": 0.09])
            }
            subSingleClaimsGenerator {
                subClaimsGenerator {
                    parmBase[0] = Exposure.ABSOLUTE
                    parmModification[0] = DistributionModifier.getStrategy(DistributionModifier.TRUNCATED, ["min": 200000.0, "max": 3000000.0])
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.PARETO, ["alpha": 1.23, "beta": 200000])
                }
                subFrequencyGenerator {
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.POISSON, ["lambda": 3.626])
                    parmBase[0] = FrequencyBase.ABSOLUTE
                }
            }
        }
        subUnderwriting {
            parmUnderwritingInformation[0] = new TableMultiDimensionalParameter([[0], [0], [53906000], [0],[0],[0]], ["maximum sum insured", "average sum insured", "premium", "number of policies/risks", "custom allocation number of single claims", "custom allocation attritional claims"])
            parmAllocationBaseSingleClaims[0] = RiskBandAllocationBase.NUMBER_OF_POLICIES
            parmAllocationBaseAttritionalClaims[0] = RiskBandAllocationBase.PREMIUM
        }
        subAllocator {
            parmRiskAllocatorStrategy[0] = RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
        }
    }
    mtpl {
        subRiProgram {
            subContract3 {
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.TRIVIAL, [:])
                parmInuringPriority[0] = 0
            }
            subContract2 {
                parmInuringPriority[0] = 0
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.WXL, ["premiumAllocation":PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),"premiumBase": PremiumBase.GNPI, "premium": 0.0573, "reinstatementPremiums": new TableMultiDimensionalParameter([0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], ["Reinstatement Premium"]), "attachmentPoint": 1000000.0, "limit": 9.9E7, "aggregateLimit": 9.9E8, "coveredByReinsurer": 1.0,])
            }
            subContract1 {
                parmInuringPriority[0] = 0
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.QUOTASHARE, ["quotaShare": 0.5, "coveredByReinsurer": 1.0,])
                parmCommissionStrategy[0] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ['commission': 0.167])
            }
        }
        subClaimsGenerator {
            subSingleClaimsGenerator {
                subFrequencyGenerator {
                    parmBase[0] = FrequencyBase.ABSOLUTE
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.POISSON, ["lambda": 4.874])
                }
                subClaimsGenerator {
                    parmModification[0] = DistributionModifier.getStrategy(DistributionModifier.CENSORED, ["max": 1.0E8, "min": 1000000.0])
                    parmBase[0] = Exposure.ABSOLUTE
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.PARETO, ["beta": 1000000.0, "alpha": 1.416])
                }
            }
            subAttritionalClaimsGenerator {
                parmModification[0] = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
                parmDistribution[0] = DistributionType.getStrategy(DistributionType.LOGNORMAL, ["stDev": 0.07, "mean": 0.823])
                parmBase[0] = Exposure.PREMIUM_WRITTEN
            }
        }
        subUnderwriting {
            parmUnderwritingInformation[0] = new TableMultiDimensionalParameter([[0], [0], [189242000], [0],[0],[0]], ["maximum sum insured", "average sum insured", "premium", "number of policies/risks", "custom allocation number of single claims", "custom allocation attritional claims"])
            parmAllocationBaseAttritionalClaims[0] = RiskBandAllocationBase.PREMIUM
            parmAllocationBaseSingleClaims[0] = RiskBandAllocationBase.NUMBER_OF_POLICIES
        }
        subAllocator {
            parmRiskAllocatorStrategy[0] = RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
        }
    }
    property {
        subClaimsGenerator {
            subAttritionalSeverityClaimsGenerator {
                parmDistribution[0] = DistributionType.getStrategy(DistributionType.LOGNORMAL, ["stDev": 0.1137, "mean": 0.563])
                parmBase[0] = Exposure.PREMIUM_WRITTEN
                parmModification[0] = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
            }
            subAttritionalFrequencyGenerator {
                parmDistribution[0] = DistributionType.getStrategy(DistributionType.CONSTANT, ["constant": 1.0])
                parmBase[0] = FrequencyBase.ABSOLUTE
            }
            subEQGenerator {
                subClaimsGenerator {
                    parmBase[allPeriods] = Exposure.ABSOLUTE
                    parmDistribution[allPeriods] = DistributionType.getStrategy(DistributionType.DISCRETEEMPIRICALCUMULATIVE, [discreteEmpiricalCumulativeValues: new TableMultiDimensionalParameter([[451.4760155775, 1209.31075601083, 2508.20008654083, 3795.89209525583, 5979.81774203583, 8617.01097588417, 12910.019370435, 18242.6767008558, 23539.3566858483, 31179.42933503, 39826.8133884317, 49392.9318561167, 64133.1110575383, 80772.7319083325, 102612.257111862, 127314.264195655, 147050.351570111, 178019.501142212, 211279.793504286, 248663.85236537, 289048.412530283, 325565.991390304, 367729.493935098, 421079.531485832, 476458.584224978, 512126.173351402, 585944.553661495, 633542.927829992, 684595.068486383, 736229.399789592, 814346.357918619, 875593.327738958, 929098.331884858, 1010219.83830683, 1082556.02702566, 1151544.71334249, 1212729.80806412, 1282311.57937107, 1340474.82945861, 1373549.82139703, 1446636.12614688, 1530409.21918392, 1600221.02259649, 1714379.88827683, 1781753.31603245, 1891170.77321198, 1954633.23878536, 2087724.92694518, 2183756.92420051, 2226505.50339752, 2280944.71542158, 2484200.48014502, 2680795.75985569, 2819675.19420827, 2952106.57947129, 3097583.84670053, 3325598.83839086, 3581142.14438667, 3770685.77321265, 4002726.7967717, 4241678.75542337, 4584637.24574602, 4902729.76441548, 5215397.32037349, 5445899.88945372, 5854831.45885131, 6291773.60783406, 6679606.39628458, 7155567.70360963, 7779062.83609399, 8226285.4129099, 8765133.3327455, 9183573.40716942, 9766056.48824933, 1.03707717360482E7, 1.09165884767581E7, 1.14741542840279E7, 1.21599186972086E7, 1.25855960861946E7, 1.3319923641533E7, 1.39565299631032E7, 1.44578341634852E7, 1.51535964910967E7, 1.58422926315304E7, 1.64442546231682E7, 1.71302285494206E7, 1.7794898458931E7, 1.8395242869239E7, 1.90228266427412E7, 1.97942551911818E7, 2.04421655479473E7, 2.12914057295502E7, 2.22590542847508E7, 2.3127006269587E7, 2.39683766873276E7, 2.49039178917684E7, 2.56989331408403E7, 2.62760560074301E7, 2.68384483932011E7, 2.76098191427018E7, 2.87717184513994E7, 2.94639561052073E7, 3.04679360267048E7, 3.11862080915291E7, 3.17108247341194E7, 3.2662308896424E7, 3.35566213178374E7, 3.45360196755173E7, 3.52684758654144E7, 3.66765636701549E7, 3.74541004364107E7, 3.8873536059844E7, 3.98378291275337E7, 4.12306773877639E7, 4.27788290794918E7, 4.43434220034294E7, 4.58073879304484E7, 4.81131242346657E7, 5.01719870659186E7, 5.21411520103988E7, 5.46186135455134E7, 5.63175403755435E7, 5.79144498371052E7, 6.03133053908351E7, 6.26202247240418E7, 6.42009299634834E7, 6.77461219693631E7, 6.98056386858287E7, 7.27484816349885E7, 7.52421482227272E7, 7.72061396251218E7, 7.90638474825736E7, 8.08805100241313E7, 8.49129992466175E7, 8.73533995153925E7, 8.97688593986183E7, 9.27095509460292E7, 9.43862117477717E7, 9.75363210115825E7, 9.95665706443492E7, 1.01827646378109E8, 1.05525666594066E8, 1.06592136947003E8, 1.07742237874467E8, 1.08747897679326E8, 1.13180710337924E8, 1.14465165320385E8, 1.16968306989863E8, 1.19262981202604E8, 1.20802538700995E8, 1.21050196981462E8, 1.2484239602917E8, 1.27052994177514E8, 1.27934908919943E8, 1.29867177941042E8, 1.31784188908307E8, 1.32774756451719E8, 1.33886935108416E8, 1.38251440458389E8, 1.38310407099997E8, 1.38791732326707E8, 1.38791732326707E8, 1.39385309378688E8, 1.4184017045966E8, 1.44555469761499E8, 1.47748879067167E8, 1.48885342908022E8, 1.48885342908022E8, 1.50386139812956E8, 1.50941251854809E8, 1.50941251854809E8, 1.54810977771998E8, 1.54810977771998E8, 1.56603418621649E8, 1.56603418621649E8, 1.56603418621649E8, 1.58106229147005E8, 1.58106229147005E8], [0.0, 0.055939123725544, 0.108749061872162, 0.158604858359564, 0.20567176528702, 0.250105790672736, 0.292054215621642, 0.331656082438367, 0.369042655525156, 0.404337856477457, 0.437658674811304, 0.469115555771937, 0.498812766374429, 0.526848741045378, 0.553316407851084, 0.578303496576943, 0.60189282945, 0.624162595715819, 0.645186610768921, 0.665034560845955, 0.683772233984893, 0.701461738110082, 0.718161706875481, 0.733927494021668, 0.748811356851072, 0.762862629435556, 0.776127886145268, 0.788651096018214, 0.800473768504977, 0.811635091052973, 0.822172058997802, 0.832119598189102, 0.841510680755307, 0.850376434391938, 0.858746245538742, 0.86664785678493, 0.874107458821754, 0.881149777257403, 0.887798154570924, 0.894074627483076, 0.900000000000754, 0.905593912372331, 0.910874906187416, 0.915860485836134, 0.920567176528227, 0.925010579067415, 0.929205421562164, 0.933165608243725, 0.936904265552516, 0.940433785647657, 0.943765867481448, 0.946911555577335, 0.949881276637695, 0.952684874104257, 0.955331640785309, 0.957830349657516, 0.960189282945, 0.962416259571475, 0.964518661076955, 0.966503456084511, 0.96837722339859, 0.970146173811075, 0.971816170687608, 0.973392749402238, 0.974881135685123, 0.976286262943598, 0.977612788614514, 0.978865109601821, 0.980047376850488, 0.981163509105271, 0.982217205899764, 0.983211959818924, 0.984151068075531, 0.985037643439188, 0.985874624553894, 0.986664785678484, 0.987410745882167, 0.988114977725733, 0.98877981545708, 0.989407462748319, 0.990000000000088, 0.990559391237224, 0.991087490618742, 0.991586048583622, 0.992056717652827, 0.992501057906741, 0.99292054215622, 0.993316560824372, 0.993690426555254, 0.994043378564762, 0.994376586748146, 0.994691155557737, 0.994988127663771, 0.995268487410427, 0.995533164078529, 0.995783034965751, 0.9960189282945, 0.996241625957149, 0.996451866107696, 0.996650345608451, 0.996837722339859, 0.997014617381108, 0.99718161706876, 0.997339274940224, 0.997488113568512, 0.997628626294359, 0.997761278861451, 0.997886510960182, 0.998004737685049, 0.998116350910527, 0.998221720589977, 0.998321195981892, 0.998415106807553, 0.998503764343919, 0.99858746245539, 0.998666478567848, 0.998741074588217, 0.998811497772573, 0.998877981545708, 0.998940746274832, 0.999000000000009, 0.999055939123722, 0.999108749061874, 0.999158604858362, 0.999205671765283, 0.999250105790674, 0.999292054215622, 0.999331656082437, 0.999369042655525, 0.999404337856476, 0.999437658674815, 0.999469115555774, 0.999498812766377, 0.999526848741043, 0.999553316407853, 0.999578303496575, 0.99960189282945, 0.999624162595715, 0.99964518661077, 0.999665034560845, 0.999683772233986, 0.999701461738111, 0.999718161706876, 0.999733927494022, 0.999748811356851, 0.999762862629436, 0.999776127886145, 0.999788651096018, 0.999800473768505, 0.999811635091053, 0.999822172058998, 0.999832119598189, 0.999841510680755, 0.999850376434392, 0.999858746245539, 0.999866647856785, 0.999874107458822, 0.999881149777257, 0.999887798154571, 0.999894074627483, 0.999900000000001, 0.999905593912372, 0.999910874906187, 0.999915860485836, 0.999920567176528, 0.999925010579067, 0.999929205421562, 0.999933165608244, 0.999936904265552, 0.999940433785648, 0.999943765867481, 0.999946911555577, 0.999949881276638, 0.999952684874104, 0.999955331640785, 0.999957830349657, 0.999960187690453, 0.9999999]], ["observations", "cumulative probabilities"])])
                }
                subEventGenerator {
                    parmDistribution[allPeriods] = DistributionType.getStrategy(DistributionType.UNIFORM, [b: 1, a: 0])
                    parmModification[allPeriods] = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
                }
                subFrequencyGenerator {
                    parmBase[allPeriods] = FrequencyBase.ABSOLUTE
                    parmDistribution[allPeriods] = DistributionType.getStrategy(DistributionType.POISSON, [lambda: 0.28])
                }
            }
            subFloodGenerator {
                subClaimsGenerator {
                    parmBase[allPeriods] = Exposure.ABSOLUTE
                    parmDistribution[allPeriods] = DistributionType.getStrategy(DistributionType.DISCRETEEMPIRICALCUMULATIVE, [discreteEmpiricalCumulativeValues: new TableMultiDimensionalParameter([[8119.63067645946, 23569.3098037995, 39309.0530166422, 46846.5967759755, 54617.6994527125, 60406.9758416539, 66539.2884227223, 111114.09291775, 158960.170055245, 163775.108229293, 168875.349426654, 181016.97739312, 214191.343570257, 241908.892388336, 242257.736234819, 242627.25037871, 243018.659612131, 256373.235646184, 345908.922419534, 440749.932170512, 548395.064290314, 693256.832161087, 696978.617614121, 720253.371550217, 748553.248221574, 778529.998007768, 810282.9818875, 942161.162856666, 1003772.52279399, 1043425.46828609, 1074206.67816904, 1106811.78940303, 1141348.87493745, 1177932.41144655, 1216683.65877397, 1257731.06186081, 1355357.70133513, 1542437.33023811, 1965461.04756109, 2332336.24140181, 2668516.86471402, 2759899.99336676, 2844992.28174813, 2935126.60519994, 3047599.78153713, 3361577.1717411, 3694158.89193611, 4095403.7677025, 4536097.54729725, 5002904.07499542, 5497370.62839682, 6021136.16706281, 6575936.76501423, 6902460.39723065, 7224198.65647886, 7565001.10611958, 7910794.58730774, 7912902.02610588, 7915134.33850336, 7917498.92372617, 7920003.61943193, 7997673.21757102, 9771711.10353826, 1.16508673428547E7, 1.36413705895408E7, 1.57498185685705E7, 1.79831999447002E7, 2.03489174871073E7, 2.28548126066192E7, 2.32543462528436E7, 2.36775537559406E7, 2.4125837880119E7, 2.46006845085928E7, 2.51036675686915E7, 2.56364542488022E7, 2.62008105244343E7, 2.67986070117249E7, 2.74318251677853E7, 2.8102563858442E7, 2.88130463151407E7, 2.9565627504072E7], [0.0, 0.0559391236829825, 0.108749061864958, 0.158604858355287, 0.2056717652603, 0.250105790656183, 0.292054215613749, 0.331656082416941, 0.369042655516275, 0.404337856465562, 0.437658674795213, 0.469115555762929, 0.498812766360508, 0.52684874103273, 0.553316407841107, 0.578303496563521, 0.60189282944246, 0.624162595704994, 0.64518661076067, 0.665034560837504, 0.683772233978133, 0.701461738104167, 0.718161706867511, 0.733927494015098, 0.74881135684377, 0.76286262943149, 0.776127886138733, 0.788651096013952, 0.8004737685, 0.811635091048564, 0.822172058993023, 0.832119598185547, 0.841510680750892, 0.85037643438785, 0.858746245535246, 0.866647856781193, 0.874107458818531, 0.881149777254263, 0.887798154568122, 0.89407462748063, 0.899999999998496, 0.905593912370085, 0.910874906185301, 0.915860485834109, 0.920567176526346, 0.925010579065618, 0.92920542156037, 0.933165608242142, 0.936904265551029, 0.940433785646201, 0.943765867480155, 0.94691155557601, 0.949881276636429, 0.952684874103161, 0.955331640784211, 0.957830349656441, 0.960189282944008, 0.96241625957057, 0.964518661076067, 0.966503456083694, 0.968377223397813, 0.970146173810372, 0.97181617068691, 0.973392749401581, 0.974881135684504, 0.976286262943008, 0.977612788613974, 0.978865109601306, 0.98004737685, 0.981163509104803, 0.982217205899334, 0.983211959818512, 0.98415106807514, 0.985037643438819, 0.985874624553555, 0.986664785678155, 0.987410745881861, 0.988114977725448, 0.988779815456806, 0.989407462748063, 0.999999999999845]], ["observations", "cumulative probabilities"])])
                }
                subEventGenerator {
                    parmDistribution[allPeriods] = DistributionType.getStrategy(DistributionType.UNIFORM, [a: 0, b: 1])
                    parmModification[allPeriods] = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
                }
                subFrequencyGenerator {
                    parmBase[allPeriods] = FrequencyBase.ABSOLUTE
                    parmDistribution[allPeriods] = DistributionType.getStrategy(DistributionType.POISSON, [lambda: 0.5])
                }
            }
            subStormGenerator {
                subClaimsGenerator {
                    parmBase[allPeriods] = Exposure.ABSOLUTE
                    parmDistribution[allPeriods] = DistributionType.getStrategy(DistributionType.DISCRETEEMPIRICALCUMULATIVE, [discreteEmpiricalCumulativeValues: new TableMultiDimensionalParameter([[2873.42510885333, 10447.1470654608, 17343.1669757975, 24010.12786725, 58671.2204668292, 60180.9339424817, 61793.00087411, 335671.410953993, 616160.712790164, 702203.844492636, 1074860.87612165, 1357758.3717606, 1553631.29711854, 1688831.97301045, 2718983.27492692, 2927411.65595261, 3055664.12917624, 3124713.25650152, 3215061.30154604, 4931541.41664691, 5141168.9013795, 5849673.03723663, 6264606.58364422, 8099736.53850689, 9205445.84266733, 9758797.66080084, 1.17048395610119E7, 1.24384930997607E7, 1.25764339946435E7, 1.27224162869858E7, 1.28770585458231E7, 1.31688627214083E7, 1.40039384243096E7, 1.58296679565572E7, 1.62696693769688E7, 1.62845320934764E7, 1.63002402938573E7, 1.69579640043658E7, 1.70464189421643E7, 1.7140234785284E7, 1.72531463820493E7, 1.75871792202096E7, 1.84723040025299E7, 1.90122687055004E7, 1.9583935881562E7, 2.07575022862829E7, 2.34382311660631E7, 2.58023766784384E7, 2.60909894199602E7, 2.62560399491249E7, 2.66801928321124E7, 2.69017740084053E7, 2.71365918141062E7, 2.74891409220759E7, 2.88958569679145E7, 3.20949599209282E7, 3.2123960958457E7, 3.21546496123147E7, 3.21871556991419E7, 3.38761546566881E7, 3.40781776406808E7, 3.50663311843008E7, 3.55793680510638E7, 3.80775585545056E7, 4.03919970936147E7, 4.33680051625993E7, 4.86508517877602E7, 4.97200814996268E7, 5.04225743039778E7, 5.27737508995178E7, 5.73678274936015E7, 5.89940022287295E7, 6.08934016615372E7, 6.10143895980888E7, 6.11425903553779E7, 6.34856695884268E7, 6.37176950568572E7, 6.39633922809345E7, 6.48435580578926E7, 6.86097950271143E7, 7.01912834782208E7, 7.03633208249197E7, 7.42770831260083E7, 7.59810933403531E7, 7.69405268023791E7, 7.82482372840299E7, 8.15613165821332E7, 8.3234639176148E7, 8.60230403827875E7, 8.64247205290825E7, 8.68502569972458E7, 9.1187618857355E7, 1.03391181711836E8, 1.05744217944434E8, 1.0703048957104E8, 1.07985560384813E8, 1.08866756342504E8, 1.11917449754511E8, 1.18015403094655E8, 1.20054957493317E8, 1.23430452572304E8, 1.40124628630308E8, 1.42930881508492E8, 1.4312908897322E8, 1.53795284285513E8, 1.63357549678052E8, 1.65454766387584E8, 1.67676243821139E8, 1.68759518781364E8, 1.69877726950674E8, 1.71345738636643E8, 1.73815088923882E8, 1.76140682582772E8, 1.7757706002254E8, 1.82256951037053E8, 1.84824768922858E8, 1.87544709269171E8, 1.9042586551588E8, 1.97748365285144E8, 2.05461035653728E8, 2.06771181620527E8, 2.08158962153886E8, 2.09628976947077E8, 2.11186091060293E8, 2.12835464405856E8, 2.14582581233435E8, 2.1643319115962E8, 2.183934860792E8, 2.20469923253882E8, 2.22669402223573E8, 2.32165579637336E8, 2.46616424288137E8, 2.61923463096783E8, 2.68537360113222E8, 2.6893483576837E8, 2.69355866276367E8, 2.69801842152698E8, 2.70274245814735E8, 2.70774639594533E8, 2.71304685717502E8, 2.71866134315202E8, 2.72460855391216E8, 2.7309081484675E8, 2.73758102450768E8, 2.74464927844258E8, 2.75213632527434E8, 2.76006705842677E8, 2.76846768991591E8, 2.77736610996618E8, 2.78679176713832E8, 2.79677594803074E8, 2.80735173732228E8, 2.81855413764413E8, 2.83042034928128E8, 2.84298969025782E8, 2.84692897953676E8, 2.84692897953676E8, 2.84692897953676E8, 2.84692897953676E8, 2.84692897953676E8, 2.84692897953676E8, 2.84692897953676E8, 2.84692897953676E8, 2.84692897953676E8, 2.84692897953676E8], [0.0, 0.055885850178359, 0.108866442199775, 0.158898305084746, 0.206, 0.250236071765817, 0.292335115864528, 0.332211942809083, 0.369340746624305, 0.4047976011994, 0.438075017692852, 0.469251336898396, 0.499053627760252, 0.527099463966647, 0.553430821147357, 0.578556263269639, 0.602005012531328, 0.624230951254141, 0.645377400625279, 0.665120202446225, 0.683917197452229, 0.701615933859451, 0.718239886444287, 0.734003350083752, 0.748893105629348, 0.762985074626866, 0.776211950394589, 0.788717402873869, 0.800552624968601, 0.811714488973204, 0.822252070740989, 0.832206255283178, 0.841580207501995, 0.850442644565832, 0.858794237951272, 0.866711431928823, 0.874167987321712, 0.881191081849469, 0.887837265150445, 0.894119215895453, 0.900037769104872, 0.905633468029475, 0.910916638617749, 0.915898739540303, 0.9206, 0.925044840932691, 0.929233511586453, 0.933193100546908, 0.936929065056796, 0.940457442819647, 0.943787610619469, 0.946932228311723, 0.949902202031674, 0.952704312604241, 0.955350615756622, 0.957846676576768, 0.960206485240315, 0.962431984859238, 0.96453298789476, 0.966517668887577, 0.968390461403718, 0.970158229037471, 0.971827987510644, 0.973403898975012, 0.974891692755273, 0.976296384750873, 0.977621825765903, 0.97887398893146, 0.980055763482455, 0.98117144889732, 0.982224411211606, 0.983218852372398, 0.984157704662902, 0.985043701024714, 0.985880428210691, 0.986670248128127, 0.987416001014327, 0.988119819253673, 0.988784518680698, 0.989411780394458, 0.990004154444626, 0.990563346802947, 0.991091164095372, 0.991589517615406, 0.99206, 0.992504130280859, 0.99292347729987, 0.993319310054691, 0.993693056802205, 0.994045833583298, 0.994378920242967, 0.99469336470085, 0.99499018859353, 0.995270431260424, 0.995535011359291, 0.995784779550341, 0.996020568747933, 0.996243180710578, 0.996453330474557, 0.996651724530546, 0.996839033556406, 0.997015856550646, 0.997182778759429, 0.997340372079936, 0.997489153395651, 0.99762960309286, 0.99776220149769, 0.997887382029976, 0.998005561318942, 0.998117131494888, 0.998222457038981, 0.998321888783919, 0.998415760983425, 0.998504381371448, 0.998588045331925, 0.998667029288422, 0.998741594118141, 0.998811989035586, 0.998878445531156, 0.998941183687294, 0.999000412927657, 0.999056329072552, 0.999109117409125, 0.999158952652424, 0.999206, 0.999250415858708, 0.999292346468595, 0.999331932129689, 0.999369303175328, 0.999404583804829, 0.999437891228413, 0.999469335051422, 0.999499019807646, 0.999527044252932, 0.999553500884844, 0.999578477731255, 0.999602057273681, 0.999624317893303, 0.99964533320588, 0.999665173017837, 0.999683902852279, 0.999701585094288, 0.999718278175819, 0.999734037475259, 0.999748915180765, 0.999762960592348, 0.999776220402048, 0.999788738427841, 0.99980055623209, 0.99981171292624, 0.999822245544721, 0.999832188984792, 0.999841576161562, 0.999850438249834, 0.999858804608518, 0.99986670295122, 0.999874159471647, 0.999881198885783, 0.999887844521431, 0.999894118396969, 0.999900041317934, 0.999905632918471, 0.999910911730917, 0.999915895256334, 0.9999999206]], ["observations", "cumulative probabilities"])])
                }
                subEventGenerator {
                    parmDistribution[allPeriods] = DistributionType.getStrategy(DistributionType.UNIFORM, [a: 0, b: 1])
                    parmModification[allPeriods] = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
                }
                subFrequencyGenerator {
                    parmBase[allPeriods] = FrequencyBase.ABSOLUTE
                    parmDistribution[allPeriods] = DistributionType.getStrategy(DistributionType.POISSON, [lambda: 1.26])
                }
            }
            subSingleClaimsGenerator {
                subFrequencyGenerator {
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.POISSON, ["lambda": 13.32])
                    parmBase[0] = FrequencyBase.ABSOLUTE
                }
                subClaimsGenerator {
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.PARETO, ["beta": 70000, "alpha": 1.76])
                    parmBase[0] = Exposure.ABSOLUTE
                    parmModification[0] = DistributionModifier.getStrategy(DistributionModifier.TRUNCATED, ["max": 1000000.0, "min": 70000.0])
                }
            }
        }
        subRiProgram {
            subContract2 {
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.QUOTASHARE, ["quotaShare": 0.5, "coveredByReinsurer": 1.0,])
                parmCommissionStrategy[0] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ['commission': 0.284])
                parmInuringPriority[0] = 0
            }
            subContract3 {
                parmInuringPriority[0] = 0
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.STOPLOSS, ["premiumAllocation":PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),"stopLossContractBase": StopLossContractBase.GNPI, "premium": 0.1207, "attachmentPoint": 3.3960398E7, "limit": 1.01881194E8, "coveredByReinsurer": 1.0,])
            }
            subContract1 {
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.WXL, ["premiumAllocation":PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),"premiumBase": PremiumBase.GNPI, "premium": 0.0688, "reinstatementPremiums": new TableMultiDimensionalParameter([1.0, 1.0, 1.0], ["Reinstatement Premium"]), "attachmentPoint": 1000000.0, "limit": 1.4E7, "aggregateLimit": 5.6E7, "coveredByReinsurer": 1.0,])
                parmInuringPriority[0] = 0
            }
        }
        subUnderwriting {
            parmAllocationBaseAttritionalClaims[0] = RiskBandAllocationBase.PREMIUM
            parmUnderwritingInformation[0] = new TableMultiDimensionalParameter([[100000, 200000, 300000, 400000, 500000, 750000, 1000000, 1250000, 1500000], [78479, 167895, 265488, 378455, 445783, 645789, 835481, 1202547, 1363521], [8706931, 25985915, 22805313, 9315291, 5547769, 564420, 8021, 2886, 2454], [92455, 128979, 71583, 24614, 12445, 874, 12, 4, 3],[0] *9, [0] *9], ["maximum sum insured", "average sum insured", "premium", "number of policies/risks", "custom allocation number of single claims", "custom allocation attritional claims"])
            parmAllocationBaseSingleClaims[0] = RiskBandAllocationBase.NUMBER_OF_POLICIES
        }
        subAllocator {
            parmRiskAllocatorStrategy[0] = RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
        }
    }
    motorHull {
        subAllocator {
            parmRiskAllocatorStrategy[0] = RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
        }
        subClaimsGenerator {
            subAttritionalClaimsGenerator {
                parmDistribution[0] = DistributionType.getStrategy(DistributionType.LOGNORMAL, ["stDev": 0.054, "mean": 0.8])
                parmBase[0] = Exposure.PREMIUM_WRITTEN
                parmModification[0] = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
            }
            subSingleClaimsGenerator {
                subClaimsGenerator {
                    parmModification[0] = DistributionModifier.getStrategy(DistributionModifier.TRUNCATED, ["max": 2.0E7, "min": 500000.0])
                    parmBase[0] = Exposure.ABSOLUTE
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.PARETO, ["beta": 500000.0, "alpha": 0.523])
                }
                subFrequencyGenerator {
                    parmBase[0] = FrequencyBase.ABSOLUTE
                    parmDistribution[0] = DistributionType.getStrategy(DistributionType.POISSON, ["lambda": 1.0])
                }
            }
        }
        subUnderwriting {
            parmAllocationBaseSingleClaims[0] = RiskBandAllocationBase.NUMBER_OF_POLICIES
            parmAllocationBaseAttritionalClaims[0] = RiskBandAllocationBase.PREMIUM
            parmUnderwritingInformation[0] = new TableMultiDimensionalParameter([[0], [0], [97014000], [0], [0],[0]], ["maximum sum insured", "average sum insured", "premium", "number of policies/risks", "custom allocation number of single claims", "custom allocation attritional claims"])
        }
        subRiProgram {
            subContract2 {
                parmInuringPriority[0] = 0
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.WXL, ["premiumAllocation":PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),"premiumBase": PremiumBase.GNPI, "premium": 0.043, "reinstatementPremiums": new TableMultiDimensionalParameter([0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], ["Reinstatement Premium"]), "attachmentPoint": 500000.0, "limit": 1.95E7, "aggregateLimit": 1.95E8, "coveredByReinsurer": 1.0,])
            }
            subContract1 {
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.QUOTASHARE, ["quotaShare": 0.5, "coveredByReinsurer": 1.0,])
                parmCommissionStrategy[0] = CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ['commission': 0.189])
                parmInuringPriority[0] = 0
            }
            subContract3 {
                parmInuringPriority[0] = 0
                parmContractStrategy[0] = ReinsuranceContractType.getStrategy(ReinsuranceContractType.TRIVIAL, [:])
            }
        }
    }
    copulaAttritional {
        parmCopulaStrategy[0] = CopulaStrategyFactory.getCopulaStrategy(LobCopulaType.NORMAL, ["dependencyMatrix": new ComboBoxMatrixMultiDimensionalParameter([[1.0, 0.0], [0.0, 1.0]], ["personal accident", "mtpl"], ISegmentMarker),])
    }
}
