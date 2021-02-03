package seanizer.json.organizer.dedup;

import static org.assertj.core.api.Assertions.assertThat;
import static seanizer.json.organizer.Data.DATE;
import static seanizer.json.organizer.Data.ID;
import static seanizer.json.organizer.Data.lead;
import static seanizer.json.organizer.dedup.PropertyBasedDeduplicationStrategy.propertyBasedDeduplicationStrategy;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seanizer.json.organizer.value.Candidate;
import seanizer.json.organizer.value.Lead;

class PropertyBasedDeduplicationStrategyTest {

  DeduplicationStrategy underTest;

  @BeforeEach
  void setUp() {
    underTest = propertyBasedDeduplicationStrategy("id", Lead::id);
  }


  @Test
  void pickLatestDate() {
    Lead first = lead();
    Lead second = lead().withEntryDate(DATE.plusYears(1));
    Lead third = lead();

    List<Lead> leads = Arrays.asList(first, second, third);
    assertDuplicatesInList(leads, true, false, true);
  }

  @Test
  void pickLatestInOrder() {
    Lead first = lead();
    Lead second = lead();
    Lead third = lead();

    List<Lead> leads = Arrays.asList(first, second, third);
    assertDuplicatesInList(leads, true, true, false);
  }

  @Test
  void nonConflictingProperties() {
    Lead first = lead().withId(ID + "x");
    Lead second = lead().withId(ID + "y");
    Lead third = lead().withId(ID + "z");

    List<Lead> leads = Arrays.asList(first, second, third);
    assertDuplicatesInList(leads, false, false, false);
  }

  /*
   * Turn a list of Leads into Candidates, run them through the prepare / process cycle, and
   * verify that the outcome is exactly as expected (true means duplicate).
   */
  private void assertDuplicatesInList(List<Lead> leads, Boolean... expectations) {
    AtomicInteger counter = new AtomicInteger();
    List<Candidate> all = leads.stream()
                               .map(l -> l.toCandidate(counter::incrementAndGet))
                               .collect(Collectors.toList());
    underTest.prepare(all);
    List<Candidate> processed = all.stream()
                                   .map(underTest::process)
                                   .collect(Collectors.toList());
    assertThat(processed).extracting(Candidate::isDuplicate).containsExactly(expectations);
  }

}