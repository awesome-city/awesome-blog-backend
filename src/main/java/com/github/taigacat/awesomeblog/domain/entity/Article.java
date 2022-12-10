package com.github.taigacat.awesomeblog.domain.entity;

import com.github.taigacat.awesomeblog.domain.common.Identified;
import io.micronaut.core.annotation.NonNull;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class Article implements Identified {

	@NonNull
	@NotBlank
	private String id;
}
