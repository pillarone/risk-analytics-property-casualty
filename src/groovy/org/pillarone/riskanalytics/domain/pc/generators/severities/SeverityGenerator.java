package org.pillarone.riskanalytics.domain.pc.generators.severities;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.utils.IRandomNumberGenerator;
import org.pillarone.riskanalytics.domain.utils.RandomNumberGeneratorFactory;
import org.pillarone.riskanalytics.domain.pc.generators.frequency.Frequency;

/**
 *  The severity generator produces severities with number of
 *  individual severities given by the external frequency and
 *  Uniform-distributed individual severities.<br/>
 *  The provided severities at <tt>outSeverities</tt> are in
 *  the range of [0..1].
 *  No parameters are needed to specify a uniform distribution.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class SeverityGenerator extends Component {

    private IRandomNumberGenerator parmGenerator = RandomNumberGeneratorFactory.getUniformGenerator();
    /** Input channel for the number of severities to be generated     */
    private PacketList<Frequency> inClaimCount = new PacketList<Frequency>(Frequency.class);
    /** Output channel for severities.     */
    private PacketList<Severity> outSeverities = new PacketList<Severity>(Severity.class);


    public void doCalculation() {
        for (Frequency frequency : inClaimCount) {
            for (int i = 0; i < frequency.value; i++) {
                outSeverities.add(new Severity((Double) parmGenerator.nextValue()));
            }
        }
        if (outSeverities.size() == 0) {
            outSeverities.add(new Severity(0d));
        }
    }

    public IRandomNumberGenerator getParmGenerator() {
        return parmGenerator;
    }

    public void setParmGenerator(IRandomNumberGenerator parmGenerator) {
        this.parmGenerator = parmGenerator;
    }

    public PacketList<Frequency> getInClaimCount() {
        return inClaimCount;
    }

    public void setInClaimCount(PacketList<Frequency> inClaimCount) {
        this.inClaimCount = inClaimCount;
    }

    public PacketList<Severity> getOutSeverities() {
        return outSeverities;
    }

    public void setOutSeverities(PacketList<Severity> outSeverities) {
        this.outSeverities = outSeverities;
    }
}