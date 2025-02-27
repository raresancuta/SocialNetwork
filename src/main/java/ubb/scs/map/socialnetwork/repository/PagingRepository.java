package ubb.scs.map.socialnetwork.repository;

import ubb.scs.map.socialnetwork.domain.Entity;
import ubb.scs.map.socialnetwork.utils.paging.Page;
import ubb.scs.map.socialnetwork.utils.paging.Pageable;

public interface PagingRepository<ID, E extends Entity<ID>> extends Repository<ID, E> {
    Page<E> findAllOnPage(Pageable pageable);
}
