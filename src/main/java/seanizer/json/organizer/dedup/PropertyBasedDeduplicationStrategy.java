package seanizer.json.organizer.dedup;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.function.Function;
import seanizer.json.organizer.value.Candidate;
import seanizer.json.organizer.value.ImmutableCandidate;
import seanizer.json.organizer.value.Lead;
import seanizer.json.organizer.value.OrderedLead;

/**
 * A {@link DeduplicationStrategy} based on extracting a specific property, and building a Multimap
 * (map from a single key to a collection of values) based on that property. The Multimap values are
 * sorted according to the natural order of {@link OrderedLead#compareTo(OrderedLead)}, so in the
 * {@link #process(Candidate)} phase we can just check whether a given element is the last in its
 * Collection (all other members of the collection are duplicates).
 */
public class PropertyBasedDeduplicationStrategy<T> implements DeduplicationStrategy {

  private final Function<Lead, T> extractor;
  private final String propertyName;

  private SetMultimap<T, OrderedLead> map;

  private PropertyBasedDeduplicationStrategy(
      Function<Lead, T> extractor, final String propertyName) {
    this.extractor = extractor;
    this.propertyName = propertyName;

    this.map = ImmutableSetMultimap.of();
  }

  /**
   * Initialize with a property name (this will be used in the duplicationMessage), and a property
   * extractor function.
   */
  public static <T> PropertyBasedDeduplicationStrategy<T> propertyBasedDeduplicationStrategy(
      final String propertyName, Function<Lead, T> extractor) {
    return new PropertyBasedDeduplicationStrategy<T>(extractor, propertyName);
  }

  @Override
  public void prepare(Collection<Candidate> records) {
    this.map = records.stream()
                      .collect(Multimaps.toMultimap(
                          this::key, Candidate::lead,
                          () -> Multimaps.newSortedSetMultimap(Maps.newHashMap(), Sets::newTreeSet)
                      ));
  }

  @Override
  public Candidate process(Candidate candidate) {
    if (candidate.isDuplicate() || isNonDuplicate(candidate)) {
      // if candidate is already marked as duplicate, or we detect that it isn't a duplicate,
      // return it unchanged
      return candidate;
    } else {
      // otherwise return a copy with a duplicationMessage
      return asDuplicate(candidate);
    }
  }

  private Candidate asDuplicate(Candidate candidate) {
    String duplicationMessage = String.format("%s: %s", propertyName, key(candidate));
    return ImmutableCandidate.builder()
                             .from(candidate)
                             .duplicationMessage(duplicationMessage)
                             .build();
  }

  private T key(Candidate candidate) {
    return extractor.apply(candidate.lead().payLoad());
  }


  private boolean isNonDuplicate(Candidate candidate) {
    /*
     * Non-duplicate if key maps to a NavigableSet that has the payload as it's last (highest sorted) item.
     */
    return Optional.ofNullable(map.get(key(candidate)))
                   .filter(NavigableSet.class::isInstance)
                   .map(NavigableSet.class::cast)
                   .map(NavigableSet::last)
                   .filter(it -> candidate.lead().equals(it))
                   .isPresent();
  }

}
