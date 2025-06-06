package mpp.iss_iteratia1_.domain.validators;

import mpp.iss_iteratia1_.domain.Play;

public class PlayValidator implements Validator<Play> {
    @Override
    public void validate(Play entity) throws ValidationException {
        StringBuilder errors = new StringBuilder();

        if (entity.getPlayName() == null || entity.getPlayName().trim().isEmpty()) {
            errors.append("Numele piesei nu poate fi gol.\n");
        }

        if (entity.getDate() == null) {
            errors.append("Data piesei nu poate fi null.\n");
        }

        if (entity.getSeats() == null || entity.getSeats().isEmpty()) {
            errors.append("Trebuie să existe cel puțin un loc disponibil pentru piesă.\n");
        }

        if (errors.length() > 0) {
            throw new ValidationException(errors.toString());
        }
    }
}
