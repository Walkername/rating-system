package ru.walkername.rating_system.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.walkername.rating_system.models.Rating;
import ru.walkername.rating_system.repositories.RatingsRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class RatingsService {

    private final RatingsRepository ratingsRepository;

    @Autowired
    public RatingsService(RatingsRepository ratingsRepository) {
        this.ratingsRepository = ratingsRepository;
    }

    public Rating findOne(int userId, int movieId) {
        Optional<Rating> rating = ratingsRepository.findByUserIdAndMovieId(userId, movieId);
        return rating.orElse(null);
    }

    @Transactional
    public void save(Rating rating) {
        ratingsRepository.save(rating);
    }

    @Transactional
    public void update(int id, Rating updatedRating) {
        updatedRating.setRatingId(id);
        ratingsRepository.save(updatedRating);
    }

    @Transactional
    public void delete(int id) {
        ratingsRepository.deleteById(id);
    }

    public List<Rating> getRatingsByUser(int id) {
        return ratingsRepository.findByUserId(id);
    }

    public List<Rating> getRatingsByMovie(int id) {
        return ratingsRepository.findByMovieId(id);
    }

}
