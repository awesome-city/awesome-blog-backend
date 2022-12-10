package com.github.taigacat.awesomeblog.presentation.model;

import java.util.List;

public record PagingResponse<T>(List<T> list, String nextPageToken) {
}
