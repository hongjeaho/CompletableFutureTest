package com.example.demo.product;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final Executor executor = Executors.newFixedThreadPool(10);
  private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
  /**
   * newFixedThreadPool
   * 처리할 작업이 등록되면 그에 따라 실제 작업할 스레드를 하나씩 생성한다.
   * 생항할 수 있는 쓰레드의 최대 개수는 제한되어 있으며 제한된 개수까지 생성 후 쓰레드를 유지한다.
   *
   * newCachedThreadPool
   * 현재 가지고 있는 쓰레드의 수가 처리할 작업의 수보다 많아서 시는 쓰레드가 많이 발생할 때
   * 쉬는 쓰레드를 종료시켜 훨씬 ㅇ연하게 대응할 수 있다.
   * 처리할 작업의 수가 많아지면 그 만큼 쓰레드를 생성한다. 반면 스레드의 수에는 제한을 두지 않는다.
   *
   * newSingleThreadExecutor
   * 단일 쓰레드로 동작하는 Executor로서 작더을 처리하는 쓰레드가 하나 뿐이다.
   *
   * newScheduledthreadPool
   * 일정 시간 이후에 실행하거나 주기적으로 작업을 실핼할 수 있으며 쓰레드의 수가 고정되어 있는 형태의
   * Executor.Timer클래스의 기능과 유사하다.
   *
   */


  /**
   * 동기방식으로 가격 호출
   * @param name
   * @return
   */
  public int getPrice(final String name) {
    log.info("동기방식으로 호출 (가격 조회)");
    return productRepository.getPriceByName(name);
  }

  /**
   * 비동기 방식으로 가격 호출 (공통 쓰래드 사)
   * @param name
   * @return
   */
  public CompletableFuture<Integer> getPriceAsync(final String name) {
    log.info("비동기방식으로 호출 (가격 조회)");

    CompletableFuture<Integer> future = new CompletableFuture<>();

    new Thread(() -> {
      log.info("새로운 쓰래드 시작");
      future.complete(productRepository.getPriceByName(name));
    }).start();

    return future;
  }

  /**
   * 비동기 방식으로 가격 호출 (executor 사용)
   * @param name
   * @return
   */
  public CompletableFuture<Integer> getPriceSupplyAsyncCommonThread(final String name) {
    log.info("비동기방식으로 호출 (가격 조회)");

    return CompletableFuture.supplyAsync(() -> {
      log.info("Supply Async Thread");
      return productRepository.getPriceByName(name);
    }, executor);
  }

  /**
   * 비동기 방식으로 가격 호출 (ThreadPoolTask 사용)
   * @param name
   * @return
   */
  public CompletableFuture<Integer> getPriceSupplyAsync(final String name) {
    log.info("비동기방식으로 호출 (가격 조회)");

    return CompletableFuture.supplyAsync(() -> {
      log.info("Supply Async Thread");
      return productRepository.getPriceByName(name);
    }, threadPoolTaskExecutor);
  }
}
