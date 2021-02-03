package seanizer.json.organizer.value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import org.immutables.value.Value.Immutable;

/**
 * The application's input and primary output format, a container of {@link Lead Leads}.
 */
@Immutable
@JsonSerialize(as = ImmutableLeadsContainer.class)
@JsonDeserialize(as = ImmutableLeadsContainer.class)
public interface LeadsContainer {

  List<Lead> leads();
}
