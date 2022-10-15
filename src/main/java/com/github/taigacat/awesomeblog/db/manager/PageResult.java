package com.github.taigacat.awesomeblog.db.manager;

import java.util.List;

public record PageResult<T>(List<T> list, String lastEvaluatedKey) {
}
