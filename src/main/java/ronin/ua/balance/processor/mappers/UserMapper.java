package ronin.ua.balance.processor.mappers;

import org.springframework.stereotype.Component;
import ronin.ua.balance.processor.dto.users.UserDto;
import ronin.ua.balance.processor.entites.UserEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class UserMapper extends Mapper<UserEntity> {

    private final Map<Class<?>, Function<UserEntity, ?>> map;

    public UserMapper() {
        this.map = new HashMap<>();
        map.put(UserDto.class, this::entityToDto);
    }

    public <T> T map(UserEntity userEntity, Class<T> clazz) {
        Function<UserEntity, ?> mapper = map.get(clazz);

        if (mapper != null) {
            return clazz.cast(mapper.apply(userEntity));
        }
        throw new IllegalArgumentException("No mapper found for class: " + clazz.getName());
    }

    private UserDto entityToDto(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }
        return new UserDto(userEntity.getId(), userEntity.getName(), userEntity.getBalance());
    }
}
