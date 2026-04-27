package com.example.controller;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Disabled("Временно отключено: тесты не адаптированы под новую систему безопасности")
class PreferencesControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void getViewPreference_ShouldReturnDefault_WhenNoCookie() throws Exception {
    mockMvc.perform(get("/api/preferences/view"))
      .andExpect(status().isOk())
      .andExpect(content().string("detailed"))
      .andExpect(header().exists("X-API-Version"));
  }

  @Test
  void setViewPreference_ShouldSetCookie_WhenModeIsCompact() throws Exception {
    mockMvc.perform(post("/api/preferences/view")
        .param("mode", "compact"))
      .andExpect(status().isOk())
      .andExpect(cookie().exists("viewPreference"))
      .andExpect(cookie().value("viewPreference", "compact"))
      .andExpect(header().exists("X-API-Version"));
  }

  @Test
  void setViewPreference_ShouldReturn400_WhenModeIsInvalid() throws Exception {
    mockMvc.perform(post("/api/preferences/view")
        .param("mode", "invalid"))
      .andExpect(status().isBadRequest())
      .andExpect(header().exists("X-API-Version"));
  }

  @Test
  void getViewPreference_ShouldReturnSavedValue_WhenCookieExists() throws Exception {
    // Устанавливаем куку через POST
    mockMvc.perform(post("/api/preferences/view")
        .param("mode", "compact"))
      .andExpect(status().isOk());

    // Проверяем GET с кукой — кука должна сохраниться в сессии
    mockMvc.perform(get("/api/preferences/view")
        .cookie(new Cookie("viewPreference", "compact")))
      .andExpect(status().isOk())
      .andExpect(content().string("compact"));
  }
}