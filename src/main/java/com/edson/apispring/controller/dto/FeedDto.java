package com.edson.apispring.controller.dto;

import java.util.List;

public record FeedDto(List<FeedItensDto> feedItens, int page, int pageSize, int totalPages, long totalElements) {
}
