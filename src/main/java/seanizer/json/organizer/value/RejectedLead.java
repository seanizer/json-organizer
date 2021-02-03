package seanizer.json.organizer.value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Immutable;

/**
 * A rejected duplicate record, with the duplicate reason.
 */
@Immutable
@JsonSerialize(as = ImmutableRejectedLead.class)
@JsonDeserialize(as = ImmutableRejectedLead.class)
public interface RejectedLead {

  String reason();

  Lead record();

}
