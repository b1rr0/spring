package ronin.ua.balance.processor.util

import spock.lang.Specification
import ronin.ua.balance.processor.service.util.ThreadExecutor

import java.util.concurrent.Callable

class ThreadExecutorTest extends Specification {

    def "should execute tasks and return results"() {
        given:
        def executor = new ThreadExecutor()
        def callable1 = Stub(Callable) {
            call() >> "result1"
        }
        def callable2 = Stub(Callable) {
            call() >> "result2"
        }
        def tasks = [callable1, callable2]

        when:
        def results = executor.asyncRunAndWaitResult(tasks)

        then:
        results == ["result1", "result2"]
    }

    def "should execute runnable tasks"() {
        given:
        def executor = new ThreadExecutor()
        def runnable1 = Spy(Runnable) {
            run() >> { println("Runnable 1 executed") }
        }
        def runnable2 = Spy(Runnable) {
            run() >> { println("Runnable 2 executed") }
        }
        def tasks = [runnable1, runnable2]

        when:
        executor.asyncRun(tasks)
        sleep(10)

        then:
        1 * runnable1.run()
        1 * runnable2.run()
    }

    def "should handle exceptions during task execution"() {
        given:
        def executor = new ThreadExecutor()
        def callable = Stub(Callable) {
            call() >> { throw new RuntimeException("Task failed") }
        }
        def tasks = [callable]

        when:
        def result = executor.asyncRunAndWaitResult(tasks)

        then:
        thrown(RuntimeException)
    }
}
