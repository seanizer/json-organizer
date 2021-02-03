package seanizer.json.organizer.dedup;

import java.util.Collection;
import seanizer.json.organizer.value.Candidate;

/**
 * A strategy interface for de-duplicating Leads. It works in two passes:
 * <ol>
 *   <li>{@link #prepare(Collection)} initializes the strategy with a given data dump</li>
 *   <li>{@link #process(Candidate)} marks a Candidate as duplicate, if applicable.
 *   Technically, a new Candidate object is returned in that case.</li>
 * </ol>
 */
public interface DeduplicationStrategy {

  /**
   * Prepare the strategy for the current data batch.
   */
  void prepare(Collection<Candidate> records);

  /**
   * If the candidate is a duplicate, return a new Candidate with the same data, but with a {@link
   * Candidate#duplicationMessage() duplicationMessage}. Otherwise return the original candidate.
   */
  Candidate process(Candidate candidate);

}
