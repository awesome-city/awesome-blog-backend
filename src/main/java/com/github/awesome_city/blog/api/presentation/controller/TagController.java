package com.github.awesome_city.blog.api.presentation.controller;

import com.github.awesome_city.blog.api.constant.error.BadRequestException;
import com.github.awesome_city.blog.api.constant.error.ResourceNotFoundException;
import com.github.awesome_city.blog.api.domain.common.PagingEntity;
import com.github.awesome_city.blog.api.domain.entity.Tag;
import com.github.awesome_city.blog.api.domain.repository.TagRepository;
import com.github.awesome_city.blog.api.util.aspect.Log;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.annotation.Status;
import io.micronaut.validation.validator.Validator;
import java.util.Optional;
import javax.validation.constraints.NotBlank;

@Controller("/tags")
public class TagController {

  private final TagRepository repository;
  private final Validator validator;

  public TagController(
      TagRepository tagRepository,
      Validator validator
  ) {
    this.repository = tagRepository;
    this.validator = validator;
  }

  @Get
  @Log
  public PagingEntity<Tag> getTags(
      @Header("X-SITE-ID") String site,
      @QueryValue Optional<Integer> limit,
      @QueryValue Optional<String> nextPageToken
  ) {
    return repository.findAll(
        site,
        limit.orElse(100),
        nextPageToken.orElse(null)
    );
  }

  @Post
  @Status(HttpStatus.CREATED)
  @Log
  public Tag createTag(
      @Header("X-SITE-ID") String site,
      @Body Tag tag
  ) {
    tag.setSite(site);
    if (tag.validate(validator)) {
      return repository.create(tag);
    } else {
      throw new BadRequestException("tag has violations");
    }
  }

  @Get("/{id}")
  @Log
  public Tag getTagById(
      @Header("X-SITE-ID") String site,
      @PathVariable @NonNull @NotBlank String id
  ) {
    Optional<Tag> tagOptional = repository.findById(site, id);
    return tagOptional.orElseThrow(() -> new ResourceNotFoundException("tag not found"));
  }

  @Put("/{id}")
  @Log
  public Tag updateTag(
      @Header("X-SITE-ID") String site,
      @PathVariable @NonNull @NotBlank String id,
      @Body Tag tag
  ) {
    tag.setSite(site);
    tag.setId(id);

    if (tag.validate(validator)) {
      return repository.update(tag);
    } else {
      throw new BadRequestException("tag has violations");
    }
  }

  @Delete("/{id}")
  @Status(HttpStatus.NO_CONTENT)
  @Log
  public void delete(
      @Header("X-SITE-ID") String site,
      @PathVariable @NonNull @NotBlank String id
  ) {
    repository.delete(site, id);
  }

}
