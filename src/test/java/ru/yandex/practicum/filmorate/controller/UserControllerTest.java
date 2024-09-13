package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private static ObjectMapper objectMapper;
    private static ObjectWriter objectWriter;

    @BeforeAll
    static void prepareToTests() {
        objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
        objectWriter = objectMapper.writer();
    }

    @Test
    void createUserAndGetAllUsersShouldBeOk() throws Exception {
        //Проверка добавления валидных пользователей и их получения
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("email1@mail.ru");
        user1.setLogin("Login1");
        user1.setName("Name1");
        user1.setBirthday(LocalDate.of(2001, 1, 1));
        String user1Json = objectWriter.writeValueAsString(user1);
        mockMvc.perform(post("/users").content(user1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("email2@mail.ru");
        user2.setLogin("Login2");
        user2.setName("Name2");
        user2.setBirthday(LocalDate.of(2002, 2, 2));
        String user2Json = objectWriter.writeValueAsString(user2);
        mockMvc.perform(post("/users").content(user2Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        List<User> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);

        MvcResult result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = result.getResponse().getContentAsString();
        List<User> responseUserList = objectMapper.readValue(responseJson, new TypeReference<>() {
        });

        assertIterableEquals(userList, responseUserList);
    }

    @Test
    void createInvalidLoginUserShouldBeFail() throws Exception {
        //Проверка невозможности добавить пользователя с некорректным логином (пустой или с пробелами)
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("email1@mail.ru");
        user1.setLogin(null);
        user1.setName("Name1");
        user1.setBirthday(LocalDate.of(2001, 1, 1));
        String user1Json = objectWriter.writeValueAsString(user1);
        mockMvc.perform(post("/users").content(user1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        user1.setLogin(" ");
        user1Json = objectWriter.writeValueAsString(user1);
        mockMvc.perform(post("/users").content(user1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        user1.setLogin("a b c");
        user1Json = objectWriter.writeValueAsString(user1);
        mockMvc.perform(post("/users").content(user1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //Проверка отсутствия в хранилище пользователей после попыток добавления невалидных пользователей
        MvcResult result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = result.getResponse().getContentAsString();
        List<User> responseUserList = objectMapper.readValue(responseJson, new TypeReference<>() {
        });

        assertIterableEquals(new ArrayList<>(), responseUserList);
    }

    @Test
    void createEmptyNameUserShouldBeOkAndShouldBeEqualsLogin() throws Exception {
        //Проверка смены пустого имени на то же значение, что и в логине при добавлении пользователя
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("email1@mail.ru");
        user1.setLogin("Login1");
        user1.setName(null);
        user1.setBirthday(LocalDate.of(2001, 1, 1));
        String user1Json = objectWriter.writeValueAsString(user1);
        mockMvc.perform(post("/users").content(user1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("email2@mail.ru");
        user2.setLogin("Login2");
        user2.setName(" ");
        user2.setBirthday(LocalDate.of(2002, 2, 2));
        String user2Json = objectWriter.writeValueAsString(user2);
        mockMvc.perform(post("/users").content(user2Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        user1.setName("Login1");
        user2.setName("Login2");

        List<User> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);

        MvcResult result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = result.getResponse().getContentAsString();
        List<User> responseUserList = objectMapper.readValue(responseJson, new TypeReference<>() {
        });

        assertIterableEquals(userList, responseUserList);
    }

    @Test
    void createFutureBirthdayUserShouldBeFail() throws Exception {
        //Проверка невозможности добавления пользователя с днем рождения "в будущем"
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("email1@mail.ru");
        user1.setLogin("Login1");
        user1.setName("Name1");
        user1.setBirthday(LocalDate.of(2111, 1, 1));
        String user1Json = objectWriter.writeValueAsString(user1);
        mockMvc.perform(post("/users").content(user1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        MvcResult result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = result.getResponse().getContentAsString();
        List<User> responseUserList = objectMapper.readValue(responseJson, new TypeReference<>() {
        });

        assertIterableEquals(new ArrayList<>(), responseUserList);
    }

    @Test
    void createInvalidEmailUserShouldBeFail() throws Exception {
        //Проверка невозможности добавления пользователя с некорректным email
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("@mail.ru");
        user1.setLogin("Login1");
        user1.setName("Name1");
        user1.setBirthday(LocalDate.of(2001, 1, 1));
        String user1Json = objectWriter.writeValueAsString(user1);
        mockMvc.perform(post("/users").content(user1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        user1.setEmail("asdmail.ru");
        user1Json = objectWriter.writeValueAsString(user1);
        mockMvc.perform(post("/users").content(user1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        user1.setEmail("@asdmailru");
        user1Json = objectWriter.writeValueAsString(user1);
        mockMvc.perform(post("/users").content(user1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        user1.setEmail("sdasd sdf@asdmail.ru");
        user1Json = objectWriter.writeValueAsString(user1);
        mockMvc.perform(post("/users").content(user1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        MvcResult result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = result.getResponse().getContentAsString();
        List<User> responseUserList = objectMapper.readValue(responseJson, new TypeReference<>() {
        });

        assertIterableEquals(new ArrayList<>(), responseUserList);
    }

    @Test
    void createDuplicateEmailUserShouldBeFail() throws Exception {
        //Проверка невозможности добавления пользователя с занятым email
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@mail.ru");
        user1.setLogin("Login1");
        user1.setName("Name1");
        user1.setBirthday(LocalDate.of(2001, 1, 1));
        String user1Json = objectWriter.writeValueAsString(user1);
        mockMvc.perform(post("/users").content(user1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user1@mail.ru");
        user2.setLogin("Login2");
        user2.setName("Name2");
        user2.setBirthday(LocalDate.of(2002, 2, 2));
        String user2Json = objectWriter.writeValueAsString(user2);
        mockMvc.perform(post("/users").content(user2Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());


        List<User> userList = new ArrayList<>();
        userList.add(user1);

        MvcResult result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = result.getResponse().getContentAsString();
        List<User> responseUserList = objectMapper.readValue(responseJson, new TypeReference<>() {
        });

        assertIterableEquals(userList, responseUserList);
    }

    @Test
    void updateUserShouldBeOk() throws Exception {
        //Проверка возможности обновления пользователя валидным пользователем
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@mail.ru");
        user1.setLogin("Login1");
        user1.setName("Name1");
        user1.setBirthday(LocalDate.of(2001, 1, 1));
        String user1Json = objectWriter.writeValueAsString(user1);
        mockMvc.perform(post("/users").content(user1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        User user2 = new User();
        user2.setId(1L);
        user2.setEmail("user2@mail.ru");
        user2.setLogin("Login2");
        user2.setName("Name2");
        user2.setBirthday(LocalDate.of(2002, 2, 2));
        String user2Json = objectWriter.writeValueAsString(user2);
        mockMvc.perform(put("/users").content(user2Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


        List<User> userList = new ArrayList<>();
        userList.add(user2);

        MvcResult result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = result.getResponse().getContentAsString();
        List<User> responseUserList = objectMapper.readValue(responseJson, new TypeReference<>() {
        });

        assertIterableEquals(userList, responseUserList);
    }

    @Test
    void updateUnknownIdUserShouldBeFail() throws Exception {
        //Проверка невозможности обновления пользователя с несуществующим id
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@mail.ru");
        user1.setLogin("Login1");
        user1.setName("Name1");
        user1.setBirthday(LocalDate.of(2001, 1, 1));
        String user1Json = objectWriter.writeValueAsString(user1);
        mockMvc.perform(post("/users").content(user1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        User user2 = new User();
        user2.setId(999L);
        user2.setEmail("user2@mail.ru");
        user2.setLogin("Login2");
        user2.setName("Name2");
        user2.setBirthday(LocalDate.of(2002, 2, 2));
        String user2Json = objectWriter.writeValueAsString(user2);
        mockMvc.perform(put("/users").content(user2Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        user2.setId(null);
        user2Json = objectWriter.writeValueAsString(user2);
        mockMvc.perform(put("/users").content(user2Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        List<User> userList = new ArrayList<>();
        userList.add(user1);

        MvcResult result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = result.getResponse().getContentAsString();
        List<User> responseUserList = objectMapper.readValue(responseJson, new TypeReference<>() {
        });

        assertIterableEquals(userList, responseUserList);
    }

    @Test
    void updateInvalidLoginUserShouldBeFail() throws Exception {
        //Проверка невозможности обновления некорректным логином пользователя
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@mail.ru");
        user1.setLogin("Login1");
        user1.setName("Name1");
        user1.setBirthday(LocalDate.of(2001, 1, 1));
        String user1Json = objectWriter.writeValueAsString(user1);
        mockMvc.perform(post("/users").content(user1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        User user2 = new User();
        user2.setId(1L);
        user2.setEmail("user2@mail.ru");
        user2.setLogin(null);
        user2.setName("Name2");
        user2.setBirthday(LocalDate.of(2002, 2, 2));
        String user2Json = objectWriter.writeValueAsString(user2);
        mockMvc.perform(put("/users").content(user2Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        user2.setLogin(" ");
        user2Json = objectWriter.writeValueAsString(user2);
        mockMvc.perform(put("/users").content(user2Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        user2.setLogin("a b c");
        user2Json = objectWriter.writeValueAsString(user2);
        mockMvc.perform(put("/users").content(user2Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        List<User> userList = new ArrayList<>();
        userList.add(user1);

        MvcResult result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = result.getResponse().getContentAsString();
        List<User> responseUserList = objectMapper.readValue(responseJson, new TypeReference<>() {
        });

        assertIterableEquals(userList, responseUserList);
    }

    @Test
    void updateEmptyNameUserShouldBeIgnore() throws Exception {
        //Проверка, что попытка обновления имени пользователя на пустое игнорируется
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@mail.ru");
        user1.setLogin("Login1");
        user1.setName("Name1");
        user1.setBirthday(LocalDate.of(2001, 1, 1));
        String user1Json = objectWriter.writeValueAsString(user1);
        mockMvc.perform(post("/users").content(user1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        user1.setName(" ");
        user1Json = objectWriter.writeValueAsString(user1);
        mockMvc.perform(put("/users").content(user1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        List<User> userList = new ArrayList<>();
        user1.setName("Login1");
        userList.add(user1);

        MvcResult result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = result.getResponse().getContentAsString();
        List<User> responseUserList = objectMapper.readValue(responseJson, new TypeReference<>() {
        });

        assertIterableEquals(userList, responseUserList);

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@mail.ru");
        user2.setLogin("Login2");
        user2.setName(" ");
        user2.setBirthday(LocalDate.of(2002, 2, 2));
        String user2Json = objectWriter.writeValueAsString(user2);
        mockMvc.perform(post("/users").content(user2Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        user2.setName("Login2");
        userList.add(user2);

        result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn();
        responseJson = result.getResponse().getContentAsString();
        responseUserList = objectMapper.readValue(responseJson, new TypeReference<>() {
        });

        assertIterableEquals(userList, responseUserList);
    }

    @Test
    void updateFutureBirthdayUserShouldBeFail() throws Exception {
        //Проверка невозможности обновления дня рождения "из будущего" пользователя
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@mail.ru");
        user1.setLogin("Login1");
        user1.setName("Name1");
        user1.setBirthday(LocalDate.of(2001, 1, 1));
        String user1Json = objectWriter.writeValueAsString(user1);
        mockMvc.perform(post("/users").content(user1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        User user2 = new User();
        user2.setId(1L);
        user2.setEmail("user2@mail.ru");
        user2.setLogin("Login2");
        user2.setName("Name2");
        user2.setBirthday(LocalDate.of(2222, 2, 2));
        String user2Json = objectWriter.writeValueAsString(user2);
        mockMvc.perform(put("/users").content(user2Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        List<User> userList = new ArrayList<>();
        userList.add(user1);

        MvcResult result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = result.getResponse().getContentAsString();
        List<User> responseUserList = objectMapper.readValue(responseJson, new TypeReference<>() {
        });

        assertIterableEquals(userList, responseUserList);
    }

    @Test
    void updateInvalidEmailUserShouldBeFail() throws Exception {
        //Проверка невозможности обновления email пользователя на некорректный
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@mail.ru");
        user1.setLogin("Login1");
        user1.setName("Name1");
        user1.setBirthday(LocalDate.of(2001, 1, 1));
        String user1Json = objectWriter.writeValueAsString(user1);
        mockMvc.perform(post("/users").content(user1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        user1.setEmail("@asdmail.ru");
        user1Json = objectWriter.writeValueAsString(user1);
        mockMvc.perform(put("/users").content(user1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        user1.setEmail("asdmail.ru");
        user1Json = objectWriter.writeValueAsString(user1);
        mockMvc.perform(put("/users").content(user1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        user1.setEmail("@asdmailru");
        user1Json = objectWriter.writeValueAsString(user1);
        mockMvc.perform(put("/users").content(user1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        user1.setEmail("sdasd sdf@asdmail.ru");
        user1Json = objectWriter.writeValueAsString(user1);
        mockMvc.perform(put("/users").content(user1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        List<User> userList = new ArrayList<>();
        user1.setEmail("user1@mail.ru");
        userList.add(user1);

        MvcResult result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = result.getResponse().getContentAsString();
        List<User> responseUserList = objectMapper.readValue(responseJson, new TypeReference<>() {
        });

        assertIterableEquals(userList, responseUserList);
    }

    @Test
    void updateDuplicateEmailUserShouldBeFail() throws Exception {
        //Проверка невозможности обновления email пользователя на занятый
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@mail.ru");
        user1.setLogin("Login1");
        user1.setName("Name1");
        user1.setBirthday(LocalDate.of(2001, 1, 1));
        String user1Json = objectWriter.writeValueAsString(user1);
        mockMvc.perform(post("/users").content(user1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@mail.ru");
        user2.setLogin("Login2");
        user2.setName("Name2");
        user2.setBirthday(LocalDate.of(2002, 2, 2));
        String user2Json = objectWriter.writeValueAsString(user2);
        mockMvc.perform(post("/users").content(user2Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        user2.setEmail("user1@mail.ru");
        user2Json = objectWriter.writeValueAsString(user2);
        mockMvc.perform(put("/users").content(user2Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        List<User> userList = new ArrayList<>();
        userList.add(user1);
        user2.setEmail("user2@mail.ru");
        userList.add(user2);

        MvcResult result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = result.getResponse().getContentAsString();
        List<User> responseUserList = objectMapper.readValue(responseJson, new TypeReference<>() {
        });

        assertIterableEquals(userList, responseUserList);
    }

    @Test
    void updateYourDuplicateEmailUserShouldBeOk() throws Exception {
        //Проверка, что в случае передачи собственного email пользователя его обновление происходит без ошибки
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@mail.ru");
        user1.setLogin("Login1");
        user1.setName("Name1");
        user1.setBirthday(LocalDate.of(2001, 1, 1));
        String user1Json = objectWriter.writeValueAsString(user1);
        mockMvc.perform(post("/users").content(user1Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        User user2 = new User();
        user2.setId(1L);
        user2.setEmail("user1@mail.ru");
        user2.setLogin("Login2");
        user2.setName("Name2");
        user2.setBirthday(LocalDate.of(2002, 2, 2));
        String user2Json = objectWriter.writeValueAsString(user2);
        mockMvc.perform(put("/users").content(user2Json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        List<User> userList = new ArrayList<>();
        userList.add(user2);

        MvcResult result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = result.getResponse().getContentAsString();
        List<User> responseUserList = objectMapper.readValue(responseJson, new TypeReference<>() {
        });

        assertIterableEquals(userList, responseUserList);
    }
}
