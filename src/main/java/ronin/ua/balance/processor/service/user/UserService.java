package ronin.ua.balance.processor.service.user;

import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ronin.ua.balance.processor.entites.UserEntity;
import ronin.ua.balance.processor.repository.UserRepository;
import ronin.ua.balance.processor.service.util.PairCreator;
import ronin.ua.balance.processor.service.util.ThreadExecutor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final ThreadExecutor threadExecutor;
    public final static int COUNT_OF_THREADS = 10;  // random number depended on PC

    @Autowired
    public UserService(UserRepository userRepository, ThreadExecutor threadExecutor) {
        this.userRepository = userRepository;
        this.threadExecutor = threadExecutor;
    }

    public UserEntity createUser(String name) {
        UserEntity userEntity = new UserEntity().setBalance(0d)// for example
                .setName(name);
        return userRepository.save(userEntity);
    }

    public List<UUID> generateRandomUsers(int count) {
        return userRepository.insertRandomUsers(count, 0d);
    }

    public UserEntity getUser(UUID userId) {
        return userRepository.findById(userId).orElseThrow();
    }

    public List<UserEntity> getUsers(List<UUID> ids) {
        return userRepository.findAllById(ids);
    }

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public List<UserEntity> updateBalancesV1(Map<UUID, Double> doubleMap) {
        Pair<UUID[], Double[]> pair = PairCreator.createPair(doubleMap);
        return userRepository.batchUpdateAndReturnProcessed(pair.a, pair.b);
    }


    public List<UserEntity> updateBalancesV2(Map<UUID, Double> balanceMap) {
        Collection<List<Map.Entry<UUID, Double>>> splitList = splitIntoListOfMaps(balanceMap);
        List<Callable<List<UserEntity>>> callables = splitList.stream()
                .map(entries -> {
                    var pair = PairCreator.createPair(entries);
                    return (Callable<List<UserEntity>>) () -> userRepository.batchUpdateAndReturnProcessed(pair.a, pair.b);
                })
                .collect(Collectors.toList());

        return threadExecutor.asyncRunAndWaitResult(callables).stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public void updateBalancesV3(Map<UUID, Double> doubleMap) {
        var pair = PairCreator.createPair(doubleMap);
        threadExecutor.asyncRun(List.of(() -> userRepository.batchUpdate(pair.a, pair.b)));
    }

    public void updateBalancesV4(Map<UUID, Double> balanceMap) {
        var splitList = splitIntoListOfMaps(balanceMap);
        List<Runnable> runnables = splitList.stream()
                .map(entries -> {
                    var pair = PairCreator.createPair(entries);
                    return (Runnable) () -> userRepository.batchUpdate(pair.a, pair.b);
                })
                .collect(Collectors.toList());

        threadExecutor.asyncRun(runnables);
    }

    private <T, E> Collection<List<Map.Entry<T, E>>> splitIntoListOfMaps(Map<T, E> doubleMap) {
        AtomicInteger i = new AtomicInteger();
        return doubleMap.entrySet().stream().collect(
                Collectors.groupingBy(it -> i.getAndIncrement() % COUNT_OF_THREADS)).values();
    }
}
