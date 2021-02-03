package seanizer.json.organizer.value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import org.immutables.value.Value.Immutable;

/**
 * A container of rejected duplicate records, which will be serialized to the path pointed to by the
 * `--rejected` parameter.
 */
@Immutable
@JsonSerialize(as = ImmutableRejectedLeadsContainer.class)
@JsonDeserialize(as = ImmutableRejectedLeadsContainer.class)
public interface RejectedLeadsContainer {

  List<RejectedLead> leads();
}
