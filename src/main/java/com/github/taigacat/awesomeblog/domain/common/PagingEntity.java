package com.github.taigacat.awesomeblog.domain.common;

import java.util.List;

public record PagingEntity<T>(List<T> list, String nextPageToken) {
}
