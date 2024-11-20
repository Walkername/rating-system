package ru.walkername.rating_system.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.walkername.rating_system.models.Rating;

import java.util.List;

@Repository
public interface RatingsRepository extends JpaRepository<Rating, Integer> {

    List<Rating> findByUserId(int id);

    List<Rating> findByMovieId(int id);

}
