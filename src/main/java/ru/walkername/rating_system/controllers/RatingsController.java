package ru.walkername.rating_system.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.walkername.rating_system.dto.RatingsResponse;
import ru.walkername.rating_system.models.Rating;
import ru.walkername.rating_system.services.RatingsService;

import java.util.List;

@RestController
@RequestMapping("/ratings")
public class RatingsController {

    private final RatingsService ratingsService;

    @Autowired
    public RatingsController(RatingsService ratingsService) {
        this.ratingsService = ratingsService;
    }

    @PostMapping("/add")
    public ResponseEntity<HttpStatus> add(
            @RequestBody @Valid Rating rating,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();

            StringBuilder strError = new StringBuilder();

            for (FieldError error : errors) {
                strError.append(error.getField())
                        .append(" - ")
                        .append(error.getDefaultMessage())
                        .append(";");
            }

            // TODO: throw exception
        }

        ratingsService.save(rating);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/{userId}/{movieId}")
    public Rating getRating(
            @PathVariable("userId") int userId,
            @PathVariable("movieId") int movieId
    ) {
        Rating rating = new Rating();
        // TODO: service get rating
        return rating;
    }

    @GetMapping("/user/{id}")
    public RatingsResponse getRatingsByUser(
            @PathVariable("id") int id
    ) {
        System.out.println(new RatingsResponse(ratingsService.getRatingsByUser(id)));
        return new RatingsResponse(ratingsService.getRatingsByUser(id));
    }

    @GetMapping("/movie/{id}")
    public RatingsResponse getRatingsByMovie(
            @PathVariable("id") int id
    ) {
        return new RatingsResponse(ratingsService.getRatingsByMovie(id));
    }

}
