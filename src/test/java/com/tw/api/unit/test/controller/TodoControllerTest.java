package com.tw.api.unit.test.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tw.api.unit.test.domain.todo.Todo;
import com.tw.api.unit.test.domain.todo.TodoRepository;
import com.tw.api.unit.test.services.ShowService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(TodoController.class)
@ActiveProfiles(profiles = "test")
public class TodoControllerTest {
    @Autowired
    TodoController todoController;

    @Autowired
    MockMvc mvc;

    @MockBean
    TodoRepository todoRepository;

    @Test
    void should_get_all_todos() throws Exception{
        List<Todo> todos =  new ArrayList<>();
        Todo me = new Todo("Laundry", true);
        Todo me2 = new Todo("Dishes", true);
        todos.add(me);
        todos.add(me2);
        //given
        when(todoRepository.getAll()).thenReturn(todos);
        //when
        ResultActions result = mvc.perform(get("/todos"));
        //then
        result.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Laundry")))
                .andExpect(jsonPath("$[0].completed", is(true)))
                .andExpect(jsonPath("$[1].title", is("Dishes")))
                .andExpect(jsonPath("$[1].completed", is(true)))
                ;
    }

    @Test
    void should_get_by_id() throws Exception {
        List<Todo> todos =  new ArrayList<>();
        Todo me = new Todo(1,"Dishes",true, 1);
        Todo me2 = new Todo(2,"Laundry",true, 2);
        todos.add(me);
        todos.add(me2);
        Optional<Todo> opt = Optional.of(me);

        //given
        when(todoRepository.findById(me.getId())).thenReturn(opt);
        //when
        ResultActions result = mvc.perform(get("/todos/1"));
        //then
        result.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.title", is("Dishes")))
                .andExpect(jsonPath("$.completed", is(true)))
                .andExpect(jsonPath("$.order", is(1)))
        ;
    }

    @Test
    void should_getSize_of_repository() throws Exception {

        List<Todo> todos =  new ArrayList<>();
        Todo me = new Todo("Me",true);
        //given
        when(todoRepository.getAll()).thenReturn(todos);
        //when
        ResultActions result = mvc.perform(post("/todos")
        .content(new ObjectMapper().writeValueAsString(me))
        .contentType(MediaType.APPLICATION_JSON));
        //then
        result.andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(1)))
        ;
    }

    @Test
    void should_delete_todo_by_id() throws Exception {
        List<Todo> todos =  new ArrayList<>();
        Todo me = new Todo(1,"Laundry",true,1);
        todos.add(me);
        Optional<Todo> opt = Optional.of(me);

        //given
        when(todoRepository.findById(me.getId())).thenReturn(opt);
        //when
        ResultActions result = mvc.perform(delete("/todos/1")
                .content(new ObjectMapper().writeValueAsString(me))
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result.andExpect(status().isOk())
                .andDo(print())
        ;
    }

    @Test
    void should_return_not_found_delete_todo_by_id() throws Exception {
        List<Todo> todos =  new ArrayList<>();
        Todo me = new Todo("Laundry",true);
        todos.add(me);
        Optional<Todo> opt = Optional.of(me);

        //given
        when(todoRepository.findById(me.getId())).thenReturn(opt);
        //when
        ResultActions result = mvc.perform(delete("/todos/1")
                .content(new ObjectMapper().writeValueAsString(me))
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result.andExpect(status().isNotFound())
                .andDo(print())
        ;
    }

    @Test
    void should_return_okay_if_able_to_patch() throws Exception {
        List<Todo> todos =  new ArrayList<>();
        Todo me = new Todo(1,"Laundry",true,1);
        todos.add(me);
        Optional<Todo> opt = Optional.of(me);

        //given
        when(todoRepository.findById(me.getId())).thenReturn(opt);
        //when
        ResultActions result = mvc.perform(patch("/todos/1")
                .content(new ObjectMapper().writeValueAsString(me))
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result.andExpect(status().isOk())
                .andDo(print())
        ;
    }

    @Test
    void should_return_not_found_if_unable_to_patch() throws Exception {
        List<Todo> todos =  new ArrayList<>();
        Todo me = new Todo("Laundry",true);
        todos.add(me);
        Optional<Todo> opt = Optional.of(me);

        //given
        when(todoRepository.findById(me.getId())).thenReturn(opt);
        //when
        ResultActions result = mvc.perform(patch("/todos/1")
                .content(new ObjectMapper().writeValueAsString(me))
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result.andExpect(status().isNotFound())
                .andDo(print())
        ;
    }

    @Test
    void should_return_bad_request_if_newTodo_is_null() throws Exception {
        //given
        when(todoController.updateTodo(1, null)).thenReturn(null);
        //when
        ResultActions result = mvc.perform(patch("/todos/1")
                .content(new ObjectMapper().writeValueAsString(null))
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result.andExpect(status().isBadRequest())
                .andDo(print())
        ;
    }
}
