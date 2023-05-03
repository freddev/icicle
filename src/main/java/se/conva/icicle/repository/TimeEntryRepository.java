package se.conva.icicle.repository;

import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import se.conva.icicle.domain.TimeEntry;

/**
 * Spring Data JPA repository for the TimeEntry entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {
    @Query("select timeEntry from TimeEntry timeEntry where timeEntry.user.login = ?#{principal.username}")
    List<TimeEntry> findByUserIsCurrentUser();
}
