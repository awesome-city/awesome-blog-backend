package com.github.taigacat.awesomeblog.util.uuid;

import io.micronaut.core.annotation.NonNull;

@FunctionalInterface
public interface IdGenerator {

	@NonNull
	String generate();
}
