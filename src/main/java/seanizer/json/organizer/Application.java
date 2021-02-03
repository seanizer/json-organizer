package seanizer.json.organizer;

import static seanizer.json.organizer.dedup.PropertyBasedDeduplicationStrategy.propertyBasedDeduplicationStrategy;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import seanizer.json.organizer.dedup.DeduplicationStrategy;
import seanizer.json.organizer.value.Candidate;
import seanizer.json.organizer.value.ImmutableLeadsContainer;
import seanizer.json.organizer.value.ImmutableOutput;
import seanizer.json.organizer.value.ImmutableRejectedLead;
import seanizer.json.organizer.value.ImmutableRejectedLeadsContainer;
import seanizer.json.organizer.value.ImmutableRejectedLeadsContainer.Builder;
import seanizer.json.organizer.value.Lead;
import seanizer.json.organizer.value.LeadsContainer;
import seanizer.json.organizer.value.Output;
import seanizer.json.organizer.value.RejectedLead;
import seanizer.json.organizer.value.RejectedLeadsContainer;

final class Application {

  private static final Application INSTANCE = new Application();
  private final List<DeduplicationStrategy> deduplicationStrategies;


  private Application() {
    this.deduplicationStrategies = ImmutableList.of(
        propertyBasedDeduplicationStrategy("_id", Lead::id),
        propertyBasedDeduplicationStrategy("email", Lead::email)
    );
  }

  public static Application instance() {
    return INSTANCE;
  }

  public Output process(LeadsContainer input) {
    List<Candidate> candidates = extractCandidates(input);
    Builder rejected = ImmutableRejectedLeadsContainer.builder();
    for (DeduplicationStrategy strategy : deduplicationStrategies) {
      candidates = applyStrategy(strategy, candidates, rejected);
    }
    return buildOutput(candidates, rejected.build());
  }

  private List<Candidate> extractCandidates(LeadsContainer input) {
    AtomicInteger counter = new AtomicInteger();
    IntSupplier orderSupplier = counter::incrementAndGet;
    return input.leads()
                .stream()
                .map(lead -> lead.toCandidate(orderSupplier))
                .collect(Collectors.toList());
  }

  private List<Candidate> applyStrategy(DeduplicationStrategy strategy, List<Candidate> candidates,
                                        Builder rejectedLeadsGoHere) {
    strategy.prepare(candidates);
    return candidates.stream()
                     .map(strategy::process)
                     .filter(weedOutDuplicates(rejectedLeadsGoHere))
                     .collect(Collectors.toList());
  }

  private Predicate<Candidate> weedOutDuplicates(Builder rejectedLeads) {
    return candidate -> {
      boolean duplicate = candidate.isDuplicate();
      if (duplicate) {
        // this feels ugly, but we want to fail fast on known duplicates
        // and this way we don't need to re-process them in the next strategy
        rejectedLeads.addLead(toRejectedLead(candidate));
      }
      return !duplicate;
    };
  }

  private RejectedLead toRejectedLead(Candidate candidate) {
    String reason = candidate.duplicationMessage().orElseThrow(NoSuchElementException::new);
    return ImmutableRejectedLead.builder().record(candidate.payLoad()).reason(reason).build();
  }

  private ImmutableOutput buildOutput(List<Candidate> candidates, RejectedLeadsContainer rejected) {
    List<Lead> nonDupes = candidates.stream().map(Candidate::payLoad).collect(Collectors.toList());
    LeadsContainer accepted = ImmutableLeadsContainer.builder().leads(nonDupes).build();
    return ImmutableOutput.builder().rejected(rejected).accepted(accepted).build();
  }

}
