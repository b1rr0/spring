package ronin.ua.balance.processor.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ronin.ua.balance.processor.dto.users.UserDto;
import ronin.ua.balance.processor.dto.users.UsersDto;
import ronin.ua.balance.processor.entites.UserEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.StreamSupport;

@Component
public class UsersIterableMapper extends Mapper<Iterable<UserEntity>> {

    private final Map<Class<?>, Function<Iterable<UserEntity>, ?>> map;

    private final UserMapper userMapper;

    @Autowired
    public UsersIterableMapper(UserMapper userMapper) {
        this.userMapper = userMapper;

        this.map = new HashMap<>();
        map.put(UsersDto.class, this::iterEntitiesToUsersDto);
    }

    public <T> T map(Iterable<UserEntity> userEntity, Class<T> clazz) {
        Function<Iterable<UserEntity>, ?> mapper = map.get(clazz);
        if (mapper != null) {
            return clazz.cast(mapper.apply(userEntity));
        }
        throw new IllegalArgumentException("No mapper found for class: " + clazz.getName());
    }

    public UsersDto iterEntitiesToUsersDto(Iterable<UserEntity> iterable) {
        if (iterable == null) {
            return null;
        }
        List<UserDto> listOfUserDto =
                StreamSupport.stream(iterable.spliterator(), false)
                        .map(entity -> userMapper.map(entity, UserDto.class))
                        .toList();

        return new UsersDto(listOfUserDto);
    }
}
