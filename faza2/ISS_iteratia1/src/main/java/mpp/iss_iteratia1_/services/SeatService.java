package mpp.iss_iteratia1_.services;

import mpp.iss_iteratia1_.domain.Seat;
import mpp.iss_iteratia1_.repository.SeatRepository;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

public class SeatService implements IService<Long, Seat> {
    private SeatRepository seatRepository;

    public SeatService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @Override
    public Optional<Seat> findOne(Long id) {
        return seatRepository.findOne(id);
    }

    @Override
    public Iterable<Seat> findAll() {
        return seatRepository.findAll();
    }

    @Override
    public Optional<Seat> save(Seat entity) {
        return seatRepository.save(entity);
    }

    @Override
    public Optional<Seat> delete(Long id) {
        return seatRepository.delete(id);
    }

    @Override
    public Optional<Seat> update(Seat entity) {
        return seatRepository.update(entity);
    }

    public Iterable<Seat> findSeatsByPlayId(Long playId) {
        return seatRepository.findByPlayId(playId);
    }

    public List<Seat> getAvailableSeats(Long playId) {
        List<Seat> availableSeats = new ArrayList<>();
        for (Seat seat : seatRepository.findByPlayId(playId)) {
            if (seat.isAvailable()) {
                availableSeats.add(seat);
            }
        }
        return availableSeats;
    }

    public void updateSeatAvailability(Long seatId, boolean available) {
        seatRepository.updateSeatAvailability(seatId, available);
    }

    public boolean reserveSeats(List<Long> seatIds) {
        try {
            for (Long seatId : seatIds) {
                Optional<Seat> seatOpt = seatRepository.findOne(seatId);
                if (seatOpt.isPresent() && seatOpt.get().isAvailable()) {
                    seatRepository.updateSeatAvailability(seatId, false);
                } else {
                    // Rollback previous reservations if any seat is not available
                    for (Long prevSeatId : seatIds) {
                        if (prevSeatId.equals(seatId)) break;
                        seatRepository.updateSeatAvailability(prevSeatId, true);
                    }
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}