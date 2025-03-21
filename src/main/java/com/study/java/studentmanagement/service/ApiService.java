package com.study.java.studentmanagement.service;

import com.study.java.studentmanagement.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ApiService {
    private final RestTemplate restTemplate;

    public <T> ApiResponse<T> get(String url, ParameterizedTypeReference<ApiResponse<T>> responseType) {
        ResponseEntity<ApiResponse<T>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                responseType);
        return response.getBody();
    }

    public <T> ApiResponse<T> post(String url, Object request,
            ParameterizedTypeReference<ApiResponse<T>> responseType) {
        HttpEntity<Object> entity = new HttpEntity<>(request);
        ResponseEntity<ApiResponse<T>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                responseType);
        return response.getBody();
    }

    public <T> ApiResponse<T> put(String url, Object request, ParameterizedTypeReference<ApiResponse<T>> responseType) {
        HttpEntity<Object> entity = new HttpEntity<>(request);
        ResponseEntity<ApiResponse<T>> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                entity,
                responseType);
        return response.getBody();
    }

    public <T> ApiResponse<T> delete(String url, ParameterizedTypeReference<ApiResponse<T>> responseType) {
        ResponseEntity<ApiResponse<T>> response = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                null,
                responseType);
        return response.getBody();
    }
}