package mpp.iss_iteratia1_.services;

import mpp.iss_iteratia1_.domain.Reservation;
import mpp.iss_iteratia1_.domain.User;
import mpp.iss_iteratia1_.domain.Play;
import mpp.iss_iteratia1_.domain.Seat;
import mpp.iss_iteratia1_.repository.ReservationRepository;
import mpp.iss_iteratia1_.services.SeatService;
import java.util.Optional;
import java.util.List;

public class ReservationService implements IService<Long, Reservation> {
    private ReservationRepository reservationRepository;
    private SeatService seatService;

    public ReservationService(ReservationRepository reservationRepository, SeatService seatService) {
        this.reservationRepository = reservationRepository;
        this.seatService = seatService;
    }

    @Override
    public Optional<Reservation> findOne(Long id) {
        return reservationRepository.findOne(id);
    }

    @Override
    public Iterable<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    @Override
    public Optional<Reservation> save(Reservation entity) {
        return reservationRepository.save(entity);
    }

    @Override
    public Optional<Reservation> delete(Long id) {
        return reservationRepository.delete(id);
    }

    @Override
    public Optional<Reservation> update(Reservation entity) {
        return reservationRepository.update(entity);
    }

    public Iterable<Reservation> findReservationsByUserId(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    public boolean makeReservation(User user, Play play, List<Seat> seats) {
        // Check if all seats are available
        for (Seat seat : seats) {
            if (!seat.isAvailable()) {
                return false;
            }
        }

        // Create reservation
        Reservation reservation = new Reservation(user, seats, play);
        Optional<Reservation> savedReservation = reservationRepository.save(reservation);

        return savedReservation.isEmpty(); // Empty means success in this implementation
    }

    public int calculateTotalPrice(List<Seat> seats) {
        return seats.stream().mapToInt(Seat::getPrice).sum();
    }
}