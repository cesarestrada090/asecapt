package com.fitech.app.commons.application.controllers;

import com.fitech.app.users.application.dto.ResultPage;

import java.util.HashMap;
import java.util.Map;
public abstract class BaseController {
    protected <T> Map<String, Object> prepareResponse(ResultPage<T> resultPageWrapper) {
        Map<String, Object> response = new HashMap<>();
        response.put(getResource(), resultPageWrapper.getPagesResult());
        response.put("currentPage", resultPageWrapper.getCurrentPage());
        response.put("totalItems", resultPageWrapper.getTotalItems());
        response.put("totalPages", resultPageWrapper.getTotalPages());
        return response;
    }
    protected abstract String getResource();
}
