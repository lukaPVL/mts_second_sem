package com.example.service;

import com.example.exception.TaskNotFoundException;
import com.example.entity.Task;
import com.example.validation.DueDateNotBeforeCreation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.example.repository.TaskRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class TaskService {

	private final TaskRepository taskRepository;

  @Transactional(readOnly = true)
	public List<Task> findAll() {
		return taskRepository.findAll();
	}

  @Transactional(readOnly = true)
  public Task findById(Long id) {
    return taskRepository.findById(id)
      .orElseThrow(() -> new TaskNotFoundException("Task with id " + id + " not found"));
  }

  @Transactional
  @DueDateNotBeforeCreation
	public Task save(Task task) {
		return taskRepository.save(task);
	}

  @Transactional
  @DueDateNotBeforeCreation
  public Task update(Task task) {
    if (task.getId() == null) {
      throw new IllegalArgumentException("Task id cannot be null");
    }
    return taskRepository.save(task);
  }

  /* я же правильно понимаю, что необязательно прописывать .existById ? -
      ибо в реализации deleteById JPA уже прописана логика: ifPresent(this::delete)
      - то есть по сути вызывается findById, который в случае, когда ничего не нашел,
      возвращает пустую обертку Optional.empty() => если id нет в бд, то deleteById не сделает вообще ничего (за счет ifPresent)
      (я, наверно, понял: мы хотим тут прокинуть наш кастомный exceptiont => прописывает руками...)
      То есть мы могли бы, наверно, это даже не прописывать, но тогда пришлось бы копаться с конфигами JPA?
  */
  @Transactional
	public void deleteById(Long id) {
    if (!taskRepository.existsById(id)) {
      throw new TaskNotFoundException("Can't delete: Task " + id + " not found");
    }
		taskRepository.deleteById(id);
	}

  /* нужно ли мне в подобных методах прописывать taskRepository.save(task)
     или одна только поставленная анноташка @Transactional - уже обо всем говорит:
     метод отслеживается hibernate => все обновления сущностей будут синхронизироваться с бд ?
     +
     Изначально, я прописал (как чувствовал) taskRepository.getById() - потом закинул в gemini и он расписал мне, что
     это ошибка, ибо, как я понял, getById() - не сразу делает sql запрос и возвращает объект (или его отсутствие Optional.empty())
     , а просто возвращает ссылку на ячейку из бд (как я понял, это просто обернутый id, по которому в дальнейшем мы будем делать sql запрос)
      => мы узнаем об отстутсвие данных, только когда обратимся к полям объекта,
     на который ссылается taskRepository.getById() => вылетит exception, если объект null, ну или его поле просто не инициализированно.
     То есть:  Task task = taskRepository.getById(id) - все норм (тут TaskProxy создается
                                                        - просто id (все остальные поля - null) + все get/set обращаются в бд, а не к полям в памяти)
               ...
               customObject.setTask(task) - все норм, ибо просто вместо оригинальной Task подкинули наш TaskProxy

               customObjectRepository.save(customObject) - exception, ибо hibernate начал составлять единый запрос в бд =>
               вызвал get TaskProxy => sql запрос в бд => если ничего не нашел - exception
               (то есть hibernate озаботился содердимым ссылки, только когда уже пришлось делать save - до этого он ничего не делал)
     Это так работает ?
   */
  @Transactional(rollbackFor = TaskNotFoundException.class)
  public void bulkCompleteTasks(List<Long> ids) {
    for (Long id : ids) {
      Task task = taskRepository.findById(id)
        .orElseThrow(() -> new TaskNotFoundException("Id:  " + id + " not found"));
      task.setCompleted(true);
      //taskRepository.save(task);
    }
  }
}