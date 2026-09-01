/*
 * Copyright (C) 2026 Jens Kutschke / BR-LAWYER Team
 *
 * This file is part of j-lawyer.org.
 *
 * j-lawyer.org is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package org.jlawyer.io.rest.v8.pojo;

import java.util.ArrayList;
import java.util.List;

public class RestfulTaskPageV8 {

    private long total;
    private int page;
    private int pageSize;
    private List<RestfulTaskOverviewV8> items = new ArrayList<>();

    public RestfulTaskPageV8() {
    }

    public RestfulTaskPageV8(long total, int page, int pageSize, List<RestfulTaskOverviewV8> items) {
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.items = items;
    }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    public List<RestfulTaskOverviewV8> getItems() { return items; }
    public void setItems(List<RestfulTaskOverviewV8> items) { this.items = items; }
}