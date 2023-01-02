package com.github.awesome_city.blog.api.infrastructure.db.dynamodb.repository;

import com.github.awesome_city.blog.api.constant.error.ResourceConflictException;
import com.github.awesome_city.blog.api.constant.error.ResourceNotFoundException;
import com.github.awesome_city.blog.api.domain.common.PagingEntity;
import com.github.awesome_city.blog.api.domain.entity.Site;
import com.github.awesome_city.blog.api.domain.repository.SiteRepository;
import com.github.awesome_city.blog.api.infrastructure.db.dynamodb.common.DynamoDbManager;
import com.github.awesome_city.blog.api.infrastructure.db.dynamodb.entity.site.SiteDomainRelation;
import com.github.awesome_city.blog.api.infrastructure.db.dynamodb.entity.site.SiteObject;
import com.github.awesome_city.blog.api.util.aspect.Log;
import com.github.awesome_city.blog.api.util.id.IdGenerator;
import jakarta.inject.Singleton;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
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
  @Log
  public PagingEntity<Site> findAll(Integer limit, String nextPageToken) {
    PagingEntity<SiteObject> dynamoEntities = manager.findAllItems(
        new SiteObject(), limit, nextPageToken
    );
    return new PagingEntity<>(
        dynamoEntities.getList().stream()
            .map(e -> (Site) e)
            .collect(Collectors.toList()),
        dynamoEntities.getNextPageToken()
    );
  }

  @Override
  @Log
  public Optional<Site> findById(String id) {
    LOGGER.debug("find site by id [id = " + id + "]");
    return manager.findItem(
        new SiteObject(Site.builder().id(id).build())
    ).map(e -> e);
  }

  @Override
  @Log
  public Optional<Site> findByDomain(String domain) {
    LOGGER.debug("find site by domain [domain = " + domain + "]");
    return manager.findItem(new SiteDomainRelation(domain))
        .map(relation -> new SiteObject(
            Site.builder()
                .domain(relation.getDomain())
                .id(relation.getId())
                .build()
        ))
        .flatMap(manager::findItem);
  }

  @Override
  @Log
  public Site create(Site site) {
    // 同じdomainで他にサイトが存在している場合はエラー
    if (this.existDomain(site)) {
      throw new ResourceConflictException("domain is already used by another site.");
    }

    // Object
    SiteObject object = new SiteObject(site);
    object.setId(idGenerator.generate());
    LOGGER.info("create site entity [ id = " + object.getId() + "]");
    manager.putItem(object);

    // Relation - domain
    manager.putItem(new SiteDomainRelation(object));

    return object;
  }

  @Override
  public Site update(Site site) {
    if (site.getId() == null) {
      throw new IllegalArgumentException();
    }

    Site old = findById(site.getId()).orElseThrow(
        () -> new ResourceNotFoundException("site not found")
    );

    // 同じdomainで他にサイトが存在している場合はエラー
    if (this.existDomain(site)) {
      throw new ResourceConflictException("domain is already used by another site.");
    }

    // Object
    manager.updateItem(new SiteObject(site));

    // Relation - domain
    if (!site.getDomain().equals(old.getDomain())) {
      manager.deleteItem(new SiteDomainRelation(old));
      manager.putItem(new SiteDomainRelation(site));
    }

    return site;
  }

  @Override
  public void delete(String id) {
    UnaryOperator<SiteObject> deleteRelations = (site) -> {
      manager.deleteItem(new SiteDomainRelation(site));
      return site;
    };

    this.findById(id)
        .map(SiteObject::new)
        .map(deleteRelations)
        .ifPresent(manager::deleteItem);
  }

  private boolean existDomain(Site site) {
    return manager.findItem(new SiteDomainRelation(site.getDomain()))
        .map(r -> !r.getId().equals(site.getId()))
        .orElse(false);
  }
}
