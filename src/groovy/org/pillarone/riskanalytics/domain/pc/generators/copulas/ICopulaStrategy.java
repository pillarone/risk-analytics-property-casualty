package org.pillarone.riskanalytics.domain.pc.generators.copulas;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@Deprecated
public interface ICopulaStrategy {
    @Deprecated
    List<Number> getRandomVector();

    @Deprecated
    List<String> getTargetNames();
}
