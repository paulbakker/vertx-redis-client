/**
 * Copyright 2015 Red Hat, Inc.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */
package io.vertx.test.redis;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class EvalTest extends AbstractRedisClientBase {

  @Test
  public void test1() {
    redis.eval("return {KEYS[1],KEYS[2],ARGV[1],ARGV[2]}", Arrays.asList("key1", "key2"), Arrays.asList("first", "second"), res -> {
      assertTrue(res.succeeded());
      // expect [key1, key2, first, second]
      testComplete();
    });
    await();
  }

  @Test
  public void test2() {
    redis.eval("return redis.call('set',KEYS[1],'bar')", Arrays.asList("foo"), null, res -> {
      assertTrue(res.succeeded());
      // expect OK
      testComplete();
    });
    await();
  }

  @Test
  public void test3() {
    redis.eval("return 10", null, null, res -> {
      assertTrue(res.succeeded());
      // expect 10
      testComplete();
    });
    await();
  }

  @Test
  public void test4() {
    redis.eval("return {1,2,3.3333,'foo',nil,'bar'}", null, null, res -> {
      assertTrue(res.succeeded());
      // expect [1, 2, 3, 'foo']
      testComplete();
    });
    await();
  }

  @Test
  public void test5() {
    redis.set("foo", "bar", res -> {
      assertTrue(res.succeeded());

      redis.eval("return redis.call('get','foo')", null, null, res1 -> {
        assertTrue(res1.succeeded());
        // expect "bar"

        redis.evalsha("6b1bf486c81ceb7edf3c093f4c48582e38c0e791", null, null, res2 -> {
          assertTrue(res1.succeeded());
          // expect "bar"
          testComplete();
        });
      });
    });
    await();
  }
}
