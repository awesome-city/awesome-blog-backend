package com.github.taigacat.awesomeblog.domain.entity;

import com.github.taigacat.awesomeblog.domain.common.Identified;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import java.time.Instant;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Introspected
@Data
@EqualsAndHashCode
@ToString(callSuper = true)
@NoArgsConstructor
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
  private String title;

  /**
   * タグ
   */
  private Set<String> tags;

  /**
   * ステータス
   */
  private Status status;

  /**
   * 著者ID
   */
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
    PUBLISHED,
    DRAFT
  }

  public static class Builder {

    private String tenant;
    private Status status;
    private String id;

    public Article build() {
      Article article = new Article();
      article.tenant = tenant;
      article.status = status;
      article.id = id;
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

    public Builder id(String id) {
      this.id = id;
      return this;
    }
  }
}
