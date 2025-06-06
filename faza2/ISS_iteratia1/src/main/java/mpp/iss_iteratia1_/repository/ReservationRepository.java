package mpp.iss_iteratia1_.repository;

import mpp.iss_iteratia1_.domain.Reservation;

public interface ReservationRepository extends Repository<Long, Reservation> {
    Iterable<Reservation> findByUserId(Long userId);
}