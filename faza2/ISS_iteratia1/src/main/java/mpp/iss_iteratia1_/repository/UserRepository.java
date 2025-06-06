package mpp.iss_iteratia1_.repository;

import mpp.iss_iteratia1_.domain.User;
import java.util.Optional;

public interface UserRepository extends Repository<Long, User> {
    Optional<User> findByUsernameAndPassword(String username, String password);
}