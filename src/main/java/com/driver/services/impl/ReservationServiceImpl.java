package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
//    @Autowired
//    UserRepository userRepository3;
//    @Autowired
//    SpotRepository spotRepository3;
//    @Autowired
//    ReservationRepository reservationRepository3;
//    @Autowired
//    ParkingLotRepository parkingLotRepository3;
//    @Override
//    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {
//
//    }

    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {
        //Reserve a spot in the given parkingLot such that the total price is minimum. Note that the price per hour for each spot is different
        //Note that the vehicle can only be parked in a spot having a type equal to or larger than given vehicle
        //If parkingLot is not found, user is not found, or no spot is available, throw "Cannot make reservation" exception.

        try {
            if (!userRepository3.findById(userId).isPresent() || !parkingLotRepository3.findById(parkingLotId).isPresent()) {
                throw new Exception("Cannot make reservation");
            }

            User user = userRepository3.findById(userId).get();
            ParkingLot parkingLot = parkingLotRepository3.findById(parkingLotId).get();

            // getting all spotlist from parking lot
            List<Spot> spotList = parkingLot.getSpotList();
            if(spotList.size()==0)  throw new Exception("Cannot make reservation");

            SpotType requiredSpotType;
            if(numberOfWheels > 4){
                requiredSpotType = SpotType.OTHERS;
            }else if(numberOfWheels >2){
                requiredSpotType = SpotType.TWO_WHEELER;
            }else requiredSpotType = SpotType.TWO_WHEELER;


            //        Finding the spot which satisfy the condition
            //        1. total price should be minimum
            //        2. Vehicle can be spot in equal and greater type

            int minPrice = Integer.MAX_VALUE;
            Spot selectedSpot = null;

            for(Spot spot : spotList){
                if(spot.getOccupied()==false){
                    if(spot.getSpotType() == SpotType.OTHERS){
                        int parkingPrice = spot.getPricePerHour()*timeInHours;
                        if(parkingPrice<minPrice){
                            minPrice = parkingPrice;
                            selectedSpot = spot;
                        }
                    }
                    if(spot.getSpotType() == SpotType.FOUR_WHEELER && numberOfWheels<=4){
                        int parkingPrice = spot.getPricePerHour()*timeInHours;
                        if(parkingPrice<minPrice){
                            minPrice = parkingPrice;
                            selectedSpot = spot;
                        }
                    }

                    if(spot.getSpotType() == SpotType.TWO_WHEELER && numberOfWheels <=2){
                        int parkingPrice = spot.getPricePerHour()*timeInHours;
                        if(parkingPrice<minPrice){
                            minPrice = parkingPrice;
                            selectedSpot = spot;
                        }
                    }

                }
            }
            if(selectedSpot == null )  throw new Exception("Cannot make reservation");

            Reservation reservation = new Reservation(timeInHours,user,selectedSpot);
            selectedSpot.setOccupied(true);
            selectedSpot.getReservationList().add(reservation);
            user.getReservationList().add(reservation);

            userRepository3.save(user);
            spotRepository3.save(selectedSpot);

            return reservation;

        }
        catch (Exception e){
            return null;
        }

    }
}
