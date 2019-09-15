package com.zdk.async.callable;


import org.omg.CORBA.PUBLIC_MEMBER;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

/**
 * 异步mvc 开启一个副线程 提高吞吐（而不是只依赖于容器的多线程），对客户端来说看不出什么区别
 */


@RequestMapping("/test")
@RestController
public class CallableTest {
    org.slf4j.Logger logger = LoggerFactory.getLogger(CallableTest.class);

    /**
     *
     * 返回Callable对象时，实际工作线程会在后台处理，Controller无需等待工作线程处理完成，但Spring会在工作线程处理完毕后才返回客户端。
     * 它的执行流程是这样的：　
     *  客户端请求服务
     * SpringMVC调用Controller，Controller返回一个Callback对象
     * SpringMVC调用ruquest.startAsync并且将Callback提交到TaskExecutor中去执行
     * DispatcherServlet以及Filters等从应用服务器线程中结束，但Response仍旧是打开状态，也就是说暂时还不返回给客户端
     * TaskExecutor调用Callback返回一个结果，SpringMVC将请求发送给应用服务器继续处理
     * DispatcherServlet再次被调用并且继续处理Callback返回的对象，最终将其返回给客户端
     * ：缺点： 副线程在同一主线程中 ，无法满足复杂的应用场景
     *
     */

    @GetMapping("/callable")
    public Callable<String> testCallabe() {
        logger.info("controller 开始----------");
        Callable callable = () -> {
            Thread.sleep(3000L);
            logger.info("-----------实际结束");
            return "成功";
        };
        logger.info("Controller执行结束！");

        return callable;

    }


    /***
     * DeferredResult 应用于一个线程接受请求，其他线程处理（如mq），
     *  deferredResult.setResult 后，会把请求结果返回给客户端
     * @return
     */

    @GetMapping("e")
    public DeferredResult  creatOrder() {


       DeferredResult deferredResult = new DeferredResult(10000L,"超时");
       DeferedResultQueue.add(deferredResult);
       return  deferredResult;
   }

   @GetMapping("create")
   public String creat() {
       DeferredResult deferredResult =  DeferedResultQueue.get();
       //这个就时返回结果
       deferredResult.setResult("消费了一个订单"+UUID.randomUUID());
       return  "success:"+UUID.randomUUID();
   }


}
