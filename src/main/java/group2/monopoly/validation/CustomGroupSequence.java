package group2.monopoly.validation;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

/**
 * Defines a partial order between validators.
 */
@GroupSequence({PreDefault.class, Default.class, PostDefault.class})
public interface CustomGroupSequence {
}
