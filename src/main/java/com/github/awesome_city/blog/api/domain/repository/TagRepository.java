package com.github.awesome_city.blog.api.domain.repository;

import com.github.awesome_city.blog.api.domain.common.PagingEntity;
import com.github.awesome_city.blog.api.domain.entity.Tag;
import io.micronaut.core.annotation.NonNull;
import java.util.Optional;

public interface TagRepository {

  PagingEntity<Tag> findAll(
      @NonNull String site,
      Integer limit,
      String nextPageToken
  );

  Optional<Tag> findById(
      @NonNull String site,
      @NonNull String id
  );

  Tag create(
      @NonNull Tag tag
  );

  Tag update(
      @NonNull Tag tag
  );

  void delete(
      @NonNull String site,
      @NonNull String id
  );

}
