package com.example.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferences")
public class PreferencesController {

  private static final String VIEW_PREFERENCE_COOKIE = "viewPreference";
  private static final String DEFAULT_MODE = "detailed";

  @Value("${app.api.version:2.0.0}")
  private String apiVersion;

  @GetMapping("/view")
  public ResponseEntity<String> getViewPreference(
    @CookieValue(value = VIEW_PREFERENCE_COOKIE, defaultValue = DEFAULT_MODE) String viewPreference) {

    return ResponseEntity.ok()
      .header("X-API-Version", apiVersion)
      .body(viewPreference);
  }

  @PostMapping("/view")
  public ResponseEntity<Void> setViewPreference(
    @RequestParam String mode,
    HttpServletResponse response) {

    if (!mode.equals("compact") && !mode.equals("detailed")) {
      return ResponseEntity.badRequest()
        .header("X-API-Version", apiVersion)  // ← добавить
        .build();
    }

    Cookie cookie = new Cookie(VIEW_PREFERENCE_COOKIE, mode);
    cookie.setPath("/");
    cookie.setMaxAge(60 * 60 * 24 * 365);
    cookie.setHttpOnly(true);

    response.addCookie(cookie);

    return ResponseEntity.ok()
      .header("X-API-Version", apiVersion)
      .build();
  }

}
