package bbitai.repository;

import bbitai.domain.User;
import hu.avhga.g3.lib.persistence.repository.AbstractRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends AbstractRepository<User> {
	List<User> findByUsername(String username);
}

