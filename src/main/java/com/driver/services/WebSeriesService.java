package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.SubscriptionType;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo
        String name = webSeriesEntryDto.getSeriesName();
        if(webSeriesRepository.findBySeriesName(name)!=null)
            throw new Exception("Series is already present");
        int ageLimit = webSeriesEntryDto.getAgeLimit();
        double rating = webSeriesEntryDto.getRating();
        SubscriptionType subscriptionType = webSeriesEntryDto.getSubscriptionType();
        Integer productionHouseId = webSeriesEntryDto.getProductionHouseId();
        Optional<ProductionHouse> productionHouseOptional = productionHouseRepository.findById(productionHouseId);
        if(!productionHouseOptional.isPresent())
            throw new Exception("Cannot Find production house.");
        ProductionHouse productionHouse = productionHouseOptional.get();
        WebSeries webSeries = new WebSeries(name,ageLimit,rating,subscriptionType);
        webSeries.setProductionHouse(productionHouse);
        webSeries = webSeriesRepository.save(webSeries);
        productionHouse.getWebSeriesList().add(webSeries);
        double sum = 0.0,length = 0.0;
        for(WebSeries webSeries1: productionHouse.getWebSeriesList()){
            sum += webSeries1.getRating();
            length += 1;
        }
        productionHouse.setRatings(sum/(length));
        productionHouseRepository.save(productionHouse);
        return webSeries.getId();
    }

}
