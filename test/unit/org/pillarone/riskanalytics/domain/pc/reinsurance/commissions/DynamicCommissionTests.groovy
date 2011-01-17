package org.pillarone.riskanalytics.domain.pc.reinsurance.commissions

import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.applicable.ApplicableStrategyType
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.IReinsuranceContractMarker
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo

/**
 * @author ben.ginsberg (at) intuitive-collaboration (dot) com
 */
class DynamicCommissionTests extends GroovyTestCase {

    void testNoCommissions() {
        DynamicCommission dynamicCommission = new DynamicCommission() // no subcomponents

        Claim claim300 = new Claim(value: 300)
        Claim claim400 = new Claim(value: 400)
        dynamicCommission.inClaims << claim300 << claim400

        UnderwritingInfo underwritingInfo200 = new UnderwritingInfo(premium: 200, commission: -50)
        UnderwritingInfo underwritingInfo100 = new UnderwritingInfo(premium: 100, commission: -5)
        dynamicCommission.inUnderwritingInfo << underwritingInfo200 << underwritingInfo100

        dynamicCommission.internalWiring()
        dynamicCommission.doCalculation()

        assertEquals '# outUnderwritingInfoModified packets', 0, dynamicCommission.outUnderwritingInfoModified.size()
        assertEquals '# outUnderwritingInfoUnmodified packets', 2, dynamicCommission.outUnderwritingInfoUnmodified.size()
        assertEquals 'underwritingInfo200', -50, dynamicCommission.outUnderwritingInfoUnmodified[0].commission
        assertEquals 'underwritingInfo100', -5, dynamicCommission.outUnderwritingInfoUnmodified[1].commission
    }

    void testSingleNullCommission() {
        DynamicCommission dynamicCommission = new DynamicCommission()
        dynamicCommission.addSubComponent new Commission(name: 'commission 1') // default: no commission

        Claim claim300 = new Claim(value: 300)
        Claim claim400 = new Claim(value: 400)
        dynamicCommission.inClaims << claim300 << claim400

        UnderwritingInfo underwritingInfo200 = new UnderwritingInfo(premium: 200, commission: -50)
        UnderwritingInfo underwritingInfo100 = new UnderwritingInfo(premium: 100, commission: -5)
        dynamicCommission.inUnderwritingInfo << underwritingInfo200 << underwritingInfo100

        dynamicCommission.internalWiring()
        dynamicCommission.doCalculation()

        assertEquals '# outUnderwritingInfoModified packets', 0, dynamicCommission.outUnderwritingInfoModified.size()
        assertEquals '# outUnderwritingInfoUnmodified packets', 2, dynamicCommission.outUnderwritingInfoUnmodified.size()
        assertEquals 'underwritingInfo200', -50, dynamicCommission.outUnderwritingInfoUnmodified[0].commission
        assertEquals 'underwritingInfo100', -5, dynamicCommission.outUnderwritingInfoUnmodified[1].commission
    }

    void testSingleZeroCommission() {
        DynamicCommission dynamicCommission = new DynamicCommission()
        dynamicCommission.addSubComponent new Commission(
                name: 'commission 1',
                parmCommissionStrategy:
                CommissionStrategyType.getStrategy(
                        CommissionStrategyType.FIXEDCOMMISSION, [commission: 0d]),
                parmApplicableStrategy:
                ApplicableStrategyType.getStrategy(
                        ApplicableStrategyType.ALL, [:]),
                simulationScope: CommissionTests.getTestSimulationScope(2010),
        )

        Claim claim300 = new Claim(value: 300)
        Claim claim400 = new Claim(value: 400)
        dynamicCommission.inClaims << claim300 << claim400

        UnderwritingInfo underwritingInfo200 = new UnderwritingInfo(premium: 200, commission: -50)
        UnderwritingInfo underwritingInfo100 = new UnderwritingInfo(premium: 100, commission: -5)
        dynamicCommission.inUnderwritingInfo << underwritingInfo200 << underwritingInfo100

        dynamicCommission.internalWiring()
        dynamicCommission.doCalculation()

        assertEquals '# outUnderwritingInfoModified packets', 2, dynamicCommission.outUnderwritingInfoModified.size()
        assertEquals '# outUnderwritingInfoUnmodified packets', 0, dynamicCommission.outUnderwritingInfoUnmodified.size()
        assertEquals 'underwritingInfo200', -50, dynamicCommission.outUnderwritingInfoModified[0].commission
        assertEquals 'underwritingInfo100', -5, dynamicCommission.outUnderwritingInfoModified[1].commission
    }

    void testSingleFixedCommission() {
        DynamicCommission dynamicCommission = new DynamicCommission()
        dynamicCommission.addSubComponent new Commission(
                name: 'commission 1',
                parmCommissionStrategy:
                CommissionStrategyType.getStrategy(
                        CommissionStrategyType.FIXEDCOMMISSION, [commission: 0.3d]),
                parmApplicableStrategy:
                ApplicableStrategyType.getStrategy(
                        ApplicableStrategyType.ALL, [:]),
                simulationScope: CommissionTests.getTestSimulationScope(2010),
        )

        UnderwritingInfo underwritingInfo200 = new UnderwritingInfo(premium: 200, commission: -50)
        UnderwritingInfo underwritingInfo100 = new UnderwritingInfo(premium: 100, commission: -5)
        dynamicCommission.inUnderwritingInfo << underwritingInfo200 << underwritingInfo100

        dynamicCommission.internalWiring()
        dynamicCommission.doCalculation()

        assertEquals '# outUnderwritingInfoModified packets', 2, dynamicCommission.outUnderwritingInfoModified.size()
        assertEquals 'underwritingInfo200', -0.3 * 200 - 50, dynamicCommission.outUnderwritingInfoModified[0].commission
        assertEquals 'underwritingInfo100', -0.3 * 100 - 5, dynamicCommission.outUnderwritingInfoModified[1].commission
    }

    void testSelectiveCommissions() {
        SimulationScope simulationScope = CommissionTests.getTestSimulationScope(2010)
        DynamicCommission dynamicCommission = new DynamicCommission()

        /**
         * Create 6 contracts numbered 1..6 that belong to 4 commissions numbered 0,1,2,3
         * where no contract belongs to commission 0, contract 1 belongs to commission 1,
         * contracts 2-3 belong to commission 2, and contracts 4-6 belong to commission 3.
         *
         * Give each contract one UnderwritingInfo with premium 100 and prior commission
         * a distinct power of 2 (so contract 1 has UnderwritingInfo with prior commission 1,
         * contract 2 prior commission 2, contract 3 prior commission 4, 4:8, 5:16 & 6:32).
         * Assign commission j a rate of (10*j) percent. Expect the following total commissions:
         *
         *  Commission Contract UWInfo PremiumWritten PriorCommission  NewCommission TotalCommission
         *    0 (0%)       -        -         -               -               -             -
         *    1 (10%)      1        1        100              1              10            11
         *    2 (20%)      2        2        100              2              20            22
         *    2 (20%)      3        3        100              4              20            24
         *    3 (30%)      4        4        100              8              30            38
         *    3 (30%)      5        5        100             16              30            46
         *    3 (30%)      6        6        100             32              30            62
         */

        List<ReinsuranceContract> contract = new ArrayList<ReinsuranceContract>(6)
        contract.add new ReinsuranceContract(name: "contract 1")
        contract.add new ReinsuranceContract(name: "contract 2")
        contract.add new ReinsuranceContract(name: "contract 3")
        contract.add new ReinsuranceContract(name: "contract 4")
        contract.add new ReinsuranceContract(name: "contract 5")
        contract.add new ReinsuranceContract(name: "contract 6")
        simulationScope.model.allComponents << contract[0] << contract[1] << contract[2] << contract[3] << contract[4] << contract[5]

        List<UnderwritingInfo> underwritingInfo = new ArrayList<UnderwritingInfo>(6)
        underwritingInfo.add new UnderwritingInfo(reinsuranceContract: contract[0], premium: 100d, commission: -1)
        underwritingInfo.add new UnderwritingInfo(reinsuranceContract: contract[1], premium: 100d, commission: -2)
        underwritingInfo.add new UnderwritingInfo(reinsuranceContract: contract[2], premium: 100d, commission: -4)
        underwritingInfo.add new UnderwritingInfo(reinsuranceContract: contract[3], premium: 100d, commission: -8)
        underwritingInfo.add new UnderwritingInfo(reinsuranceContract: contract[4], premium: 100d, commission: -16)
        underwritingInfo.add new UnderwritingInfo(reinsuranceContract: contract[5], premium: 100d, commission: -32)
        dynamicCommission.inUnderwritingInfo << underwritingInfo[0] << underwritingInfo[1] << underwritingInfo[2]
        dynamicCommission.inUnderwritingInfo << underwritingInfo[3] << underwritingInfo[4] << underwritingInfo[5]

        dynamicCommission.addSubComponent new Commission(
                name: "commission 0",
                parmCommissionStrategy:
                CommissionStrategyType.getStrategy(
                        CommissionStrategyType.FIXEDCOMMISSION, [commission: 0d]),
                parmApplicableStrategy:
                ApplicableStrategyType.getStrategy(
                        ApplicableStrategyType.CONTRACT, [applicableContracts: new ComboBoxTableMultiDimensionalParameter(
                                ['no such contract'], ['Applicable Contracts'], IReinsuranceContractMarker)]),
                simulationScope: simulationScope,
        )
        dynamicCommission.addSubComponent new Commission(
                name: "commission 1",
                parmCommissionStrategy:
                CommissionStrategyType.getStrategy(
                        CommissionStrategyType.FIXEDCOMMISSION, [commission: 0.1d]),
                parmApplicableStrategy:
                ApplicableStrategyType.getStrategy(
                        ApplicableStrategyType.CONTRACT, [applicableContracts: new ComboBoxTableMultiDimensionalParameter(
                                ["contract 1"], ['Applicable Contracts'], IReinsuranceContractMarker)]),
                simulationScope: simulationScope,
        )
        dynamicCommission.addSubComponent new Commission(
                name: "commission 2",
                parmCommissionStrategy:
                CommissionStrategyType.getStrategy(
                        CommissionStrategyType.FIXEDCOMMISSION, [commission: 0.2d]),
                parmApplicableStrategy:
                ApplicableStrategyType.getStrategy(
                        ApplicableStrategyType.CONTRACT, [applicableContracts: new ComboBoxTableMultiDimensionalParameter(
                                ["contract 2", "contract 3"], ['Applicable Contracts'], IReinsuranceContractMarker)]),
                simulationScope: simulationScope,
        )
        dynamicCommission.addSubComponent new Commission(
                name: "commission 3",
                parmCommissionStrategy:
                CommissionStrategyType.getStrategy(
                        CommissionStrategyType.FIXEDCOMMISSION, [commission: 0.3d]),
                parmApplicableStrategy:
                ApplicableStrategyType.getStrategy(
                        ApplicableStrategyType.CONTRACT, [applicableContracts: new ComboBoxTableMultiDimensionalParameter(
                                ["contract 4", "contract 5", "contract 6"], ['Applicable Contracts'], IReinsuranceContractMarker)]),
                simulationScope: simulationScope,
        )
        dynamicCommission.componentList.each { Commission commission ->
            commission.parmApplicableStrategy.applicableContracts.setSimulationModel simulationScope.model
        }

        dynamicCommission.internalWiring()
        dynamicCommission.doCalculation()

        assertEquals '# outUnderwritingInfoUnmodified packets', 0, dynamicCommission.outUnderwritingInfoUnmodified.size()
        assertEquals '# outUnderwritingInfoModified packets', 6, dynamicCommission.outUnderwritingInfoModified.size()

        Map<Integer, Double> expectedCommission = [1: 11, 2: 22, 3: 24, 4: 38, 5: 46, 6: 62]
        List<Integer> expectedOutPacketOrder = [4, 5, 6, 2, 3, 1] // must contain each key of expectedCommission exactly once
        // ORDER BY dynamicCommission.addSubComponent.order DESC, dynamicCommission.inUnderwritingInfo.order ASC
        for (int i = 0; i < 6; i++) {
            UnderwritingInfo outUWInfo = dynamicCommission.outUnderwritingInfoModified[i]
            int contractNumber = outUWInfo.reinsuranceContract.name.replace("contract ", "").toInteger()
            assertTrue "underwritingInfo from contract $contractNumber was calculated", expectedCommission.containsKey(contractNumber)
            double expectedValue = -expectedCommission.get(contractNumber)
            double computedValue = outUWInfo.commission
            int expectedOrder = expectedOutPacketOrder.indexOf(contractNumber)
            assertEquals "underwritingInfo from contract $contractNumber had the correct commission", expectedValue, computedValue
            assertEquals "underwritingInfo from contract $contractNumber was in the correct order", expectedOrder, i
            expectedCommission.remove(contractNumber)
        }
        assertTrue "all expected underwritingInfo packets were processed", expectedCommission.size() == 0
    }
}