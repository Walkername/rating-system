package ru.walkername.rating_system.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.walkername.rating_system.models.Rating;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingsRepository extends JpaRepository<Rating, Integer> {

    Optional<Rating> findByUserIdAndMovieId(int userId, int movieId);

    List<Rating> findByUserId(int id);

    List<Rating> findByMovieId(int id);

}
