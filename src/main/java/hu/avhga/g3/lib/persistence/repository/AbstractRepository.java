package hu.avhga.g3.lib.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AbstractRepository<A> extends JpaRepository<A, Long>, JpaSpecificationExecutor<A> {

}
