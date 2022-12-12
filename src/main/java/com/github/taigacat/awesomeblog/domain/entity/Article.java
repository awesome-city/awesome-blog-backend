package com.github.taigacat.awesomeblog.domain.entity;

import com.github.taigacat.awesomeblog.domain.common.Identified;
import io.micronaut.core.annotation.NonNull;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString
public class Article implements Identified {

  @NonNull
  @NotBlank
  private String id;
}
