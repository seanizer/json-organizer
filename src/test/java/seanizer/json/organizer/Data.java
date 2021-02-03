package seanizer.json.organizer;

import java.time.OffsetDateTime;
import seanizer.json.organizer.value.ImmutableLead;

public class Data {

  public static final String ID = "abc123";
  public static final OffsetDateTime DATE = OffsetDateTime.now()
                                                          .withYear(2021)
                                                          .withMonth(2)
                                                          .withDayOfMonth(1)
                                                          .withHour(11)
                                                          .withMinute(12)
                                                          .withSecond(13);
  private static final String EMAIL = "123@abc.com";

  public static ImmutableLead lead() {
    return ImmutableLead.builder()
                        .id(ID)
                        .email(EMAIL)
                        .firstName("First")
                        .lastName("Name")
                        .address("123 Some Street")
                        .entryDate(DATE)
                        .build();
  }
}
