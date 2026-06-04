package com.ats.user.domain.model;

public record PageQuery(int page, int size) {
    public PageQuery {
        if (page < 0) throw new IllegalArgumentException("Page must be >= 0");
        if (size < 1) throw new IllegalArgumentException("Size must be >= 1");
    }
}
