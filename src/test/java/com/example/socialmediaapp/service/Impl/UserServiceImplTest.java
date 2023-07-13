package com.example.socialmediaapp.service.Impl;

import com.example.socialmediaapp.dto.UserProfileDto;
import com.example.socialmediaapp.entities.User;
import com.example.socialmediaapp.exception.CustomException;
import com.example.socialmediaapp.exception.NotFound;
import com.example.socialmediaapp.mapstruct.MapStructMapper;
import com.example.socialmediaapp.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("User service tests")
class UserServiceImplTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private MapStructMapper mapStructMapper;

    @Autowired
    private UserServiceImpl userService;

    private User user;
    private User userNullFirstName;

    static List<UserProfileDto> userProfileDtoList;
    static List<User> userList;

    @BeforeAll
    static void beforeAll() {
        userProfileDtoList = new ArrayList<>();
        userProfileDtoList.add(new UserProfileDto("likita", "rai", "developer", LocalDate.of(2001, 11, 1), "F"));
        userProfileDtoList.add(new UserProfileDto("dhruvi", "patel", "developer", LocalDate.of(2001, 11, 1), "F"));

        userList = new ArrayList<>();
        userList.add(new User(1, "likita", "rai", null));
        userList.add(new User(2, "dhruvi", "patel", null));
    }

    @AfterAll
    static void afterAll() {
        userProfileDtoList.clear();
        userList.clear();
    }

    @BeforeEach
    void setUp() {
        user = new User(1, "likita", "rai", null);
        userNullFirstName = new User(2, null, "rai", null);
    }

    @AfterEach
    void tearDown() {
        user = null;
        userNullFirstName = null;
    }

    @Nested
    @DisplayName("Add user into database")
    class AddUserTests{
        @Test
        @DisplayName("User added successfully")
        void testAddUser_WhenUserValid(){
            when(userRepository.save(user)).thenReturn(user);
            User savedUser = userService.addUser(user);
            assertEquals(user, savedUser);
            verify(userRepository, times(1)).save(user);
        }

        @Test
        @DisplayName("Custom exception occurs")
        void testAddUser_WithNullFirstName(){
            assertNull(userNullFirstName.getFirstName());
            assertThrows(CustomException.class, () -> userService.addUser(userNullFirstName));
        }

    }

    @Nested
    @DisplayName("Get all users information from database")
    class GetAllUsersTests{
        @Test
        @DisplayName("Get information when user exists")
        void testGetAllUsers_WhenUsersExist(){
            when(userRepository.findAll()).thenReturn(userList);
            when(mapStructMapper.userToUserProfileDto(userList.get(0))).thenReturn(userProfileDtoList.get(0));
            when(mapStructMapper.userToUserProfileDto(userList.get(1))).thenReturn(userProfileDtoList.get(1));
            assertEquals(userProfileDtoList, userService.getAllUsers());
            verify(userRepository, times(2)).findAll();
        }

        @Test
        @DisplayName("Get information when no user exists")
        void testGetAllUsers_WhenNoUsersExist(){
            when(userRepository.findAll()).thenReturn(null);
            assertEquals(new ArrayList<UserProfileDto>(), userService.getAllUsers());
            verify(userRepository).findAll();
        }
    }

    @Nested
    @DisplayName("Edit user in database")
    class EditUserTests{
        @Test
        @DisplayName("User doesn't exist")
        void testEditUser_WhenUserDoesNotExist(){
            assertThrows(NotFound.class, () -> userService.updateUser(user), "User not found");
        }

        @Test
        @DisplayName("User updated successfully")
        void testEditUser_WhenUserExists(){
            when(userRepository.findById(user.getUserId())).thenReturn(Optional.ofNullable(user));
            when(userRepository.updateUser(user.getFirstName(), user.getUserId())).thenReturn(0).thenReturn(1);
            assertEquals(0, userService.updateUser(user));
            assertEquals(1, userService.updateUser(user));
        }
    }

    @Nested
    @DisplayName("Delete user from database")
    class DeleteUserTests{
        @Test
        @DisplayName("User doesn't exist")
        void testDeleteUser_WhenUserDoesNotExist(){
            assertThrows(NotFound.class, () -> userService.deleteUser(user.getUserId()), "User not found");
        }

        @Test
        @DisplayName("User deleted successfully")
        void testDeleteUser_WhenUserExists(){
            when(userRepository.findById(user.getUserId())).thenReturn(Optional.ofNullable(user));
            assertEquals("Success", userService.deleteUser(user.getUserId()));
        }
    }
}