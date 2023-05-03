package se.conva.icicle.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import se.conva.icicle.domain.TimeEntry;
import se.conva.icicle.repository.TimeEntryRepository;
import se.conva.icicle.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link se.conva.icicle.domain.TimeEntry}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class TimeEntryResource {

    private final Logger log = LoggerFactory.getLogger(TimeEntryResource.class);

    private static final String ENTITY_NAME = "timeEntry";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TimeEntryRepository timeEntryRepository;

    public TimeEntryResource(TimeEntryRepository timeEntryRepository) {
        this.timeEntryRepository = timeEntryRepository;
    }

    /**
     * {@code POST  /time-entries} : Create a new timeEntry.
     *
     * @param timeEntry the timeEntry to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new timeEntry, or with status {@code 400 (Bad Request)} if the timeEntry has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/time-entries")
    public ResponseEntity<TimeEntry> createTimeEntry(@Valid @RequestBody TimeEntry timeEntry) throws URISyntaxException {
        log.debug("REST request to save TimeEntry : {}", timeEntry);
        if (timeEntry.getId() != null) {
            throw new BadRequestAlertException("A new timeEntry cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TimeEntry result = timeEntryRepository.save(timeEntry);
        return ResponseEntity
            .created(new URI("/api/time-entries/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /time-entries/:id} : Updates an existing timeEntry.
     *
     * @param id the id of the timeEntry to save.
     * @param timeEntry the timeEntry to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated timeEntry,
     * or with status {@code 400 (Bad Request)} if the timeEntry is not valid,
     * or with status {@code 500 (Internal Server Error)} if the timeEntry couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/time-entries/{id}")
    public ResponseEntity<TimeEntry> updateTimeEntry(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TimeEntry timeEntry
    ) throws URISyntaxException {
        log.debug("REST request to update TimeEntry : {}, {}", id, timeEntry);
        if (timeEntry.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, timeEntry.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!timeEntryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TimeEntry result = timeEntryRepository.save(timeEntry);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, timeEntry.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /time-entries/:id} : Partial updates given fields of an existing timeEntry, field will ignore if it is null
     *
     * @param id the id of the timeEntry to save.
     * @param timeEntry the timeEntry to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated timeEntry,
     * or with status {@code 400 (Bad Request)} if the timeEntry is not valid,
     * or with status {@code 404 (Not Found)} if the timeEntry is not found,
     * or with status {@code 500 (Internal Server Error)} if the timeEntry couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/time-entries/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TimeEntry> partialUpdateTimeEntry(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TimeEntry timeEntry
    ) throws URISyntaxException {
        log.debug("REST request to partial update TimeEntry partially : {}, {}", id, timeEntry);
        if (timeEntry.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, timeEntry.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!timeEntryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TimeEntry> result = timeEntryRepository
            .findById(timeEntry.getId())
            .map(existingTimeEntry -> {
                if (timeEntry.getDate() != null) {
                    existingTimeEntry.setDate(timeEntry.getDate());
                }
                if (timeEntry.getMinutesWorked() != null) {
                    existingTimeEntry.setMinutesWorked(timeEntry.getMinutesWorked());
                }
                if (timeEntry.getTaskName() != null) {
                    existingTimeEntry.setTaskName(timeEntry.getTaskName());
                }

                return existingTimeEntry;
            })
            .map(timeEntryRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, timeEntry.getId().toString())
        );
    }

    /**
     * {@code GET  /time-entries} : get all the timeEntries.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of timeEntries in body.
     */
    @GetMapping("/time-entries")
    public List<TimeEntry> getAllTimeEntries() {
        log.debug("REST request to get all TimeEntries");
        return timeEntryRepository.findAll();
    }

    /**
     * {@code GET  /time-entries/:id} : get the "id" timeEntry.
     *
     * @param id the id of the timeEntry to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the timeEntry, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/time-entries/{id}")
    public ResponseEntity<TimeEntry> getTimeEntry(@PathVariable Long id) {
        log.debug("REST request to get TimeEntry : {}", id);
        Optional<TimeEntry> timeEntry = timeEntryRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(timeEntry);
    }

    /**
     * {@code DELETE  /time-entries/:id} : delete the "id" timeEntry.
     *
     * @param id the id of the timeEntry to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/time-entries/{id}")
    public ResponseEntity<Void> deleteTimeEntry(@PathVariable Long id) {
        log.debug("REST request to delete TimeEntry : {}", id);
        timeEntryRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
