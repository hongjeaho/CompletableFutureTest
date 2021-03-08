package com.example.demo.product;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepository {

  private final Map<String, Integer> product = new HashMap<>();

  @PostConstruct
  public void init() {
    product.put("notebook", 250000);
    product.put("ipone", 1500000);
    product.put("pen", 10000);
  }

  public int getPriceByName(String name) {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    return product.get(name);
  }
}
