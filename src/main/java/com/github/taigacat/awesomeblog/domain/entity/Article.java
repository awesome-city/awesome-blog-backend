package com.github.taigacat.awesomeblog.domain.entity;

import com.github.taigacat.awesomeblog.domain.common.Identified;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import java.time.Instant;
import java.util.Arrays;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString(callSuper = true)
@Introspected
public class Article implements Identified {

  @NonNull
  @NotBlank
  private String tenant;

  /**
   * 記事ID
   */
  @NonNull
  @NotBlank
  private String id;

  /**
   * 記事識別名
   */
  @NonNull
  @NotBlank
  private String name;

  /**
   * タイトル
   */
  @NonNull
  @NotBlank
  private String title;

  /**
   * タグ
   */
  @NotEmpty
  private Set<String> tags;

  /**
   * ステータス
   */
  @NonNull
  private Status status = Status.DRAFT;

  /**
   * 著者ID
   */
  @NonNull
  private String authorId;

  /**
   * 要約
   */
  private String summary;

  /**
   * 本文
   */
  private String body;

  /**
   * サムネイル画像URL
   */
  private String thumbnailUrl;

  /**
   * カバー画像URL
   */
  private String coverImageUrl;

  /**
   * 公開日
   */
  private Instant publishAt;

  /**
   * 作成日
   */
  private Instant createAt;

  /**
   * 変更日
   */
  private Instant modifyAt;

  /**
   * 記事ステータス
   */
  @Introspected
  public enum Status {
    PUBLISHED("published"),
    DRAFT("draft");

    private final String name;

    Status(final String name) {
      this.name = name;
    }

    public static Status byName(String name) {
      return Arrays.stream(Status.values())
          .filter(s -> s.name.equals(name))
          .findAny()
          .orElse(null);
    }
  }

  @NonNull
  public String getTenant() {
    return tenant;
  }

  public void setTenant(@NonNull String tenant) {
    this.tenant = tenant;
  }

  @Override
  public String getId() {
    return id;
  }

  public void setId(@NonNull String id) {
    this.id = id;
  }

  @NonNull
  public String getName() {
    return name;
  }

  public void setName(@NonNull String name) {
    this.name = name;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Set<String> getTags() {
    return tags;
  }

  public void setTags(Set<String> tags) {
    this.tags = tags;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public String getAuthorId() {
    return authorId;
  }

  public void setAuthorId(String authorId) {
    this.authorId = authorId;
  }

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public String getThumbnailUrl() {
    return thumbnailUrl;
  }

  public void setThumbnailUrl(String thumbnailUrl) {
    this.thumbnailUrl = thumbnailUrl;
  }

  public String getCoverImageUrl() {
    return coverImageUrl;
  }

  public void setCoverImageUrl(String coverImageUrl) {
    this.coverImageUrl = coverImageUrl;
  }

  public Instant getPublishAt() {
    return publishAt;
  }

  public void setPublishAt(Instant publishAt) {
    this.publishAt = publishAt;
  }

  public Instant getCreateAt() {
    return createAt;
  }

  public void setCreateAt(Instant createAt) {
    this.createAt = createAt;
  }

  public Instant getModifyAt() {
    return modifyAt;
  }

  public void setModifyAt(Instant modifyAt) {
    this.modifyAt = modifyAt;
  }

  public static class Builder {

    private String tenant;
    private Status status;
    private String id;
    private String name;
    private Set<String> tags;
    private String authorId;

    public Article build() {
      Article article = new Article();
      article.tenant = tenant;
      article.status = status;
      article.id = id;
      article.name = name;
      article.tags = tags;
      article.authorId = authorId;
      return article;
    }

    public Builder tenant(String tenant) {
      this.tenant = tenant;
      return this;
    }

    public Builder status(Status status) {
      this.status = status;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder id(String id) {
      this.id = id;
      return this;
    }

    public Builder tags(Set<String> tags) {
      this.tags = tags;
      return this;
    }

    public Builder author(String authorId) {
      this.authorId = authorId;
      return this;
    }
  }
}

