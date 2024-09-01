package ronin.ua.balance.processor.dto.users;

import java.util.UUID;

public record UserDto(
        UUID userId,
        String userName,
        double userBalance) {
}

