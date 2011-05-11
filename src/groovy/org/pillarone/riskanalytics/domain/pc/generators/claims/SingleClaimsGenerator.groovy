package org.pillarone.riskanalytics.domain.pc.generators.claims

import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.claims.ClaimPacketFactory
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.generators.frequency.Frequency
import org.pillarone.riskanalytics.domain.pc.generators.severities.Severity
import org.pillarone.riskanalytics.domain.utils.*
import org.pillarone.riskanalytics.core.components.ComponentCategory
import org.pillarone.riskanalytics.core.wiring.WiringValidation

/**
 *  The large claims generators generate claims according the number received
 *  over the input channel <tt>inClaimCount</tt> and writes them to the
 *  channel outClaims. Claims are generated as soon as the doCalculation()
 *  method is invoked.<br />
 *  The distribution type is set at the instantiation of the <tt>parmGgenerator</tt>
 *  or whenever setParmGenerator is invoked.<br />
 *  Among the possible distributions are normal, lognormal, pareto, uniform,
 *  constant, poisson and negative binomial.<br />
 *  For scaling purposes underwriting information may be received.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@ComponentCategory(categories = ['CLAIM','GENERATOR','SINGLE'])
class SingleClaimsGenerator extends ClaimsGenerator implements PerilMarker {

    IRandomNumberGenerator generator

    RandomDistribution parmDistribution = DistributionType.getStrategy(ClaimSizeDistributionType.CONSTANT, ["constant": 0d])
    DistributionModified parmModification = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])

    /** Input channel for claim severity to be generated    */
    @WiringValidation(connections= [0, 1], packets= [1, 1])
    PacketList<Severity> inProbability = new PacketList(Severity.class);

    /** Input channel for how many claims are to be generated                                */
    PacketList<Frequency> inClaimCount = new PacketList(Frequency)
    ClaimType claimType = ClaimType.SINGLE

    public void validateWiring() {
        assert "Wiring error: More than one underwriting information source is wired!", maxOneSenderWired(inUnderwritingInfo)
        super.validateWiring();
    }

    public void validateParameterization() {
        if (!parmDistribution) {
            throw new IllegalStateException("SingleClaimsGenerator.noRandomNumberGenerator")
        }
        if (!parmBase.equals(Exposure.ABSOLUTE) && !isReceiverWired(inUnderwritingInfo)) {
            throw new IllegalStateException("SingleClaimsGenerator.invalidExposureBase")
        }

        super.validateParameterization();
    }

    public void doCalculation() {
        generator = getCachedGenerator(parmDistribution, parmModification)
        double scalingFactor = getScalingFactor();

        // generate dates
        IRandomNumberGenerator dateGenerator = RandomNumberGeneratorFactory.getUniformGenerator();

        // generate value claims and build claim object
        int number = 0;
        if (isReceiverWired(inClaimCount)) {
            for (Frequency frequency: inClaimCount) {
                for (int i = 0; i < frequency.value; i++) {
                    Claim claim = ClaimPacketFactory.createPacket();
                    claim.origin = this;
                    claim.claimType = this.claimType;
                    claim.setUltimate((Double) generator.nextValue() * scalingFactor);
                    claim.fractionOfPeriod = (double) dateGenerator.nextValue();
                    outClaims.add(claim);
                }
            }
        }
        if (isReceiverWired(inProbability)) {
            for (Severity probability: inProbability) {
                Claim claim = ClaimPacketFactory.createPacket();
                claim.origin = this;
                claim.claimType = this.claimType;
                claim.setUltimate(parmDistribution.getDistribution().inverseF(probability.value) * scalingFactor);
                claim.fractionOfPeriod = (double) dateGenerator.nextValue();
                getOutClaims().add(claim);
            }
        }
        if (getOutClaims().isEmpty()) {
            Claim claim = ClaimPacketFactory.createPacket();
            claim.origin = this;
            claim.claimType = this.claimType;
            claim.setUltimate(0d);
            claim.fractionOfPeriod = 0d;
            getOutClaims().add(claim);
        }
    }
}
