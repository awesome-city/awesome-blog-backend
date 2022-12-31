package com.github.awesome_city.blog.api.infrastructure.db.dynamodb.repository;

import com.github.awesome_city.blog.api.constant.error.ResourceNotFoundException;
import com.github.awesome_city.blog.api.domain.common.PagingEntity;
import com.github.awesome_city.blog.api.domain.entity.Tag;
import com.github.awesome_city.blog.api.domain.repository.TagRepository;
import com.github.awesome_city.blog.api.infrastructure.db.dynamodb.common.DynamoDbManager;
import com.github.awesome_city.blog.api.infrastructure.db.dynamodb.entity.tag.TagObject;
import com.github.awesome_city.blog.api.util.aspect.Log;
import com.github.awesome_city.blog.api.util.id.IdGenerator;
import jakarta.inject.Singleton;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class DynamoDbTagRepository implements TagRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(DynamoDbTagRepository.class);

  private final DynamoDbManager manager;

  private final IdGenerator idGenerator;

  public DynamoDbTagRepository(
      DynamoDbManager manager,
      IdGenerator idGenerator
  ) {
    this.manager = manager;
    this.idGenerator = idGenerator;
  }

  @Override
  @Log
  public PagingEntity<Tag> findAll(String site, Integer limit, String nextPageToken) {
    PagingEntity<TagObject> dynamoEntities = manager.findAllItems(
        new TagObject(Tag.builder().site(site).build()),
        limit,
        nextPageToken
    );

    return new PagingEntity<>(
        dynamoEntities.getList().stream()
            .map(t -> (Tag) t)
            .collect(Collectors.toList()),
        dynamoEntities.getNextPageToken()
    );
  }

  @Override
  @Log
  public Optional<Tag> findById(String site, String id) {
    LOGGER.debug("find tag by id [id = " + id + "]");
    return manager.findItem(new TagObject(
        Tag.builder().site(site).id(id).build()
    )).map(t -> t);
  }

  @Override
  @Log
  public Tag create(Tag tag) {
    TagObject object = new TagObject(tag);
    object.setId(idGenerator.generate());
    LOGGER.info("create tag entity [ id = " + object.getId() + "]");
    manager.putItem(object);

    return object;
  }

  @Override
  @Log
  public Tag update(Tag tag) {
    Tag old = findById(tag.getSite(), tag.getId())
        .orElseThrow(
            () -> new ResourceNotFoundException("tag not found")
        );
    assert old != null;

    TagObject object = new TagObject(tag);
    LOGGER.info("update tag entity [ id = " + object.getId() + "]");
    manager.updateItem(object);

    return object;
  }

  @Override
  @Log
  public void delete(String site, String id) {
    this.findById(site, id).ifPresent(
        tag -> manager.deleteItem(new TagObject(tag))
    );
  }
}
