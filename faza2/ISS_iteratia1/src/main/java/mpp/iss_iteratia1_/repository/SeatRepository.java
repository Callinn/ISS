package mpp.iss_iteratia1_.repository;

import mpp.iss_iteratia1_.domain.Seat;

public interface SeatRepository extends Repository<Long, Seat> {
    Iterable<Seat> findByPlayId(Long playId);
    void updateSeatAvailability(Long seatId, boolean available);
}