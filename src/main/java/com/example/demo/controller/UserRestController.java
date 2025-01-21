package com.example.demo.controller;

import com.example.demo.dto.UserDto;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * packageName    : com.example.demo.controller
 * fileName       : UserRestController
 * author         : doong2s
 * date           : 2025. 1. 12.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 1. 12.        doong2s       최초 생성
 */
@RestController
@RequestMapping("/api/v1/user")
public class UserRestController {

    @Autowired
    private UserService userService;

    @Value("${SERVICE_ENDPOINT}")
    private String serviceEndpoint;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @GetMapping("/")
    public String healthCheck() {
        // 간단하게 OK 문자열만 응답
        return "OK";
    }

    @GetMapping("/{userNo}")
    public ResponseEntity<Map<String, Object>> getUserByuserNo(@PathVariable String userNo) throws IOException, InterruptedException {

        String url = serviceEndpoint + userNo;
//        String url = serviceEndpoint;
        System.out.println(url);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response);

        ObjectMapper objectMapper = new ObjectMapper();

        // Map<String, Object>로 받아오기
        Map<String, String> result = objectMapper.readValue(response.body(), new TypeReference<Map<String, String>>(){});

        System.out.println(result.get("goodsNo"));   // 8226
        System.out.println(result.get("goodsName")); // prod-8226

        UserDto userDto = userService.getUserByuserNo(userNo);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("User",userDto);
        responseMap.put("Goods",result);
        return ResponseEntity.ok(responseMap);
    }
}
