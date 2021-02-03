package seanizer.json.organizer.value;

import java.util.Optional;
import org.immutables.value.Value.Immutable;

/**
 * A candidate for deduplication. Considered a duplicate iff it has a {@link
 * #duplicationMessage()}.
 */
@Immutable
public interface Candidate {

  OrderedLead lead();

  Optional<String> duplicationMessage();

  default Lead payLoad() {
    return lead().payLoad();
  }

  default boolean isDuplicate() {
    return duplicationMessage().isPresent();
  }


}
