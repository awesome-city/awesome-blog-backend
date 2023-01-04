package com.github.awesome_city.blog.api.presentation.controller;

import com.github.awesome_city.blog.api.constant.error.NoPermissionException;
import com.github.awesome_city.blog.api.constant.error.ResourceNotFoundException;
import com.github.awesome_city.blog.api.domain.common.PagingEntity;
import com.github.awesome_city.blog.api.domain.entity.Site;
import com.github.awesome_city.blog.api.domain.repository.SiteRepository;
import com.github.awesome_city.blog.api.util.aspect.Log;
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

@Controller("/sites")
public class SiteController {

  private final SiteRepository repository;
  private final Validator validator;

  public SiteController(SiteRepository repository, Validator validator) {
    this.repository = repository;
    this.validator = validator;
  }

  @Get
  @Log
  public PagingEntity<Site> getSites(
      @Header("X-USER-ID") String userId,
      @QueryValue Optional<Integer> limit,
      @QueryValue Optional<String> nextPageToken
  ) {
    return repository.findByUser(userId, limit.orElse(1000), nextPageToken.orElse(null));
  }

  @Post
  @Status(HttpStatus.CREATED)
  @Log
  public Site createSite(
      @Header("X-USER-ID") String userId,
      @Body Site site
  ) {
    site.setOwner(userId);
    return repository.create(site);
  }

  @Get("/{id}")
  @Log
  public Site getSiteById(
      @Header("X-USER-ID") String userId,
      @PathVariable @NotBlank String id
  ) {
    return repository.findById(id)
        .filter(site -> {
          if (userId.equals(site.getOwner())) {
            return true;
          } else {
            throw new NoPermissionException(
                "no permission to access this site [id = " + site.getId() + "]");
          }
        })
        .orElseThrow(() -> new ResourceNotFoundException("site not found"));
  }

  @Put("/{id}")
  @Log
  public Site updateSite(
      @Header("X-USER-ID") String userId,
      @PathVariable @NotBlank String id,
      @Body Site site
  ) {
    site.setId(id);
    site.setOwner(userId);
    return repository.update(site);
  }

  @Delete("/{id}")
  @Status(HttpStatus.NO_CONTENT)
  @Log
  public void delete(
      @PathVariable @NotBlank String id
  ) {
    repository.delete(id);
  }

  @Get("/find-by-domain/{domain}")
  @Log
  public Site findByDomain(
      @PathVariable @NotBlank String domain
  ) {
    return repository.findByDomain(domain)
        .orElseThrow(() -> new ResourceNotFoundException("site not found"));
  }
}
