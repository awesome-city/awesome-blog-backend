package com.github.awesome_city.blog.api.domain.repository;

import com.github.awesome_city.blog.api.domain.common.PagingEntity;
import com.github.awesome_city.blog.api.domain.entity.Site;
import io.micronaut.core.annotation.NonNull;
import java.util.Optional;
import javax.validation.Valid;

public interface SiteRepository {

  PagingEntity<Site> findAll(
      Integer limit,
      String nextPageToken
  );

  PagingEntity<Site> findByUser(
      @NonNull String userId,
      Integer limit,
      String nextPageToken
  );

  Optional<Site> findById(
      @NonNull String id
  );

  Optional<Site> findByDomain(
      @NonNull String domain
  );

  Site create(
      @NonNull @Valid Site site
  );

  Site update(
      @NonNull @Valid Site site
  );

  void delete(
      @NonNull String id
  );


}
