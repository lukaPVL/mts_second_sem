package com.example.service;

import com.example.model.Task;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FavoritesService {

  private static final String FAVORITES_SESSION_KEY = "favoriteTaskIds";

  private final TaskService taskService;

  public void addToFavorite(Long taskId, HttpSession session) {
    taskService.findById(taskId);
    Set<Long> favorites = getFavoritesSet(session);
    favorites.add(taskId);
    session.setAttribute(FAVORITES_SESSION_KEY, favorites);
  }

  public void removeFromFavorite(Long taskId, HttpSession session) {
    Set<Long> favorites = getFavoritesSet(session);
    favorites.remove(taskId);
    session.setAttribute(FAVORITES_SESSION_KEY, favorites);
  }
  /*    Как по умному организовать связь TaskService и FavoritesService ?
        - чтобы при удалении таски (TaskService) она удалялась и в куках (FavoritesService).
        Я думал просто добавить в TaskService, FavoriteService + HttpSession, но ллм сказала,
        что так лучше не делать, ибо взаимная связь сервисов - это плохо...
        - я решил просто доджить удаленные таски при выдачи favoriteTasks :)
   */
  public List<Task> getFavoriteTasks(HttpSession session) {
    Set<Long> favorites = getFavoritesSet(session);
    return favorites.stream()
      .map(id -> {
        try {
          return taskService.findById(id);
        } catch (Exception e) {
          return null;
        }
      })
      .filter(Objects::nonNull)
      .toList();
  }

  private Set<Long> getFavoritesSet(HttpSession session) {
    Set<Long> favorites = (Set<Long>) session.getAttribute(FAVORITES_SESSION_KEY);
    if (favorites == null) {
      favorites = new HashSet<>();
      session.setAttribute(FAVORITES_SESSION_KEY, favorites);
    }
    return favorites;
  }
}
