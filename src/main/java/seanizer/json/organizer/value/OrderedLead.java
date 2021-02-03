package seanizer.json.organizer.value;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.function.Function;
import javax.annotation.Nonnull;
import org.immutables.value.Value.Immutable;

/**
 * A Wrapper around {@link Lead} with a defined order relative to the input data (higher values came
 * last). The natural Order defined in the {@link #COMPARATOR} compares the entryDate property
 * first, and then the incoming order.
 */
@Immutable
public interface OrderedLead extends Comparable<OrderedLead> {

  Comparator<OrderedLead> COMPARATOR = Comparator.comparing(
      (Function<OrderedLead, OffsetDateTime>) o -> o.payLoad().entryDate())
                                                 .thenComparing(OrderedLead::order);

  default int compareTo(@Nonnull OrderedLead that) {
    return COMPARATOR.compare(this, that);
  }

  int order();

  Lead payLoad();
}
