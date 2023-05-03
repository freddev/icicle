package se.conva.icicle.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import se.conva.icicle.web.rest.TestUtil;

class TimeEntryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TimeEntry.class);
        TimeEntry timeEntry1 = new TimeEntry();
        timeEntry1.setId(1L);
        TimeEntry timeEntry2 = new TimeEntry();
        timeEntry2.setId(timeEntry1.getId());
        assertThat(timeEntry1).isEqualTo(timeEntry2);
        timeEntry2.setId(2L);
        assertThat(timeEntry1).isNotEqualTo(timeEntry2);
        timeEntry1.setId(null);
        assertThat(timeEntry1).isNotEqualTo(timeEntry2);
    }
}
