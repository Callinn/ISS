package mpp.iss_iteratia1_.domain.validators;

import mpp.iss_iteratia1_.domain.Seat;

public class SeatValidator implements Validator<Seat> {
    @Override
    public void validate(Seat entity) throws ValidationException {
        StringBuilder errors = new StringBuilder();

        if (entity.getRow() == null || entity.getRow().trim().isEmpty()) {
            errors.append("Rândul nu poate fi gol.\n");
        }

        if (entity.getNumber() <= 0) {
            errors.append("Numărul locului trebuie să fie pozitiv.\n");
        }

        if (entity.getPrice() < 0) {
            errors.append("Prețul nu poate fi negativ.\n");
        }

        if (errors.length() > 0) {
            throw new ValidationException(errors.toString());
        }
    }
}
