package ru.walkername.rating_system.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.walkername.rating_system.dto.RatingDTO;
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
    private final ModelMapper modelMapper;

    @Autowired
    public RatingsController(RatingsService ratingsService, RatingValidator ratingValidator, ModelMapper modelMapper) {
        this.ratingsService = ratingsService;
        this.ratingValidator = ratingValidator;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/add")
    public ResponseEntity<HttpStatus> add(
            @RequestBody @Valid RatingDTO ratingDTO,
            BindingResult bindingResult
    ) {
        Rating rating = convertToRating(ratingDTO);
        ratingValidator.validate(rating, bindingResult);
        validateRating(bindingResult);
        ratingsService.save(rating);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/edit/{id}")
    public ResponseEntity<HttpStatus> update(
            @PathVariable("id") int id,
            @RequestBody @Valid RatingDTO ratingDTO,
            BindingResult bindingResult
    ) {
        validateRating(bindingResult);
        Rating rating = convertToRating(ratingDTO);
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

    @GetMapping("/{userId}/{movieId}")
    public RatingDTO getRating(
            @PathVariable("userId") int userId,
            @PathVariable("movieId") int movieId
    ) {
        Rating rating = ratingsService.findOne(userId, movieId);
        return convertToRatingDTO(rating);
    }

    @GetMapping("/user/{id}")
    public List<RatingDTO> getRatingsByUser(
            @PathVariable("id") int id
    ) {
        List<Rating> ratings = ratingsService.getRatingsByUser(id);
        return ratings.stream().map(this::convertToRatingDTO).toList();
    }

    @GetMapping("/movie/{id}")
    public List<RatingDTO> getRatingsByMovie(
            @PathVariable("id") int id
    ) {
        List<Rating> ratings = ratingsService.getRatingsByUser(id);
        return ratings.stream().map(this::convertToRatingDTO).toList();
    }

    @ExceptionHandler
    public ResponseEntity<RatingErrorResponse> handleException(RatingNotCreatedException ex) {
        RatingErrorResponse response = new RatingErrorResponse(
                ex.getMessage(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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

    private Rating convertToRating(RatingDTO ratingDTO) {
        return modelMapper.map(ratingDTO, Rating.class);
    }

    private RatingDTO convertToRatingDTO(Rating rating) {
        return modelMapper.map(rating, RatingDTO.class);
    }

}
