package socialnetwork.repository.paging;


import socialnetwork.domain.Entity;
import socialnetwork.repository.CrudRepository;
import socialnetwork.repository.RepositoryOptional;

public interface PagingRepository<ID ,
        E extends Entity<ID>>
        extends RepositoryOptional<ID, E> {

    Page<E> findAll(Pageable pageable);   // Pageable e un fel de paginator
}
