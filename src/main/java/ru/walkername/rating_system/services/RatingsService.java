package ru.walkername.rating_system.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.walkername.rating_system.models.Rating;
import ru.walkername.rating_system.repositories.RatingsRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RatingsService {

    private final RatingsRepository ratingsRepository;

    @Autowired
    public RatingsService(RatingsRepository ratingsRepository) {
        this.ratingsRepository = ratingsRepository;
    }

    @KafkaListener(topics = "${kafka.topic.name}", groupId = "rating-service-group")
    @Transactional
    public void listen(String message) {
        System.out.println("Rating received: " + message);
        ObjectMapper mapper = new ObjectMapper();
        try {
            Rating rating = mapper.readValue(message, Rating.class);
            save(rating);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void save(Rating rating) {
        ratingsRepository.save(rating);
    }

    public List<Rating> getRatingsByUser(int id) {
        return ratingsRepository.findByUserId(id);
    }

    public List<Rating> getRatingsByMovie(int id) {
        return ratingsRepository.findByMovieId(id);
    }

}
