package ru.walkername.rating_system.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ru.walkername.rating_system.dto.NewRatingDTO;
import ru.walkername.rating_system.models.Rating;
import ru.walkername.rating_system.repositories.RatingsRepository;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class RatingsService {

    private final RatingsRepository ratingsRepository;

    private final String MOVIE_SERVICE_API;
    private final RestTemplate restTemplate;

    @Autowired
    public RatingsService(
            RatingsRepository ratingsRepository,
            @Value("${movie.service.url}") String MOVIE_SERVICE_API,
            RestTemplate restTemplate) {
        this.ratingsRepository = ratingsRepository;
        this.MOVIE_SERVICE_API = MOVIE_SERVICE_API;
        this.restTemplate = restTemplate;
    }

    public Rating findOne(int userId, int movieId) {
        Optional<Rating> rating = ratingsRepository.findByUserIdAndMovieId(userId, movieId);
        return rating.orElse(null);
    }

    @Transactional
    public void save(Rating rating) {
        try {
            String url = MOVIE_SERVICE_API + "/movies/update-avg-rating/" + rating.getMovieId();
            System.out.println(url);
            URI uri = new URI(url);
            NewRatingDTO newRatingDTO = new NewRatingDTO(rating.getRating(), 0.0, false);
            restTemplate.patchForObject(uri, newRatingDTO, String.class);

            // Save to db new added rating
            ratingsRepository.save(rating);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void update(int id, Rating updatedRating) {
        Optional<Rating> oldRating = ratingsRepository.findById(id);
        oldRating.ifPresent(value -> {
            try {
                String url = MOVIE_SERVICE_API + "/movies/update-avg-rating/" + value.getMovieId();
                URI uri = new URI(url);
                NewRatingDTO newRatingDTO = new NewRatingDTO(updatedRating.getRating(), value.getRating(), true);
                restTemplate.patchForObject(uri, newRatingDTO, String.class);

                // Save to DB updated rating
                updatedRating.setRatingId(id);
                ratingsRepository.save(updatedRating);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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
