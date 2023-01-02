package com.github.awesome_city.blog.api.domain.entity;

import com.github.awesome_city.blog.api.domain.common.Identified;
import com.github.awesome_city.blog.api.domain.common.Validatable;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import javax.validation.constraints.NotEmpty;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;


@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Introspected
public class Site extends Validatable implements Identified {

  @Nullable
  private String id;

  @NotEmpty
  private String domain;

  @NotEmpty
  private String title;

  private String subTitle;

  private String coverImageUrl;

  @Override
  public String getId() {
    return id;
  }

  public void setId(@Nullable String id) {
    this.id = id;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getSubTitle() {
    return subTitle;
  }

  public void setSubTitle(String subTitle) {
    this.subTitle = subTitle;
  }

  public String getCoverImageUrl() {
    return coverImageUrl;
  }

  public void setCoverImageUrl(String coverImageUrl) {
    this.coverImageUrl = coverImageUrl;
  }

  public static Site.Builder builder() {
    return new Site.Builder();
  }

  @NoArgsConstructor
  public static class Builder {

    private String id;

    private String domain;

    private String title;

    public Site build() {
      Site site = new Site();
      site.id = this.id;
      site.domain = this.domain;
      site.title = this.title;
      return site;
    }

    public Builder id(String id) {
      this.id = id;
      return this;
    }

    public Builder domain(String domain) {
      this.domain = domain;
      return this;
    }

    public Builder title(String title) {
      this.title = title;
      return this;
    }
  }
}
