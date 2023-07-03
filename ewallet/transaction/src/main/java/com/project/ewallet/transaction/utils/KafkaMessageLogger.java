package com.project.ewallet.transaction.utils;

import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class KafkaMessageLogger {

    public static void addCallBack(String message ,  ListenableFuture<SendResult<String, String>> send){
        send.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                log.info(" ***** Unable to send message=[" + message + "]");
            }

            @Override
            public void onSuccess(SendResult<String, String> result) {
                log.info(" ***** Sent message=[" + message + "] with " +
                        " partition=[" + result.getRecordMetadata().partition() + "]" +
                        " offset=[" + result.getRecordMetadata().offset() + "]");
            }
        });
    }

}
