package mpp.iss_iteratia1_.domain.validators;

import mpp.iss_iteratia1_.domain.Reservation;

public class ReservationValidator implements Validator<Reservation> {
    @Override
    public void validate(Reservation entity) throws ValidationException {
        StringBuilder errors = new StringBuilder();

        if (entity.getUser() == null) {
            errors.append("Utilizatorul care face rezervarea nu poate fi null.\n");
        }

        if (entity.getPlay() == null) {
            errors.append("Piesa pentru care se face rezervarea nu poate fi null.\n");
        }

        if (entity.getSeats() == null || entity.getSeats().isEmpty()) {
            errors.append("Trebuie selectat cel puțin un loc pentru rezervare.\n");
        }

        if (errors.length() > 0) {
            throw new ValidationException(errors.toString());
        }
    }
}
