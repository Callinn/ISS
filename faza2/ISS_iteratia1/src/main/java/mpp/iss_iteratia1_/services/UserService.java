package mpp.iss_iteratia1_.services;

import mpp.iss_iteratia1_.domain.User;
import mpp.iss_iteratia1_.repository.UserRepository;
import java.util.Optional;

public class UserService implements IService<Long, User> {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findOne(Long id) {
        return userRepository.findOne(id);
    }

    @Override
    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> save(User entity) {
        return userRepository.save(entity);
    }

    @Override
    public Optional<User> delete(Long id) {
        return userRepository.delete(id);
    }

    @Override
    public Optional<User> update(User entity) {
        return userRepository.update(entity);
    }

    public Optional<User> authenticate(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password);
    }
}
