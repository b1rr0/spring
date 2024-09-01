package ronin.ua.balance.processor.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ronin.ua.balance.processor.dto.users.*;
import ronin.ua.balance.processor.mappers.UserMapper;
import ronin.ua.balance.processor.mappers.UsersIterableMapper;
import ronin.ua.balance.processor.service.user.UserService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@Tag(name = "User", description = "Operations related to user")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final UsersIterableMapper usersIterableMapper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper, UsersIterableMapper usersIterableMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.usersIterableMapper = usersIterableMapper;
    }

    @PostMapping
    public UserDto createUser(@RequestBody CreateUserDto userDto) {
        return userMapper.map(userService.createUser(userDto.name()));
    }

    @PostMapping("/fake")
    public IdsDto createFakeUsers(@RequestBody CountDto count) {
        return new IdsDto(userService.generateRandomUsers(count.count()));
    }

    @PutMapping("/balance/v1")
    public UsersDto updateBalancesV1(@RequestBody Map<UUID, Double> map) {
        return usersIterableMapper.map(userService.updateBalancesV1(map));
    }

    @PutMapping("/balance/v2")
    public UsersDto updateBalancesV2(@RequestBody Map<UUID, Double> map) {
        return usersIterableMapper.map(userService.updateBalancesV2(map));
    }

    @PutMapping("/balance/v3")
    public void updateBalancesV3(@RequestBody Map<UUID, Double> map) {
        userService.updateBalancesV3(map);
    }

    @PutMapping("/balance/v4")
    public void updateBalancesV4(@RequestBody Map<UUID, Double> map) {
        userService.updateBalancesV4(map);
    }

    @GetMapping("/{userId}")
    public UsersDto getUser(@PathVariable UUID userId) {
        return userMapper.map(userService.getUser(userId));
    }

    @GetMapping()
    public UsersDto getAllUsers() {
        return usersIterableMapper.map(userService.getAllUsers());
    }

    @PostMapping("/list")//BAD practice just for test
    public UsersDto getUserList(@RequestBody IdsDto idsDto) {
        return usersIterableMapper.map(userService.getUsers(idsDto.ids()));
    }
}
