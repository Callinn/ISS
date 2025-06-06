package mpp.iss_iteratia1_.domain.validators;

import mpp.iss_iteratia1_.domain.User;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        StringBuilder errors = new StringBuilder();

        if (entity.getUsername() == null || entity.getUsername().trim().isEmpty()) {
            errors.append("Username-ul nu poate fi gol.\n");
        }

        if (entity.getPassword() == null || entity.getPassword().trim().isEmpty()) {
            errors.append("Parola nu poate fi goală.\n");
        }

        // Poți adăuga și validare pentru lungimea minimă sau complexitate

        if (errors.length() > 0) {
            throw new ValidationException(errors.toString());
        }
    }
}
