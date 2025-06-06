package mpp.iss_iteratia1_.services;

import mpp.iss_iteratia1_.domain.Entity;
import java.util.Optional;

public interface IService<ID, E extends Entity<ID>> {
    Optional<E> findOne(ID id);
    Iterable<E> findAll();
    Optional<E> save(E entity);
    Optional<E> delete(ID id);
    Optional<E> update(E entity);
}