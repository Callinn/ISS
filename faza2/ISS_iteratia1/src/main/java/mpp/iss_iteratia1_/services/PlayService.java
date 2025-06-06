package mpp.iss_iteratia1_.services;

import mpp.iss_iteratia1_.domain.Play;
import mpp.iss_iteratia1_.repository.PlayRepository;
import java.util.Optional;

public class PlayService implements IService<Long, Play> {
    private PlayRepository playRepository;

    public PlayService(PlayRepository playRepository) {
        this.playRepository = playRepository;
    }

    @Override
    public Optional<Play> findOne(Long id) {
        return playRepository.findOne(id);
    }

    @Override
    public Iterable<Play> findAll() {
        return playRepository.findAll();
    }

    @Override
    public Optional<Play> save(Play entity) {
        return playRepository.save(entity);
    }

    @Override
    public Optional<Play> delete(Long id) {
        return playRepository.delete(id);
    }

    @Override
    public Optional<Play> update(Play entity) {
        return playRepository.update(entity);
    }
}