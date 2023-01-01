package com.github.awesome_city.blog.api.domain.entity;

import com.github.awesome_city.blog.api.domain.common.Identified;
import com.github.awesome_city.blog.api.domain.common.Validatable;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Introspected
public class Tag extends Validatable implements Identified {

  @NotEmpty
  private String site;

  @Nullable
  private String id;

  @NotEmpty
  private String name;

  @NotEmpty
  @Pattern(regexp = "^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$")
  private String color;

  public String getSite() {
    return site;
  }

  public void setSite(String site) {
    this.site = site;
  }

  @Override
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public static Tag.Builder builder() {
    return new Tag.Builder();
  }

  public static class Builder {

    @NotBlank
    private String site;

    @NotBlank
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    @Pattern(regexp = "^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$")
    private String color;

    public Tag build() {
      Tag tag = new Tag();
      tag.site = site;
      tag.id = id;
      tag.name = name;
      tag.color = color;
      return tag;
    }

    public Builder site(String site) {
      this.site = site;
      return this;
    }

    public Builder id(String id) {
      this.id = id;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder color(String color) {
      this.color = color;
      return this;
    }
  }
}
