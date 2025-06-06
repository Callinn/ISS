package mpp.iss_iteratia1_.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class Play extends  Entity<Long> {
    private LocalDateTime date;
    private String playName;
    private List<Seat> seats;

    public Play(LocalDateTime date, String playName, List<Seat> seats) {
        this.date = date;
        this.playName = playName;
        this.seats = seats;
    }

    public LocalDateTime getDate() {
        return date;
    }
    public String getPlayName() {
        return playName;
    }
    public List<Seat> getSeats() {
        return seats;
    }
    @Override
    public String toString() {
        return "Play{" +
                "date=" + date +
                ", playName='" + playName + '\'' +
                ", seats=" + seats +
                '}';
    }
}
