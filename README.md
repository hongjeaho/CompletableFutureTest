# CompletableFuture를 이용한 비동기 테스트
자바 5부터 미래 시점에 결과를 얻을 수 있는 Future 인터페이스를 제공하고 있다.  
시간이 걸릴 수 있는 작업을 Future 내부로 작성하고 호출자 스레드가 결과를. 기다리는 동안 다른 유용한 작업을 할 수 있다.
Future의 get 메서드로 결과를 가져올 수 있는데 가져오는 시점에 완료가 되었으면 성공이지만 완료가 되지 않았다면 결과적으로 블로킹이 일어나게 된다.  

CompletableFuture는 Future와 CompletionStage를 구현한 클래스다.  
Future이지만 직접. 쓰레드를 생성하지 않고 async로 작업을 처리할 수 있고, 여러 CompletableFuture를 병렬로 처리하거나, 병합하여 처리할 수 있게 합니다. 
또한 Cancel, Error를 처리할 수 있는 방법을 제공합니다.

CompletableFuture를 사용해서 async, blocking, non-blocking 을 공부해보자. 

## ExecutorService
Executor Service는 스레드 풀과 Queue로 구성되어 있다.  
제출된 task들은 Queue에 들어가게 되고 순차적으로 스레드에 할당된다. 스레드가 만약 남아있지 않다면 Queue 안에서 대기하게 된다.  
스레드를 생성하는 것은 비용이 큰 작업이기 때문에 이를 최소화하기 위해 미리 스레드 풀 안에 스레드를 생성해 놓고 관리한다.  


다양한 executor서비스의 인스턴스를 생성하는 Factory 클래스

### newFixedThreadPool
처리할 작업이 등록되면 그에 따라 실제 작업할 스레드를 하나씩 생성한다.  
생항할 수 있는 쓰레드의 최대 개수는 제한되어 있으며 제한된 개수까지 생성 후 쓰레드를 유지한다.

### newCachedThreadPool
현재 가지고 있는 쓰레드의 수가 처리할 작업의 수보다 많아서 시는 쓰레드가 많이 발생할 때 쉬는 쓰레드를 종료시켜 훨씬 ㅇ연하게 대응할 수 있다.  
처리할 작업의 수가 많아지면 그 만큼 쓰레드를 생성한다. 반면 스레드의 수에는 제한을 두지 않는다.

### newSingleThreadExecutor
단일 쓰레드로 동작하는 Executor로서 작더을 처리하는 쓰레드가 하나 뿐이다.

### newScheduledthreadPool
일정 시간 이후에 실행하거나 주기적으로 작업을 실핼할 수 있으며 쓰레드의 수가 고정되어 있는 형태의 Executor.Timer클래스의 기능과 유사하다.

## ThreadPoolTaskExecutor
최상위 인터페이스이 Executor의 구현체로 스레드풀을 간편하게 사용하게 해주는 클래스이다.  

``` java
@Bean
public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
  ThreadPoolTaskExecutor task =  new ThreadPoolTaskExecutor();
  task.setCorePoolSize(10); //기본 쓰래드 수
  task.setMaxPoolSize(10);  //최대 쓰래드 수
  task.setQueueCapacity(100); //Queue size
  task.setThreadNamePrefix("sample-");
  task.initialize();
  return task;
}
```
위 코드에서 최초 corePoolSize 만큼 동작하다가 더 이상 처리할 수 없을 경우 maxPoolSize 만큼 스레드가 증가할 것이라고 예상할 수 있다.  
내부적으로는 Integer.MAX_VALUE 사이즈의 LinkedBlockingQueue를 생성해서 corePoolSize 만큼의 스레드에서 task를 처리할 수 없을 경우 queue에서 대기하게 된다. 
queue가 꽉 차게 되면 그때 maxPoolSize 만큼 스레드를 생성해서 처리하게 된다.

Integer.MAX_VALUE 만큼의 queue를 이용한다고 했는데 이게 너무 크다고 생각된다면 queueCapacity 를사용해 queue size를 변경할 수 있다.
