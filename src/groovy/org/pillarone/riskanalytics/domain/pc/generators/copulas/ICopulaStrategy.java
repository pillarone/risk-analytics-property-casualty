package org.pillarone.riskanalytics.domain.pc.generators.copulas;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface ICopulaStrategy {
    List<Number> getRandomVector();

    List<String> getTargetNames();
}
