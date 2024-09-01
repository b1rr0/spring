package ronin.ua.balance.processor.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import ronin.ua.balance.processor.DefaultTest;
import ronin.ua.balance.processor.dto.users.CountDto;
import ronin.ua.balance.processor.dto.users.IdsDto;
import ronin.ua.balance.processor.dto.users.UserDto;
import ronin.ua.balance.processor.dto.users.UsersDto;

import java.util.*;

import static java.lang.Thread.sleep;

public class UserTest extends DefaultTest {

    public static final String USERS_PATH = "/users";

    @Test
    void testChangeBalanceV1(){
        testV1V2("/balance/v1");
    }

    @Test
    void testChangeBalanceV2() {
        testV1V2("/balance/v2");
    }

    private void testV1V2(String url) {
        var response = restTemplate.postForEntity(USERS_PATH + "/fake", new CountDto(10_000), IdsDto.class);
        Map<UUID, Double> map = new HashMap<>();
        double i = 0;
        var ids = new HashSet<>();

        for (UUID id : Objects.requireNonNull(response.getBody()).ids()) {
            ids.add(id);
            map.put(id, i++);
        }

        var userResp = restTemplate.exchange(USERS_PATH + url, HttpMethod.PUT, new HttpEntity<>(map), UsersDto.class);

        Assertions.assertEquals(response.getBody().ids().size(), userResp.getBody().users().size());

        for (UserDto user : userResp.getBody().users()) {
            var b = map.get(user.userId());
            if (b == user.userBalance()) {
                ids.remove(user.userId());
            }
        }
        Assertions.assertEquals(ids.size(), 0);
    }

    @Test
    void testChangeBalanceV3() {
        testV3V4("/balance/v3");
    }

    @Test
    void testChangeBalanceV4() {
        testV3V4("/balance/v4");
    }

    void testV3V4(String url) {
        var response = restTemplate.postForEntity(USERS_PATH + "/fake", new CountDto(10_000), IdsDto.class);
        Map<UUID, Double> map = new HashMap<>();
        double i = 0;
        var ids = new HashSet<>();

        for (UUID id : Objects.requireNonNull(response.getBody()).ids()) {
            ids.add(id);
            map.put(id, i++);
        }

        restTemplate.put(USERS_PATH + url, map);

        var userResp = restTemplate.exchange(USERS_PATH + "/list", HttpMethod.POST,
                new HttpEntity<>(new IdsDto(response.getBody().ids())), UsersDto.class);

        try {
            sleep(5000);//random
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(response.getBody().ids().size(),
                Objects.requireNonNull(userResp.getBody()).users().size());

        for (UserDto user : userResp.getBody().users()) {
            var b = map.get(user.userId());
            if (b == user.userBalance()) {
                ids.remove(user.userId());
            }
        }
        Assertions.assertEquals(ids.size(), 0);
    }
}
