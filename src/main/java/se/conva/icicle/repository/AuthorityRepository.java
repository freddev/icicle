package se.conva.icicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.conva.icicle.domain.Authority;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {}
