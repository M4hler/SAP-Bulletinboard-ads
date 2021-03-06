package com.sap.bulletinboard.ads.services;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.sap.bulletinboard.ads.services.UserServiceClient.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

public class GetUserCommand extends HystrixCommand<User> {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private String url;
    private RestTemplate restTemplate;

    public GetUserCommand(String url, RestTemplate restTemplate) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("User"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("User.getById")));
        this.url = url;
        this.restTemplate = restTemplate;
    }

    @Override
    protected User run() throws Exception {
        logger.info("sending request {}", url);

        try {
            ResponseEntity<User> responseEntity = sendRequest();
            logger.info("received response, status code: {}", responseEntity.getStatusCode());
            return responseEntity.getBody();
        } catch(HttpServerErrorException error) {
            logger.warn("received HTTP status code: {}", error.getStatusCode());
            throw error;
        } catch(HttpClientErrorException error) {
            logger.error("received HTTP status code: {}", error.getStatusCode());
            throw new HystrixBadRequestException("Unsuccessful outgoing request", error);
        }
    }

    protected ResponseEntity<User> sendRequest() {
        return restTemplate.getForEntity(url, User.class);
    }

    // this will be used in exercise 18
    protected int getTimeoutInMs() {
        return this.properties.executionTimeoutInMilliseconds().get();
    }

    @Override
    protected User getFallback()
    {
        logger.info("enter fallback method");
        if (isResponseTimedOut()) {
            logger.error("execution timed out after {} ms (HystrixCommandKey:{})", getTimeoutInMs(),
                    this.getCommandKey().name());
        }
        if (isFailedExecution()) {
            logger.error("execution failed", getFailedExecutionException());
        }
        if (isResponseRejected()) {
            logger.warn("request was rejected");
        }
        return new User();
    }
}