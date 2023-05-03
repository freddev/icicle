package se.conva.icicle.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import se.conva.icicle.IntegrationTest;
import se.conva.icicle.domain.TimeEntry;
import se.conva.icicle.repository.TimeEntryRepository;

/**
 * Integration tests for the {@link TimeEntryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TimeEntryResourceIT {

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Integer DEFAULT_MINUTES_WORKED = 1;
    private static final Integer UPDATED_MINUTES_WORKED = 2;

    private static final String DEFAULT_TASK_NAME = "AAAAAAAAAA";
    private static final String UPDATED_TASK_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/time-entries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TimeEntryRepository timeEntryRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTimeEntryMockMvc;

    private TimeEntry timeEntry;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TimeEntry createEntity(EntityManager em) {
        TimeEntry timeEntry = new TimeEntry().date(DEFAULT_DATE).minutesWorked(DEFAULT_MINUTES_WORKED).taskName(DEFAULT_TASK_NAME);
        return timeEntry;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TimeEntry createUpdatedEntity(EntityManager em) {
        TimeEntry timeEntry = new TimeEntry().date(UPDATED_DATE).minutesWorked(UPDATED_MINUTES_WORKED).taskName(UPDATED_TASK_NAME);
        return timeEntry;
    }

    @BeforeEach
    public void initTest() {
        timeEntry = createEntity(em);
    }

    @Test
    @Transactional
    void createTimeEntry() throws Exception {
        int databaseSizeBeforeCreate = timeEntryRepository.findAll().size();
        // Create the TimeEntry
        restTimeEntryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timeEntry)))
            .andExpect(status().isCreated());

        // Validate the TimeEntry in the database
        List<TimeEntry> timeEntryList = timeEntryRepository.findAll();
        assertThat(timeEntryList).hasSize(databaseSizeBeforeCreate + 1);
        TimeEntry testTimeEntry = timeEntryList.get(timeEntryList.size() - 1);
        assertThat(testTimeEntry.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testTimeEntry.getMinutesWorked()).isEqualTo(DEFAULT_MINUTES_WORKED);
        assertThat(testTimeEntry.getTaskName()).isEqualTo(DEFAULT_TASK_NAME);
    }

    @Test
    @Transactional
    void createTimeEntryWithExistingId() throws Exception {
        // Create the TimeEntry with an existing ID
        timeEntry.setId(1L);

        int databaseSizeBeforeCreate = timeEntryRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTimeEntryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timeEntry)))
            .andExpect(status().isBadRequest());

        // Validate the TimeEntry in the database
        List<TimeEntry> timeEntryList = timeEntryRepository.findAll();
        assertThat(timeEntryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = timeEntryRepository.findAll().size();
        // set the field null
        timeEntry.setDate(null);

        // Create the TimeEntry, which fails.

        restTimeEntryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timeEntry)))
            .andExpect(status().isBadRequest());

        List<TimeEntry> timeEntryList = timeEntryRepository.findAll();
        assertThat(timeEntryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMinutesWorkedIsRequired() throws Exception {
        int databaseSizeBeforeTest = timeEntryRepository.findAll().size();
        // set the field null
        timeEntry.setMinutesWorked(null);

        // Create the TimeEntry, which fails.

        restTimeEntryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timeEntry)))
            .andExpect(status().isBadRequest());

        List<TimeEntry> timeEntryList = timeEntryRepository.findAll();
        assertThat(timeEntryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTaskNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = timeEntryRepository.findAll().size();
        // set the field null
        timeEntry.setTaskName(null);

        // Create the TimeEntry, which fails.

        restTimeEntryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timeEntry)))
            .andExpect(status().isBadRequest());

        List<TimeEntry> timeEntryList = timeEntryRepository.findAll();
        assertThat(timeEntryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTimeEntries() throws Exception {
        // Initialize the database
        timeEntryRepository.saveAndFlush(timeEntry);

        // Get all the timeEntryList
        restTimeEntryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(timeEntry.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].minutesWorked").value(hasItem(DEFAULT_MINUTES_WORKED)))
            .andExpect(jsonPath("$.[*].taskName").value(hasItem(DEFAULT_TASK_NAME)));
    }

    @Test
    @Transactional
    void getTimeEntry() throws Exception {
        // Initialize the database
        timeEntryRepository.saveAndFlush(timeEntry);

        // Get the timeEntry
        restTimeEntryMockMvc
            .perform(get(ENTITY_API_URL_ID, timeEntry.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(timeEntry.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.minutesWorked").value(DEFAULT_MINUTES_WORKED))
            .andExpect(jsonPath("$.taskName").value(DEFAULT_TASK_NAME));
    }

    @Test
    @Transactional
    void getNonExistingTimeEntry() throws Exception {
        // Get the timeEntry
        restTimeEntryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTimeEntry() throws Exception {
        // Initialize the database
        timeEntryRepository.saveAndFlush(timeEntry);

        int databaseSizeBeforeUpdate = timeEntryRepository.findAll().size();

        // Update the timeEntry
        TimeEntry updatedTimeEntry = timeEntryRepository.findById(timeEntry.getId()).get();
        // Disconnect from session so that the updates on updatedTimeEntry are not directly saved in db
        em.detach(updatedTimeEntry);
        updatedTimeEntry.date(UPDATED_DATE).minutesWorked(UPDATED_MINUTES_WORKED).taskName(UPDATED_TASK_NAME);

        restTimeEntryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTimeEntry.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTimeEntry))
            )
            .andExpect(status().isOk());

        // Validate the TimeEntry in the database
        List<TimeEntry> timeEntryList = timeEntryRepository.findAll();
        assertThat(timeEntryList).hasSize(databaseSizeBeforeUpdate);
        TimeEntry testTimeEntry = timeEntryList.get(timeEntryList.size() - 1);
        assertThat(testTimeEntry.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testTimeEntry.getMinutesWorked()).isEqualTo(UPDATED_MINUTES_WORKED);
        assertThat(testTimeEntry.getTaskName()).isEqualTo(UPDATED_TASK_NAME);
    }

    @Test
    @Transactional
    void putNonExistingTimeEntry() throws Exception {
        int databaseSizeBeforeUpdate = timeEntryRepository.findAll().size();
        timeEntry.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTimeEntryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, timeEntry.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(timeEntry))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimeEntry in the database
        List<TimeEntry> timeEntryList = timeEntryRepository.findAll();
        assertThat(timeEntryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTimeEntry() throws Exception {
        int databaseSizeBeforeUpdate = timeEntryRepository.findAll().size();
        timeEntry.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimeEntryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(timeEntry))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimeEntry in the database
        List<TimeEntry> timeEntryList = timeEntryRepository.findAll();
        assertThat(timeEntryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTimeEntry() throws Exception {
        int databaseSizeBeforeUpdate = timeEntryRepository.findAll().size();
        timeEntry.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimeEntryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timeEntry)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TimeEntry in the database
        List<TimeEntry> timeEntryList = timeEntryRepository.findAll();
        assertThat(timeEntryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTimeEntryWithPatch() throws Exception {
        // Initialize the database
        timeEntryRepository.saveAndFlush(timeEntry);

        int databaseSizeBeforeUpdate = timeEntryRepository.findAll().size();

        // Update the timeEntry using partial update
        TimeEntry partialUpdatedTimeEntry = new TimeEntry();
        partialUpdatedTimeEntry.setId(timeEntry.getId());

        partialUpdatedTimeEntry.date(UPDATED_DATE).taskName(UPDATED_TASK_NAME);

        restTimeEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTimeEntry.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTimeEntry))
            )
            .andExpect(status().isOk());

        // Validate the TimeEntry in the database
        List<TimeEntry> timeEntryList = timeEntryRepository.findAll();
        assertThat(timeEntryList).hasSize(databaseSizeBeforeUpdate);
        TimeEntry testTimeEntry = timeEntryList.get(timeEntryList.size() - 1);
        assertThat(testTimeEntry.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testTimeEntry.getMinutesWorked()).isEqualTo(DEFAULT_MINUTES_WORKED);
        assertThat(testTimeEntry.getTaskName()).isEqualTo(UPDATED_TASK_NAME);
    }

    @Test
    @Transactional
    void fullUpdateTimeEntryWithPatch() throws Exception {
        // Initialize the database
        timeEntryRepository.saveAndFlush(timeEntry);

        int databaseSizeBeforeUpdate = timeEntryRepository.findAll().size();

        // Update the timeEntry using partial update
        TimeEntry partialUpdatedTimeEntry = new TimeEntry();
        partialUpdatedTimeEntry.setId(timeEntry.getId());

        partialUpdatedTimeEntry.date(UPDATED_DATE).minutesWorked(UPDATED_MINUTES_WORKED).taskName(UPDATED_TASK_NAME);

        restTimeEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTimeEntry.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTimeEntry))
            )
            .andExpect(status().isOk());

        // Validate the TimeEntry in the database
        List<TimeEntry> timeEntryList = timeEntryRepository.findAll();
        assertThat(timeEntryList).hasSize(databaseSizeBeforeUpdate);
        TimeEntry testTimeEntry = timeEntryList.get(timeEntryList.size() - 1);
        assertThat(testTimeEntry.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testTimeEntry.getMinutesWorked()).isEqualTo(UPDATED_MINUTES_WORKED);
        assertThat(testTimeEntry.getTaskName()).isEqualTo(UPDATED_TASK_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingTimeEntry() throws Exception {
        int databaseSizeBeforeUpdate = timeEntryRepository.findAll().size();
        timeEntry.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTimeEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, timeEntry.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(timeEntry))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimeEntry in the database
        List<TimeEntry> timeEntryList = timeEntryRepository.findAll();
        assertThat(timeEntryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTimeEntry() throws Exception {
        int databaseSizeBeforeUpdate = timeEntryRepository.findAll().size();
        timeEntry.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimeEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(timeEntry))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimeEntry in the database
        List<TimeEntry> timeEntryList = timeEntryRepository.findAll();
        assertThat(timeEntryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTimeEntry() throws Exception {
        int databaseSizeBeforeUpdate = timeEntryRepository.findAll().size();
        timeEntry.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimeEntryMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(timeEntry))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TimeEntry in the database
        List<TimeEntry> timeEntryList = timeEntryRepository.findAll();
        assertThat(timeEntryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTimeEntry() throws Exception {
        // Initialize the database
        timeEntryRepository.saveAndFlush(timeEntry);

        int databaseSizeBeforeDelete = timeEntryRepository.findAll().size();

        // Delete the timeEntry
        restTimeEntryMockMvc
            .perform(delete(ENTITY_API_URL_ID, timeEntry.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TimeEntry> timeEntryList = timeEntryRepository.findAll();
        assertThat(timeEntryList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
