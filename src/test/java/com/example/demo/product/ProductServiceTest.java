package com.example.demo.product;

import com.example.demo.config.ThreadConfig;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {
    ProductRepository.class,
    ProductService.class,
    ThreadConfig.class
})
class ProductServiceTest {

  @Autowired
  private ProductService productService;
  private final static Logger log = LoggerFactory.getLogger(ProductServiceTest.class);

  private final int loop = 5;
  private final String name = "ipone";
  private final int price = 1500000;

  @Test
  @DisplayName("동기방식으로 가격 조회 메소드 호출")
  public void syncMethodCall() {

    //when
    log.info("메소드 호출 전");
    int resultPrice = productService.getPrice(name);
    log.info("동기처리 확인 1");
    //then
    Assertions.assertThat(price).isEqualTo(resultPrice);
    log.info("동기처리 확인 2");
  }

  @Test
  @DisplayName("비동기방식으로 가격 조회 메소드 호출 (블록킹 + 논블록킹 혼합)")
  public void asyncMethodCall() throws ExecutionException, InterruptedException {
    //when
    log.info("메소드 호출 전");
    Future<Integer> future = productService.getPriceAsync(name); // Non-Blocking
    log.info("비동기 동기처리 확인 1");
    //then
    Assertions.assertThat(price).isEqualTo(future.get()); // future.get() Blocking
    log.info("비동기 동기처리 확인 2");
  }

  @Test
  @DisplayName("비동기방식으로 가격 조회 메소드 호출 (블록킹 + 논블록킹 혼합) : Refactoring")
  public void getPriceSupplyAsync() throws ExecutionException, InterruptedException {
    //when
    log.info("메소드 호출 전");
    Future<Integer> future = productService.getPriceSupplyAsync(name); // Non-Blocking
    log.info("비동기 동기처리 확인 1");
    //then
    Assertions.assertThat(price).isEqualTo(future.get()); // future.get() Blocking
    log.info("비동기 동기처리 확인 2");
  }

  @Test
  @DisplayName("Executor 사용 비동기 가격 호출")
  public void getPriceSupplyAsyncCommonThread() {
    log.info("메소드 호출 전");
    CompletableFuture<Void>  future =
        productService.getPriceSupplyAsyncCommonThread(name)
        .thenAccept(p -> {
          log.info("콜백 가격은 " + p + ", 데이터는 반환 하지 않음") ;
          Assertions.assertThat(price).isEqualTo(p);
        });

    /**
     * Non-Blocking 처리하므로 thenAccept 처리전 프로세스가 종료 되므로
     * future.join()을 사용하여 non-blocking 처리
     */
    Assertions.assertThat(future.join()).isNull();
  }

  @Test
  @DisplayName("비동기 방식으로 가격 호출 및 Non-Blocking (반환값없음)")
  public void nonBlockingNoResult() {
    CompletableFuture<Void> future =  productService.getPriceSupplyAsync(name)
        .thenAccept(p -> {
          log.info("콜백 가격은 " + p + ", 데이터는 반환 하지 않음") ;
          Assertions.assertThat(price).isEqualTo(p);
        });

    /**
     * Non-Blocking 처리하므로 thenAccept 처리전 프로세스가 종료 되므로
     * future.join()을 사용하여 non-blocking 처리
     */
    Assertions.assertThat(future.join()).isNull();
  }

  @Test
  @DisplayName("비동기 방식으로 가격 호출 및 Non-Blocking (반환값 + 반환값없음)")
  public void nonBlockingResultAndNoResult() {

    /**
     * 아래 thenApply, thenAccept 메서드는 같은 쓰레드에서 동작한다.
     * 다른 쓰레드를 사용 하고 싶다면 thenApplyAsync, thenAcceptAsync
     */

    CompletableFuture<Void> future =  productService.getPriceSupplyAsync(name)
        .thenApply(p -> {
          log.info("할인율은 10000원 입니다.") ;
          return p - 10000;
        })
        .thenAccept(p -> {
          log.info("콜백 가격은 " + p + ", 데이터는 반환 하지 않음") ;
          Assertions.assertThat(price - 10000).isEqualTo(p);
        });

    /**
     * Non-Blocking 처리하므로 thenAccept 처리전 프로세스가 종료 되므로
     * future.join()을 사용하여 non-blocking 처리
     */
    Assertions.assertThat(future.join()).isNull();
  }
}
