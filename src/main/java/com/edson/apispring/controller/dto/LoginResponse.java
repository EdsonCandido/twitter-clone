package com.edson.apispring.controller.dto;

public record LoginResponse(String accessToken, Long expiresIn) {
}
