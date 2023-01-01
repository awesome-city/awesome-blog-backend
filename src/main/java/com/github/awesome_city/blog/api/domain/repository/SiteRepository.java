package com.github.awesome_city.blog.api.domain.repository;

import com.github.awesome_city.blog.api.domain.common.PagingEntity;
import com.github.awesome_city.blog.api.domain.entity.Site;
import io.micronaut.core.annotation.NonNull;
import java.util.Optional;

public interface SiteRepository {

  PagingEntity<Site> findAll(
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
      @NonNull Site site
  );

  Site update(
      @NonNull Site site
  );

  void delete(
      @NonNull String id
  );


}
