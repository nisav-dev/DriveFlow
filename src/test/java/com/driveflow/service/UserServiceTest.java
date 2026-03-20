package com.driveflow.service;

import com.driveflow.entity.Customer;
import com.driveflow.entity.User;
import com.driveflow.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@DisplayName("בדיקות UserService")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ── TC-007: הרשמת משתמש ─────────────────────────────────────────────

    @Test
    @DisplayName("TC-007a: הרשמה תקינה — יוצרת User + Customer")
    void register_ValidData_ShouldCreateUserAndCustomer() {
        // When
        User user = userService.register(
                "newuser@driveflow.test", "Password1",
                "ישראל", "ישראלי", "050-1234567");

        // Then
        assertThat(user.getId()).isNotNull();
        assertThat(user.getEmail()).isEqualTo("newuser@driveflow.test");
        assertThat(user.getRole()).isEqualTo(UserRole.CUSTOMER);
        assertThat(user.isActive()).isTrue();

        // Customer record should exist
        Optional<Customer> customer = userService.findCustomerByUserId(user.getId());
        assertThat(customer).isPresent();
    }

    @Test
    @DisplayName("TC-007b: סיסמה מוצפנת ב-BCrypt")
    void register_PasswordIsEncoded() {
        // When
        User user = userService.register(
                "encode@driveflow.test", "PlainText123",
                "בדיקה", "בדיקה", null);

        // Then
        assertThat(user.getPassword()).doesNotContain("PlainText123");
        assertThat(passwordEncoder.matches("PlainText123", user.getPassword())).isTrue();
    }

    @Test
    @DisplayName("TC-007c: הרשמה עם מייל כפול — זורקת RuntimeException")
    void register_DuplicateEmail_ThrowsException() {
        // Given
        userService.register("dup@driveflow.test", "Password1", "א", "ב", null);

        // When/Then
        assertThatThrownBy(() ->
                userService.register("dup@driveflow.test", "OtherPassword", "ג", "ד", null))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("TC-007d: findByEmail — מוצא משתמש קיים")
    void findByEmail_ExistingUser_ReturnsUser() {
        // Given
        userService.register("find@driveflow.test", "Password1", "בדיקה", "בדיקה", null);

        // When
        Optional<User> result = userService.findByEmail("find@driveflow.test");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("find@driveflow.test");
    }

    @Test
    @DisplayName("TC-007e: findByEmail — מייל לא קיים מחזיר Optional.empty()")
    void findByEmail_NonExistingUser_ReturnsEmpty() {
        // When
        Optional<User> result = userService.findByEmail("doesnotexist@driveflow.test");

        // Then
        assertThat(result).isEmpty();
    }
}
