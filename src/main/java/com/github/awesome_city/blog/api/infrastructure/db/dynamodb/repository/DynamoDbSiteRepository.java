package com.github.awesome_city.blog.api.infrastructure.db.dynamodb.repository;

import com.github.awesome_city.blog.api.domain.common.PagingEntity;
import com.github.awesome_city.blog.api.domain.entity.Site;
import com.github.awesome_city.blog.api.domain.repository.SiteRepository;
import com.github.awesome_city.blog.api.infrastructure.db.dynamodb.common.DynamoDbManager;
import com.github.awesome_city.blog.api.util.id.IdGenerator;
import jakarta.inject.Singleton;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class DynamoDbSiteRepository implements SiteRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(DynamoDbSiteRepository.class);

  private final DynamoDbManager manager;

  private final IdGenerator idGenerator;

  public DynamoDbSiteRepository(
      DynamoDbManager manager,
      IdGenerator idGenerator
  ) {
    this.manager = manager;
    this.idGenerator = idGenerator;
  }

  @Override
  public PagingEntity<Site> findAll(Integer limit, String nextPageToken) {
    return null;
  }

  @Override
  public Optional<Site> findById(String id) {
    return Optional.empty();
  }

  @Override
  public Optional<Site> findByDomain(String domain) {
    return Optional.empty();
  }

  @Override
  public Site create(Site site) {
    return null;
  }

  @Override
  public Site update(Site site) {
    return null;
  }

  @Override
  public void delete(String id) {

  }
}
