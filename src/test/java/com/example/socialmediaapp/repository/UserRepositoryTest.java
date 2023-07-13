package com.example.socialmediaapp.repository;

import com.example.socialmediaapp.SocialMediaAppApplication;
import com.example.socialmediaapp.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = {SocialMediaAppApplication.class})
@Slf4j
@DisplayName("User repository tests")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    static private User user;
    static private User user1;

    static private List<User> userList;

    @BeforeAll
    static void beforeAll() {
        user = User.builder()
                .userId(1)
                .firstName("likita")
                .lastName("rai").build();
        user1 = User.builder()
                .userId(2)
                .firstName("dhruvi")
                .lastName("patel").build();
        userList = new ArrayList<>();
        userList.add(user);
        userList.add(user1);
    }

    @BeforeEach
    void setUp() {
        userRepository.save(user);
        userRepository.save(user1);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteById(user.getUserId());
    }

    @AfterAll
    static void afterAll() {
        user = null;
        userList = null;
    }

    @Nested
    @DisplayName("Save user")
    class saveUserTest{
        @Test
        @DisplayName("Save users success")
        void saveUserTest_Success() {
            User savedUser = userRepository.save(user);
            log.info("Actual user: " + savedUser);
            log.info("Expected user: " + user);
            assertEquals(user, savedUser);
        }

        @Test
        @DisplayName("Save users failure")
        void saveUserTest_Fail() {
            assertThrows(NullPointerException.class, () -> userRepository.save(new User(1, "likita", null, null)));
        }
    }


    @Nested
    @DisplayName("Find all users")
    class findAllUsersTest{
        @Test
        @DisplayName("Find all users success")
        void findAllUsersTest_Success(){
            List<User> list = userRepository.findAll();
            log.info("Actual userList: " + list);
            log.info("Expected userList: " + userList);
            assertEquals(userList, list);
        }

        @Test
        @DisplayName("Find all users failure")
        void findAllUsersTest_Fail(){
            userRepository.deleteById(1);
            userRepository.deleteById(2);
            List<User> list = userRepository.findAll();
            log.info("Actual userList: " + list);
            log.info("Expected userList: " + new ArrayList<User>());
            assertEquals(new ArrayList<User>(), list);
        }
    }


    @Nested
    @DisplayName("Find user by ID")
    class findUserByIdTest{
        @Test
        @DisplayName("Find user by ID success")
        void findUserByIdTest_Success(){
            User fetchedUser = userRepository.findById(user.getUserId()).orElse(null);
            log.info("Actual user: " + fetchedUser);
            log.info("Expected user: " + user);
            assertEquals(user, fetchedUser);
        }

        @Test
        @DisplayName("Find user by ID failure")
        void findUserByIdTest_Fail(){
            User fetchedUser = userRepository.findById(10).orElse(null);
            log.info("Actual user: " + fetchedUser);
            log.info("Expected user: " + null);
            assertNull(fetchedUser);
        }
    }


    @Nested
    @DisplayName("Update user")
    class updateUserTest{
        @Test
        @DisplayName("Update user success")
        void updateUserTest_Success(){
            int status = userRepository.updateUser(user.getFirstName(), user.getUserId());
            log.info("Status : " + status);
            assertEquals(1, status);
        }

        @Test
        @DisplayName("Update user failure")
        void updateUserTest_Fail(){
            int status = userRepository.updateUser(user.getFirstName(), 10);
            log.info("Status : " + status);
            assertEquals(0, status);
        }
    }


    @Nested
    @DisplayName("Delete user")
    class deleteUserTest{
        @Test
        @DisplayName("Delete user success")
        void deleteUserTest_Success(){
            userRepository.deleteById(user.getUserId());
            User fetchedUser = userRepository.findById(user.getUserId()).orElse(null);
            assertNull(fetchedUser);
        }

        @Test
        @DisplayName("Delete user failure")
        void deleteUserTest_Fail(){
            userRepository.deleteById(10);
            User fetchedUser = userRepository.findById(10).orElse(null);
            assertNull(fetchedUser);
        }
    }
}