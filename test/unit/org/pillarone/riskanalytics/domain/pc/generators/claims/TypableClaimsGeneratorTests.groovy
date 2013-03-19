package org.pillarone.riskanalytics.domain.pc.generators.claims

import models.claims.ClaimsModel
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope
import org.pillarone.riskanalytics.core.util.TestPretendInChannelWired
import org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase
import org.pillarone.riskanalytics.domain.pc.constants.FrequencySeverityClaimType
import org.pillarone.riskanalytics.domain.pc.generators.copulas.DependenceStream
import org.pillarone.riskanalytics.domain.pc.generators.copulas.EventDependenceStream
import org.pillarone.riskanalytics.domain.pc.generators.severities.Event
import org.pillarone.riskanalytics.domain.pc.generators.severities.EventSeverity
import org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker
import org.pillarone.riskanalytics.domain.pc.underwriting.RiskBands
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.DistributionType

import umontreal.iro.lecuyer.probdist.*
import org.pillarone.riskanalytics.domain.utils.DistributionModified
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.utils.randomnumbers.UniformDoubleList
import org.pillarone.riskanalytics.core.util.MathUtils
import umontreal.iro.lecuyer.rng.RandomStreamBase
import org.pillarone.riskanalytics.domain.utils.RandomDistribution
import org.pillarone.riskanalytics.domain.utils.FrequencyDistributionType
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */

public class TypableClaimsGeneratorTests extends GroovyTestCase {

    TypableClaimsGenerator claimsGenerator
    RiskBands riskBands = new RiskBands()

    void setUp() {

        claimsGenerator = new TypableClaimsGenerator()
        ComboBoxTableMultiDimensionalParameter uwInfoComboBox = new ComboBoxTableMultiDimensionalParameter(
                ["motor hull"], ["Underwriting Information"], IUnderwritingInfoMarker)
        uwInfoComboBox.comboBoxValues.put('motorHull', riskBands)
        claimsGenerator.setParmUnderwritingInformation(uwInfoComboBox)
        claimsGenerator.setParmClaimsModel(ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.ATTRITIONAL, [
                        "claimsSizeBase": Exposure.PREMIUM_WRITTEN,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),]))
        claimsGenerator.setParmAssociateExposureInfo(RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:]))
        claimsGenerator.setSimulationScope(new SimulationScope(model: new ClaimsModel(), iterationScope: new IterationScope(periodScope: new PeriodScope())))
        ConstraintsFactory.registerConstraint(new DoubleConstraints())
    }

    void testUsage() {
        claimsGenerator.doCalculation()

        assertEquals "one attritional claim", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of attritional claim", 123, claimsGenerator.outClaims[0].ultimate
    }

    void testRelativeClaims() {
        UnderwritingInfo underwritingInfo = new UnderwritingInfo(premium: 1000, numberOfPolicies: 20, origin: riskBands)
        claimsGenerator.inUnderwritingInfo.add(underwritingInfo)

        claimsGenerator.doCalculation()

        assertEquals "one attritional claim", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of attritional claim", 123000, claimsGenerator.outClaims[0].ultimate
    }

    void testNone() {
        claimsGenerator = new TypableClaimsGenerator()
        ComboBoxTableMultiDimensionalParameter uwInfoComboBox = new ComboBoxTableMultiDimensionalParameter(
                ["motor hull"], ["Underwriting Information"], IUnderwritingInfoMarker)
        uwInfoComboBox.comboBoxValues.put('motorHull', riskBands)
        claimsGenerator.setParmUnderwritingInformation(uwInfoComboBox)
        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.NONE, [:])
        claimsGenerator.setParmAssociateExposureInfo(RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:]))
        claimsGenerator.setSimulationScope(new SimulationScope(model: new ClaimsModel()))
        claimsGenerator.doCalculation()

        assertEquals "no claims", 0, claimsGenerator.outClaims.size()
    }

    void testAttritional() {
        claimsGenerator = new TypableClaimsGenerator()
        ComboBoxTableMultiDimensionalParameter uwInfoComboBox = new ComboBoxTableMultiDimensionalParameter(
                ["motor hull"], ["Underwriting Information"], IUnderwritingInfoMarker)
        uwInfoComboBox.comboBoxValues.put('motorHull', riskBands)
        claimsGenerator.setParmUnderwritingInformation(uwInfoComboBox)
        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.ATTRITIONAL, [
                        "claimsSizeBase": Exposure.PREMIUM_WRITTEN,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])
        claimsGenerator.setParmAssociateExposureInfo(RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:]))
        claimsGenerator.setSimulationScope(new SimulationScope(model: new ClaimsModel()))
        claimsGenerator.doCalculation()

        assertEquals "one attritional claim", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of attritional claim", 123, claimsGenerator.outClaims[0].ultimate
    }

    void testOccurrenceAttritional() {
        claimsGenerator = new TypableClaimsGenerator()
        ComboBoxTableMultiDimensionalParameter uwInfoComboBox = new ComboBoxTableMultiDimensionalParameter(
                ["motor hull"], ["Underwriting Information"], IUnderwritingInfoMarker)
        uwInfoComboBox.comboBoxValues.put('motorHull', riskBands)
        claimsGenerator.setParmUnderwritingInformation(uwInfoComboBox)
        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.ATTRITIONAL_WITH_DATE, [
                        "claimsSizeBase": Exposure.PREMIUM_WRITTEN,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "occurrenceDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.957]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])
        claimsGenerator.setParmAssociateExposureInfo(RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:]))
        claimsGenerator.setSimulationScope(new SimulationScope(model: new ClaimsModel()))
        claimsGenerator.doCalculation()

        assertEquals "one attritional claim", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of attritional claim", 123, claimsGenerator.outClaims[0].ultimate
        assertEquals "correct fraction of period of attritional claim", 0.957, claimsGenerator.outClaims[0].fractionOfPeriod
    }

    void testFrequencyAverageAttritional() {
        claimsGenerator = new TypableClaimsGenerator()
        ComboBoxTableMultiDimensionalParameter uwInfoComboBox = new ComboBoxTableMultiDimensionalParameter(
                ["motor hull"], ["Underwriting Information"], IUnderwritingInfoMarker)
        uwInfoComboBox.comboBoxValues.put('motorHull', riskBands)
        claimsGenerator.setParmUnderwritingInformation(uwInfoComboBox)
        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.FREQUENCY_AVERAGE_ATTRITIONAL, [
                        "frequencyBase": FrequencyBase.ABSOLUTE,
                        "frequencyDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 1]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": Exposure.PREMIUM_WRITTEN,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])
        claimsGenerator.setParmAssociateExposureInfo(RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:]))
        claimsGenerator.setSimulationScope(new SimulationScope(model: new ClaimsModel()))
        claimsGenerator.doCalculation()

        assertEquals "one single claim", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of claim", 123, claimsGenerator.outClaims[0].ultimate
    }

    void testFrequencySeverity() {
        claimsGenerator = new TypableClaimsGenerator()
        ComboBoxTableMultiDimensionalParameter uwInfoComboBox = new ComboBoxTableMultiDimensionalParameter(
                ["motor hull"], ["Underwriting Information"], IUnderwritingInfoMarker)
        uwInfoComboBox.comboBoxValues.put('motorHull', riskBands)
        claimsGenerator.setParmUnderwritingInformation(uwInfoComboBox)
        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.FREQUENCY_SEVERITY, [
                        "frequencyBase": FrequencyBase.ABSOLUTE,
                        "frequencyDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 1]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": Exposure.PREMIUM_WRITTEN,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "produceClaim": FrequencySeverityClaimType.SINGLE,])
        claimsGenerator.setParmAssociateExposureInfo(RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:]))
        claimsGenerator.setSimulationScope(new SimulationScope(model: new ClaimsModel()))
        claimsGenerator.doCalculation()

        assertEquals "one single claim", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of claim", 123, claimsGenerator.outClaims[0].ultimate
    }

    void testOccurrenceAndSeverity() {
        claimsGenerator = new TypableClaimsGenerator()
        ComboBoxTableMultiDimensionalParameter uwInfoComboBox = new ComboBoxTableMultiDimensionalParameter(
                ["motor hull"], ["Underwriting Information"], IUnderwritingInfoMarker)
        uwInfoComboBox.comboBoxValues.put('motorHull', riskBands)
        claimsGenerator.setParmUnderwritingInformation(uwInfoComboBox)
        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.OCCURRENCE_AND_SEVERITY, [
                        "frequencyBase": FrequencyBase.ABSOLUTE,
                        "frequencyDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 1]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": Exposure.PREMIUM_WRITTEN,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "occurrenceDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.957]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "produceClaim": FrequencySeverityClaimType.SINGLE,])
        claimsGenerator.setParmAssociateExposureInfo(RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:]))
        claimsGenerator.setSimulationScope(new SimulationScope(model: new ClaimsModel()))
        claimsGenerator.doCalculation()

        assertEquals "one single claim", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of claim", 123, claimsGenerator.outClaims[0].ultimate
        assertEquals "correct fraction of period of claim", 0.957, claimsGenerator.outClaims[0].fractionOfPeriod
    }

    void testExternalSeverity() {
        claimsGenerator = new TypableClaimsGenerator()
        ComboBoxTableMultiDimensionalParameter uwInfoComboBox = new ComboBoxTableMultiDimensionalParameter(
                ["motor hull"], ["Underwriting Information"], IUnderwritingInfoMarker)
        uwInfoComboBox.comboBoxValues.put('motorHull', riskBands)
        claimsGenerator.setParmUnderwritingInformation(uwInfoComboBox)
        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.SEVERITY_OF_EVENT_GENERATOR, [
                        //TODO(2): FrequencyBase.NUMBER_OF_POLICIES with Freq>1
                        "claimsSizeBase": Exposure.ABSOLUTE, //TODO(1): PREMIUM_WRITTEN for AttritionalCG
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.UNIFORM, ['a': 0d, 'b': 1d]),
                        "produceClaim": FrequencySeverityClaimType.AGGREGATED_EVENT,])
        claimsGenerator.setParmAssociateExposureInfo(RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:]))
        claimsGenerator.setSimulationScope(new SimulationScope(model: new ClaimsModel()))
        //TODO(): Test inUnderwritingInfo & inProbabilities (for Attritional) analogously to inEventSeverities
        EventSeverity eventSeverity = new EventSeverity()
        eventSeverity.event = new Event(fractionOfPeriod: 0.3d)
        eventSeverity.value = 0.7d
        EventDependenceStream events = new EventDependenceStream()
        events.severities = [eventSeverity]
        events.marginals = ['motor']
        claimsGenerator.name = 'motor'
        claimsGenerator.inEventSeverities << events
        def channelWired = new TestPretendInChannelWired(claimsGenerator, "inEventSeverities")
        claimsGenerator.doCalculation()

        assertEquals "one single claim", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of claim", 0.7, claimsGenerator.outClaims[0].ultimate
    }

    void testExternalSeverityAndUnderwritingInfo() {
        claimsGenerator = new TypableClaimsGenerator()
        ComboBoxTableMultiDimensionalParameter uwInfoComboBox = new ComboBoxTableMultiDimensionalParameter(
                ["motor hull"], ["Underwriting Information"], IUnderwritingInfoMarker)
        uwInfoComboBox.comboBoxValues.put('motorHull', riskBands)
        claimsGenerator.setParmUnderwritingInformation(uwInfoComboBox)
        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.SEVERITY_OF_EVENT_GENERATOR, [
                        "claimsSizeBase": Exposure.PREMIUM_WRITTEN,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.UNIFORM, ['a': 0d, 'b': 1d]),
                        "produceClaim": FrequencySeverityClaimType.AGGREGATED_EVENT,])
        claimsGenerator.setParmAssociateExposureInfo(RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:]))
        claimsGenerator.setSimulationScope(new SimulationScope(model: new ClaimsModel()))
        // wire an external severity
        EventSeverity eventSeverity = new EventSeverity()
        eventSeverity.event = new Event(fractionOfPeriod: 0.3d)
        eventSeverity.value = 0.7d
        EventDependenceStream events = new EventDependenceStream()
        events.severities = [eventSeverity]
        events.marginals = ['motor']
        claimsGenerator.name = 'motor'
        claimsGenerator.inEventSeverities << events
        def channelWired = new TestPretendInChannelWired(claimsGenerator, "inEventSeverities")
        // wire underwriting info
        UnderwritingInfo underwritingInfo = new UnderwritingInfo(premium: 1000d)
        //underwritingInfo.originalUnderwritingInfo = underwritingInfo
        claimsGenerator.inUnderwritingInfo << underwritingInfo
        claimsGenerator.doCalculation()

        assertEquals "one single claim (premium written)", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of claim (premium written)", 700d, claimsGenerator.outClaims[0].ultimate

        claimsGenerator.outClaims.clear()
        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.SEVERITY_OF_EVENT_GENERATOR, [
                        "claimsSizeBase": Exposure.ABSOLUTE,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.UNIFORM, ['a': 0d, 'b': 1d]),
                        "produceClaim": FrequencySeverityClaimType.AGGREGATED_EVENT,])
        claimsGenerator.doCalculation()

        assertEquals "one single claim (absolute)", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of claim (absolute)", 0.7d, claimsGenerator.outClaims[0].ultimate
    }

    void testOccurrenceAndSeverityAndUnderwritingInfo() {
        claimsGenerator = new TypableClaimsGenerator()
        ComboBoxTableMultiDimensionalParameter uwInfoComboBox = new ComboBoxTableMultiDimensionalParameter(
                ["motor hull"], ["Underwriting Information"], IUnderwritingInfoMarker)
        uwInfoComboBox.comboBoxValues.put('motorHull', riskBands)
        claimsGenerator.setParmUnderwritingInformation(uwInfoComboBox)
        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.OCCURRENCE_AND_SEVERITY, [
                        "frequencyBase": FrequencyBase.ABSOLUTE,
                        "frequencyDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 1]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": Exposure.PREMIUM_WRITTEN,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 12.34d]),
                        "occurrenceDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.501d]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "produceClaim": FrequencySeverityClaimType.SINGLE,])
        claimsGenerator.setParmAssociateExposureInfo(RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:]))
        claimsGenerator.setSimulationScope(new SimulationScope(model: new ClaimsModel()))
        // wire underwriting info
        UnderwritingInfo underwritingInfo = new UnderwritingInfo(premium: 100d, numberOfPolicies: 5d)
        //underwritingInfo.originalUnderwritingInfo = underwritingInfo
        claimsGenerator.inUnderwritingInfo << underwritingInfo
        claimsGenerator.doCalculation()

        assertEquals "one single claim", 1, claimsGenerator.outClaims.size()
        assertEquals "underwriting info available (premium written)", 1, claimsGenerator.inUnderwritingInfo.size()
        assertEquals "correct claim size", 1234d, claimsGenerator.outClaims[0].ultimate
        assertEquals "correct claim date", 0.501d, claimsGenerator.outClaims[0].fractionOfPeriod

        claimsGenerator.outClaims.clear()
        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.OCCURRENCE_AND_SEVERITY, [
                        "frequencyBase": FrequencyBase.ABSOLUTE,
                        "frequencyDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 1]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": Exposure.ABSOLUTE,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 12.34d]),
                        "occurrenceDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.501d]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "produceClaim": FrequencySeverityClaimType.SINGLE,])
        claimsGenerator.doCalculation()

        assertEquals "one single claim (absolute)", 1, claimsGenerator.outClaims.size()
        assertEquals "underwriting info available (absolute)", 1, claimsGenerator.inUnderwritingInfo.size()
        assertEquals "correct claim size (absolute)", 12.34d, claimsGenerator.outClaims[0].ultimate

        claimsGenerator.outClaims.clear()
        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.OCCURRENCE_AND_SEVERITY, [
                        "frequencyBase": FrequencyBase.NUMBER_OF_POLICIES,
                        "frequencyDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 1]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": Exposure.ABSOLUTE,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 12.34d]),
                        "occurrenceDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.501d]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "produceClaim": FrequencySeverityClaimType.SINGLE,])

        claimsGenerator.doCalculation()

        assertEquals "one single claim (number of policies)", 5, claimsGenerator.outClaims.size()
        assertEquals "underwriting info available (number of policies)", 1, claimsGenerator.inUnderwritingInfo.size()
        for (int i = 0; i < 5; i++) {
            assertEquals "correct claim size #policiy $i", 12.34d, claimsGenerator.outClaims[i].ultimate
        }
    }

    void testAttritionalWithInProbability() {
        claimsGenerator = new TypableClaimsGenerator()
        ComboBoxTableMultiDimensionalParameter uwInfoComboBox = new ComboBoxTableMultiDimensionalParameter(
                ["motor hull"], ["Underwriting Information"], IUnderwritingInfoMarker)
        uwInfoComboBox.comboBoxValues.put('motorHull', riskBands)
        claimsGenerator.setParmUnderwritingInformation(uwInfoComboBox)
        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.ATTRITIONAL, [
                        "claimsSizeBase": Exposure.PREMIUM_WRITTEN,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])
        claimsGenerator.setParmAssociateExposureInfo(RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:]))
        claimsGenerator.setSimulationScope(new SimulationScope(model: new ClaimsModel()))
        // wire one inProbability
        DependenceStream probabilities = new DependenceStream()
        probabilities.probabilities = [1.0d]
        probabilities.marginals = ['motor']
        claimsGenerator.name = 'motor'
        claimsGenerator.inProbabilities << probabilities
        def channelWired = new TestPretendInChannelWired(claimsGenerator, "inProbabilities")
        claimsGenerator.doCalculation()

        assertEquals "one attritional claim (p=1)", 1, claimsGenerator.outClaims.size()
        assertEquals "correct attritional claim size (p=1)", 123, claimsGenerator.outClaims[0].ultimate

        claimsGenerator.outClaims.clear()
        claimsGenerator.inProbabilities.clear()
        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.ATTRITIONAL, [
                        "claimsSizeBase": Exposure.PREMIUM_WRITTEN,
                        //"claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.UNIFORM, [a: 0d, b: 1d]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])
        probabilities.probabilities = [0.578d]
        probabilities.marginals = ['motor']
        claimsGenerator.name = 'motor'
        claimsGenerator.inProbabilities << probabilities
        channelWired = new TestPretendInChannelWired(claimsGenerator, "inProbabilities")
        claimsGenerator.doCalculation()

        /*
         * The result is the same with probability 0, because a claimsGenerator MUST provide at least one
         * claim for each iteration & period; otherwise, the key statistical figures would be incorrect.
         */
        assertEquals "one attritional claim (p=0)", 1, claimsGenerator.outClaims.size()
        /*
         * We would need to use a uniform distribution to see an effect.
         */
        assertEquals "correct attritional claim size (p=0)", 0.578, claimsGenerator.outClaims[0].ultimate
    }

    void testAttritionalWithTwoInProbabilities() {
        claimsGenerator = new TypableClaimsGenerator()
        ComboBoxTableMultiDimensionalParameter uwInfoComboBox = new ComboBoxTableMultiDimensionalParameter(
                ["motor hull"], ["Underwriting Information"], IUnderwritingInfoMarker)
        uwInfoComboBox.comboBoxValues.put('motorHull', riskBands)
        claimsGenerator.setParmUnderwritingInformation(uwInfoComboBox)
        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.ATTRITIONAL, [
                        "claimsSizeBase": Exposure.PREMIUM_WRITTEN,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])
        claimsGenerator.setParmAssociateExposureInfo(RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:]))
        claimsGenerator.setSimulationScope(new SimulationScope(model: new ClaimsModel()))
        // wire a first inProbability
        DependenceStream probabilities1 = new DependenceStream()
        probabilities1.probabilities = [0d]
        probabilities1.marginals = ['motors']
        // wire a second inProbability
        DependenceStream probabilities2 = new DependenceStream()
        probabilities2.probabilities = [1d]
        probabilities2.marginals = ['motors']
        claimsGenerator.name = 'motors'
        claimsGenerator.inProbabilities << probabilities1 << probabilities2
        /*
         * Note that the names (probabilities*.marginals[0] & claimsGenerator.name) must all match
         * in order for both inProbabilites to pass through the filter filterProbabilities() and
         * generate the expected error.
         */
        def channelWired = new TestPretendInChannelWired(claimsGenerator, "inProbabilities")
        channelWired = new TestPretendInChannelWired(claimsGenerator, "inProbabilities")
        shouldFail(IllegalArgumentException, { claimsGenerator.doCalculation() })
    }


    void testPartitionFunctionForTruncatedDensity() {
        Distribution dist = DistributionType.getLognormalDistribution(1.0, 1.0)
        double partitionFunction = dist.cdf(105000) - dist.cdf(95000)
        assertEquals "partition function on truncated interval", 0.0, partitionFunction


        Distribution distTruncated = new TruncatedDist((ContinuousDistribution) dist, (Double) 95000, (Double) 105000)
        assertEquals " density left from interval", 0d, distTruncated.density(94000)
        assertEquals " density within interval", Double.POSITIVE_INFINITY, distTruncated.density(100000)
        assertEquals " density right from interval", 0d, distTruncated.density(106000)
        assertEquals " inverse F(0.9)", Double.POSITIVE_INFINITY, distTruncated.inverseF(0.9)
        assertEquals " cumulative distribution of 1000000", Double.NaN, distTruncated.cdf(100000)

        Distribution dist2 = DistributionType.getLognormalDistribution(1.0, 1.0)
        double partitionFunction2 = dist2.cdf(5) - dist2.cdf(2)
        assertEquals "partition function on truncated interval", true, partitionFunction2 > 0 && partitionFunction2 < Double.POSITIVE_INFINITY

        Distribution dist2Truncated = new TruncatedDist((ContinuousDistribution) dist2, 2d, 5d)
        assertEquals " density left from interval", 0d, dist2Truncated.density(1)
        assertEquals " density at left boundary", dist2.density(2) / partitionFunction2, dist2Truncated.density(2)
        assertEquals " density within interval", dist2.density(3) / partitionFunction2, dist2Truncated.density(3)
        assertEquals " density at right boundary", dist2.density(5) / partitionFunction2, dist2Truncated.density(5)
        assertEquals " density right from interval", 0d, dist2Truncated.density(6)
        assertEquals " inverse F(0.9)", true, dist2Truncated.inverseF(0.9) > 2 && dist2Truncated.inverseF(0.9) < 5
    }

    void testPMLData() {

        ConstrainedMultiDimensionalParameter pmlData = new ConstrainedMultiDimensionalParameter(
                [[0.1d, 0.2d, 0.3d, 0.4d, 0.7d, 1.5d, 2.2d, 4.0d, 5.0d], [0.1d, 45d, 200d, 500d, 600d, 800d, 1500d, 2016d, 4000d]], ["return period", "maximum claim"], ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))
        DistributionModified claimsSizeModification = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
        claimsGenerator.setParmClaimsModel(ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.PML,
                ["pmlData": pmlData, "claimsSizeModification": claimsSizeModification, 'produceClaim': FrequencySeverityClaimType.SINGLE]))


        MathUtils.initRandomStreamBase(1531)
        RandomStreamBase referenceStream = MathUtils.getRandomStream(MathUtils.getRandomStreamBase(), 0).clone()
        List<Double> randomNumbers = UniformDoubleList.getDoubles((int) 300, false, referenceStream);
        RandomDistribution poissonDist = (RandomDistribution) FrequencyDistributionType.getStrategy(FrequencyDistributionType.POISSON,
                new HashMap<String, Double>() {
                    {
                        put("lambda", 10);
                    }
                });
        double frequency = poissonDist.getDistribution().inverseF(randomNumbers[0]);
        List<Double> cumProb = []; cumProb[0] = 0
        double[] periods = [0.1d, 0.2d, 0.3d, 0.4d, 0.7d, 1.5d, 2.2d, 4.0d, 5.0d]
        for (int i = 1; i < 9; i++) {
            cumProb[i] = 1d - 0.1d / periods[i]
        }
        TableMultiDimensionalParameter cumValues = new TableMultiDimensionalParameter([[0.1d, 45d, 200d, 500d, 600d, 800d, 1500d, 2016d, 4000d], cumProb], ['observations', 'cumulative probabilities'])
        Map distributionData = new HashMap();
        distributionData.put("discreteEmpiricalCumulativeValues", cumValues);
        RandomDistribution cumEmpiricalDistribution = DistributionType.getStrategy(DistributionType.DISCRETEEMPIRICALCUMULATIVE, distributionData);
        List<Double> claimValues = new ArrayList<Double>();
        for (int i = 0; i < frequency; i++) {
            claimValues.add(cumEmpiricalDistribution.getDistribution().inverseF(randomNumbers[i + 1]));
        }

        claimsGenerator.doCalculation();

        List<Double> outClaimsUltimate = []
        for (int i = 0; i < claimsGenerator.outClaims.size(); i++) {
            outClaimsUltimate[i] = claimsGenerator.outClaims.get(i).getUltimate()
        }

        assertEquals "number of claims", frequency, claimsGenerator.outClaims.size()
        assertEquals "claims", true, claimValues.containsAll(outClaimsUltimate)

    }

}