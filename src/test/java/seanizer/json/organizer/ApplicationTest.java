package seanizer.json.organizer;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import java.io.FileInputStream;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import seanizer.json.organizer.value.Lead;
import seanizer.json.organizer.value.LeadsContainer;
import seanizer.json.organizer.value.Output;
import seanizer.json.organizer.value.RejectedLead;

class ApplicationTest {

  // verify that every record is returned exactly once, either in output.accepted(), or in
  // output.rejected()
  @Test
  void sanityTest() throws Exception {
    FileInputStream stream = new FileInputStream("src/test/resources/leads.json");
    LeadsContainer leadsContainer = Json.instance().deserialize(stream, LeadsContainer.class);
    Output output = Application.instance().process(leadsContainer);
    List<Lead> allLeads = ImmutableList.<Lead>builder()
        .addAll(output.accepted().leads())
        .addAll(output.rejected()
                      .leads()
                      .stream()
                      .map(RejectedLead::record)
                      .collect(Collectors.toList()))
        .build();
    assertThat(leadsContainer.leads()).containsExactlyInAnyOrderElementsOf(allLeads);
  }

}