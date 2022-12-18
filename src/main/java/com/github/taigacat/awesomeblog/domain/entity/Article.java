package com.github.taigacat.awesomeblog.domain.entity;

import com.github.taigacat.awesomeblog.domain.common.Identified;
import io.micronaut.core.annotation.NonNull;
import java.time.Instant;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString(callSuper = true)
@NoArgsConstructor
public class Article implements Identified {

  public Article(Status status) {
    this.status = status;
  }

  public Article(Status status, String id) {
    this(status);
    this.id = id;
  }

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
  public enum Status {
    PUBLISHED,
    DRAFT
  }

}
