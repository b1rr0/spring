package ronin.ua.balance.processor.user;


import ronin.ua.balance.processor.entites.UserEntity
import ronin.ua.balance.processor.repository.UserRepository
import ronin.ua.balance.processor.service.user.UserService
import ronin.ua.balance.processor.service.util.ThreadExecutor

import spock.lang.Specification
import java.util.concurrent.Callable

class UserServiceTest extends Specification {

    def userRepository = Mock(UserRepository)
    def threadExecutor = Mock(ThreadExecutor)
    def userService = new UserService(userRepository, threadExecutor)

    def "createUser should save and return a UserEntity"() {
        given:
        String userName = "TestUser"
        UserEntity expectedUser = new UserEntity(name: userName, balance: 0)

        when:
        def result = userService.createUser(userName)

        then:
        1 * userRepository.save(_ as UserEntity) >> expectedUser
        result.name == userName
        result.balance == 0
    }

    def "generateRandomUsers should return a list of UUIDs"() {
        given:
        int count = 5
        def uuids = (1..count).collect { UUID.randomUUID() }

        when:
        def result = userService.generateRandomUsers(count)

        then:
        1 * userRepository.insertRandomUsers(count, 0d) >> uuids
        result.size() == count
        result == uuids
    }

    def "getUser should return the correct UserEntity by ID"() {
        given:
        UUID userId = UUID.randomUUID()
        UserEntity user = new UserEntity(id: userId, name: "TestUser", balance: 100)

        when:
        def result = userService.getUser(userId)

        then:
        1 * userRepository.findById(userId) >> Optional.of(user)
        result == user
    }

    def "getAllUsers should return all users"() {
        given:
        List<UserEntity> users = [
                new UserEntity(id: UUID.randomUUID(), name: "User1", balance: 100),
                new UserEntity(id: UUID.randomUUID(), name: "User2", balance: 200)
        ]

        when:
        def result = userService.getAllUsers()

        then:
        1 * userRepository.findAll() >> users
        result == users
    }

    def "updateBalancesV2 should update user balances asynchronously and return processed users"() {
        given:
        Map<UUID, Double> balanceMap = [(UUID.randomUUID()): 50.0d, (UUID.randomUUID()): 75.0d]
        List<UserEntity> updatedUsers = [
                new UserEntity(id: balanceMap.keySet().first(), balance: 50d),
                new UserEntity(id: balanceMap.keySet().last(), balance: 75d)
        ]

        when:
        def result = userService.updateBalancesV2(balanceMap)

        then:
        1 * threadExecutor.asyncRunAndWaitResult(_ as List<Callable<List<UserEntity>>>) >> [updatedUsers]
        result == updatedUsers
    }

    def "updateBalancesV3 should update balances using asyncRun"() {
        given:
        UUID id1 = UUID.randomUUID()
        Double d1=100d;

        when:
        userService.updateBalancesV3( [(id1): d1])

        then:
        1 * threadExecutor.asyncRun(_ as List<Runnable>)
    }

    def "updateBalancesV4 should split the map and update balances using asyncRun"() {
        given:
        Map<UUID, Double> balanceMap = [(UUID.randomUUID()): 100.0d, (UUID.randomUUID()): 200.0d]

        when:
        userService.updateBalancesV4(balanceMap)

        then:
        1 * threadExecutor.asyncRun(_ as List<Runnable>)
    }

    def "splitIntoListOfMaps should correctly split the map into sublists"() {
        given:
        UUID id1 = UUID.randomUUID()
        UUID id2 = UUID.randomUUID()
        UUID id3 = UUID.randomUUID()
        UUID id4 = UUID.randomUUID()

        Map<UUID, Double> balanceMap = [
                (id1): 100.0d,
                (id2): 200.0d,
                (id3): 300.0d,
                (id4): 400.0d
        ]


        when:
        def result = userService.splitIntoListOfMaps(balanceMap)

        then:
        result.size() == 4
        result.every { it.size() == 1 } // Since there are 4 items and 2 threads, each sublist should contain 2 items
        result.flatten().collect { it.key } as Set == balanceMap.keySet() // Ensure all keys are present
    }
}
