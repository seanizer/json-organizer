package seanizer.json.organizer.value;


import org.immutables.value.Value.Immutable;

/**
 * Application return object, containing accepted records, and records rejected as duplicates.
 */
@Immutable
public interface Output {

  LeadsContainer accepted();

  default int acceptedCount() {
    return this.accepted().leads().size();
  }

  RejectedLeadsContainer rejected();

  default int rejectedCount() {
    return this.rejected().leads().size();
  }
}
