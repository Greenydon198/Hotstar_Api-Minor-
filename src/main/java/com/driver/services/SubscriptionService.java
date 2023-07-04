package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto) {
        //We need to buy subscription and save its relevant subscription to the db and return the finalAmount
        // Save The subscription Object into the Db and return the total Amount that user has to pay
        Optional<User> userOptional = userRepository.findById(subscriptionEntryDto.getUserId());
//        if(!userOptional.isPresent())
//            throw new Exception("User Not Found.");
        User user = userOptional.get();

        SubscriptionType subscriptionType  = subscriptionEntryDto.getSubscriptionType();
        int noOfScreensSubscribed = subscriptionEntryDto.getNoOfScreensRequired();
        Date startSubscriptionDate = new Date();
        int totalAmountPaid = getPrice(subscriptionType,noOfScreensSubscribed);
        Subscription subscription = new Subscription(subscriptionType,noOfScreensSubscribed,startSubscriptionDate,totalAmountPaid);
        subscription.setUser(user);
        user.setSubscription(subscription);
        userRepository.save(user);
        return totalAmountPaid;
    }

    private int getPrice(SubscriptionType type,int screens){
        int price;
        if(type.equals(SubscriptionType.BASIC)){
            price = 500 + 200 * screens;
        }
        else if(type.equals(SubscriptionType.PRO)){
            price = 800 + 250 * screens;
        }
        else{
            price = 1000 + 350 * screens;
        }
        return price;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{
        //In this function you need to upgrade the subscription to  its next level
        //ie if You are A BASIC subscriber update to PRO and if You are a PRO upgrade to ELITE.
        //Incase you are already an ELITE member throw an Exception
        //and at the end return the difference in fare that you need to pay to get this subscription done.

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        Optional<User> userOptional = userRepository.findById(userId);
        if(!userOptional.isPresent())
            throw new Exception("Cannot find user.");

        User user = userOptional.get();
        Subscription subscription = user.getSubscription();
        SubscriptionType type = subscription.getSubscriptionType();
        int screens = subscription.getNoOfScreensSubscribed();
        if(type.equals(SubscriptionType.ELITE)){
            throw new Exception("Already the best Subscription");
        }
        int currPrice = subscription.getTotalAmountPaid();
        int upgradePrice;
        if(type.equals(SubscriptionType.BASIC)){
            upgradePrice = getPrice(SubscriptionType.PRO,screens);
            subscription.setSubscriptionType(SubscriptionType.PRO);
        }
        else{
            upgradePrice = getPrice(SubscriptionType.ELITE,screens);
            subscription.setSubscriptionType(SubscriptionType.ELITE);
        }
        subscription.setTotalAmountPaid(upgradePrice);
        user.setSubscription(subscription);
        userRepository.save(user);
        return upgradePrice - currPrice;
    }

    public Integer calculateTotalRevenueOfHotstar(){
        //Calculate the total Revenue of hot-star from all the Users combined...

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription> subscriptions = subscriptionRepository.findAll();
        int sum = 0;
        for(Subscription subscription: subscriptions){
            sum += subscription.getTotalAmountPaid();
        }
        return sum;
    }

}
