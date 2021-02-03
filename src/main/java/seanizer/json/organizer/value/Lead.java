package seanizer.json.organizer.value;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.OffsetDateTime;
import java.util.function.IntSupplier;
import org.immutables.value.Value.Immutable;


/**
 * A single lead record, as defined in the input file.
 */
@Immutable
@JsonSerialize(as = ImmutableLead.class)
@JsonDeserialize(as = ImmutableLead.class)
public interface Lead {

  String OFFSET_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ssxxx";

  @JsonProperty("_id")
  String id();

  String email();

  String firstName();

  String lastName();

  String address();

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = OFFSET_DATETIME_PATTERN)
  OffsetDateTime entryDate();

  /**
   * Convert this lead to a candidate, using the provided {@link IntSupplier} as value for {@link
   * OrderedLead#order()}.
   */
  default Candidate toCandidate(IntSupplier orderSupplier) {
    OrderedLead orderedLead = ImmutableOrderedLead.builder()
                                                  .payLoad(this)
                                                  .order(orderSupplier.getAsInt())
                                                  .build();
    return ImmutableCandidate.builder().lead(orderedLead).build();
  }
}
