package bbitai.repository;

import bbitai.domain.Photo;
import bbitai.domain.User;
import hu.avhga.g3.lib.persistence.repository.AbstractRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PhotoRepository extends AbstractRepository<Photo> {
	List<Photo> findByUploadedBy(User user);

	Photo findByIdAndUploadedBy(UUID photoId, User user);

	Page<Photo> findByUploadedBy(User user, Pageable pageable);

	List<Photo> findAll();
}

