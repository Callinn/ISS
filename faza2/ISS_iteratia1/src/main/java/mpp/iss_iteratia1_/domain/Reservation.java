package mpp.iss_iteratia1_.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Reservation extends Entity<Long>{

    private User user;
    private List<Seat> seats;
    private Play play;

    public Reservation(User user, List<Seat> seats, Play play) {
        this.user = user;
        this.seats = seats;
        this.play = play;

    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public List<Seat> getSeats() {
        return seats;
    }
    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }
    public Play getPlay() {
        return play;
    }
    public void setPlay(Play play) {
        this.play = play;
    }
    @Override
    public String toString() {
        return "Reservation{" +
                "user=" + user +
                ", seats=" + seats +
                ", play=" + play +
                '}';
    }
}
