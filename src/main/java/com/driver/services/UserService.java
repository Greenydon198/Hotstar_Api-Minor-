package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository
        user = userRepository.save(user);
        return user.getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId) throws Exception {

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository
        Optional<User> userOptional = userRepository.findById(userId);
        if(!userOptional.isPresent())
            throw new Exception("User Not Found.");

        User user = userOptional.get();
        SubscriptionType type = user.getSubscription().getSubscriptionType();
        int age = user.getAge();
        if(type.equals(SubscriptionType.BASIC)){
            return basicType(age);
        }
        else if(type.equals(SubscriptionType.PRO)){
            return proType(age);
        }
        else{
            return eliteType(age);
        }
    }

    private Integer eliteType(int age) {
        List<WebSeries> webSeriesList = webSeriesRepository.findAll();
        int count = 0;
        for(WebSeries webSeries: webSeriesList){
            if(age>=webSeries.getAgeLimit())
                count++;
        }
        return count;
    }

    private Integer proType(int age) {
        List<WebSeries> webSeriesList = webSeriesRepository.findAll();
        int count = 0;
        for(WebSeries webSeries: webSeriesList){
            if(age>=webSeries.getAgeLimit() && !webSeries.getSubscriptionType().equals(SubscriptionType.ELITE))
                count++;
        }
        return count;
    }

    private Integer basicType(int age) {
        int count = 0;
        List<WebSeries> webSeriesList = webSeriesRepository.findAll();
        for(WebSeries webSeries: webSeriesList){
            if(age>=webSeries.getAgeLimit() && webSeries.getSubscriptionType().equals(SubscriptionType.BASIC))
                count++;
        }
        return count;
    }


}
