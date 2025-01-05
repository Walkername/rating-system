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
import ru.walkername.rating_system.utils.RatingErrorResponse;
import ru.walkername.rating_system.utils.RatingNotCreatedException;
import ru.walkername.rating_system.utils.RatingValidator;

import java.util.List;

@RestController
@RequestMapping("/ratings")
@CrossOrigin
public class RatingsController {

    private final RatingsService ratingsService;
    private final RatingValidator ratingValidator;

    @Autowired
    public RatingsController(RatingsService ratingsService, RatingValidator ratingValidator) {
        this.ratingsService = ratingsService;
        this.ratingValidator = ratingValidator;
    }

    @PostMapping("/add")
    public ResponseEntity<HttpStatus> add(
            @RequestBody @Valid Rating rating,
            BindingResult bindingResult
    ) {
        ratingValidator.validate(rating, bindingResult);
        validateRating(bindingResult);
        ratingsService.save(rating);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/edit/{id}")
    public ResponseEntity<HttpStatus> update(
            @PathVariable("id") int id,
            @RequestBody @Valid Rating rating,
            BindingResult bindingResult
    ) {
        validateRating(bindingResult);
        ratingsService.update(id, rating);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> delete(
            @PathVariable("id") int id
    ) {
        ratingsService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private void validateRating(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();

            StringBuilder strError = new StringBuilder();

            for (FieldError error : errors) {
                strError.append(error.getField())
                        .append(" - ")
                        .append(error.getDefaultMessage())
                        .append(";");
            }

            throw new RatingNotCreatedException(strError.toString());
        }
    }

    @GetMapping("/{userId}/{movieId}")
    public Rating getRating(
            @PathVariable("userId") int userId,
            @PathVariable("movieId") int movieId
    ) {
        return ratingsService.findOne(userId, movieId);
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

    @ExceptionHandler
    public ResponseEntity<RatingErrorResponse> handleException(RatingNotCreatedException ex) {
        RatingErrorResponse response = new RatingErrorResponse(
                ex.getMessage(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
