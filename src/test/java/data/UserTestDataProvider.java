package data;

import dto.user.UserDto;
import java.util.List;
import java.util.stream.Stream;

public class UserTestDataProvider {

    public static Stream<List<UserDto>> provideValidUsers() {
        return Stream.of(
                List.of(
                        UserDto.builder().id(5L).username("user5").firstName("John").lastName("Doe").email("john.doe@email.com").password("pass123").phone("1234567890").userStatus(1).build(),
                        UserDto.builder().id(6L).username("user6").firstName("Jane").lastName("Smith").email("jane.smith@email.com").password("securePass").phone("9876543210").userStatus(1).build(),
                        UserDto.builder().id(7L).username("user7").firstName("Alice").lastName("Brown").email("alice.brown@email.com").password("strongPassword").phone("5557778888").userStatus(1).build()
                )
        );
    }

    public static Stream<String> provideInvalidUsernames() {
        return Stream.of(
                "nonexistentUser123",           // Имя, которого нет в системе
                "",                                     // Пустое имя
                "   ",                                  // Только пробелы
                "@invalid#",                            // Специальные символы
                (String) null                           // null-значение
        );
    }


    public static Stream<List<UserDto>> provideInvalidUsers() {
        return Stream.of(
                List.of(
                        UserDto.builder().id(8L).username("").firstName("John").lastName("Doe").email("invalid-email").password("pass123").phone("1234567890").userStatus(1).build(),  // Пустое username + некорректный email
                        UserDto.builder().id(9L).username(null).firstName("Jane").lastName("Smith").email("jane.smith@email.com").password("").phone("9876543210").userStatus(1).build(),  // null username + пустой пароль
                        UserDto.builder().id(10L).username("12345").firstName("Alice").lastName("Brown").email("alice.brown@email.com").password("strongPassword").phone("5557778888").userStatus(1).build()  // Числовое username
                )
        );
    }

    public static Stream<List<UserDto>> provideDuplicateUsers() {
        return Stream.of(
                List.of(
                        UserDto.builder().id(5L).username("user5").firstName("John").lastName("Doe").email("john.doe@email.com").password("pass123").phone("1234567890").userStatus(1).build()  // `user5` уже есть
                )
        );
    }
}
