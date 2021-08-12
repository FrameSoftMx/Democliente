package com.operialcompany.Democliente.Constants;

import com.operialcompany.Democliente.Models.RestaurantsModel;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class DataParser {

    public static RestaurantsModel Pasrse_Restaurent(JSONObject jsonObject){

        RestaurantsModel restaurantsModel=new RestaurantsModel();

        try {

            JSONObject jsonObjRestaurant = jsonObject.getJSONObject("Restaurant");
            JSONObject jsonObjCurrency = jsonObject.getJSONObject("Currency");
            JSONObject RestaurantLocation=jsonObject.getJSONObject("RestaurantLocation");

            String symbol = jsonObjCurrency.optString("symbol");
            JSONObject jsonObjTax = jsonObject.getJSONObject("Tax");
            JSONObject jsonObjRating = null;
            try {
                jsonObjRating = jsonObject.getJSONObject("TotalRatings");
            } catch (JSONException ignored) {
                ignored.getCause();
            }

            JSONObject jsonObjDistance = jsonObject.getJSONObject("0");
            String distance = jsonObjDistance.optString("distance");

            restaurantsModel.restaurant_name = jsonObjRestaurant.optString("name");
            restaurantsModel.restaurant_salgon = jsonObjRestaurant.optString("slogan");
            restaurantsModel.restaurant_about = jsonObjRestaurant.optString("about");
            restaurantsModel.restaurant_fee = symbol + jsonObjRestaurant.optString("delivery_fee");
            restaurantsModel.restaurant_image = jsonObjRestaurant.optString("image");
            restaurantsModel.restaurant_id = jsonObjRestaurant.optString("id");
            restaurantsModel.restaurant_phone = jsonObjRestaurant.optString("phone");
            restaurantsModel.restaurant_cover = jsonObjRestaurant.optString("cover_image");
            restaurantsModel.restaurant_isFav = jsonObjRestaurant.optString("favourite");
            restaurantsModel.promoted = jsonObjRestaurant.optString("promoted");
            restaurantsModel.preparation_time = jsonObjRestaurant.optString("preparation_time");
            restaurantsModel.min_order_price = jsonObjRestaurant.optString("min_order_price");
            restaurantsModel.restaurant_menu_style = jsonObjRestaurant.optString("menu_style");
            restaurantsModel.deliveryFee_Range = jsonObjRestaurant.optString("delivery_free_range");

            if (distance != null) {
                String distanceKM = String.valueOf(new DecimalFormat("##.#").format(Double.parseDouble(distance) * 1.6)) + " KM";
                restaurantsModel.restaurant_distance = distanceKM;
            } else
                restaurantsModel.restaurant_distance = "0.0 KM";


            if (jsonObjRating == null) {

                restaurantsModel.restaurant_avgRating = "0.00";
                restaurantsModel.restaurant_totalRating = "0.00";
            } else {
                restaurantsModel.restaurant_avgRating = jsonObjRating.optString("avg");
            }
            restaurantsModel.restaurant_currency = jsonObjCurrency.optString("symbol");
            restaurantsModel.restaurant_tax = jsonObjTax.optString("tax");
            restaurantsModel.delivery_fee_per_km = jsonObjTax.optString("delivery_fee_per_km");
            restaurantsModel.deliveryTime = jsonObjTax.getString("delivery_time");

            restaurantsModel.lat=RestaurantLocation.optString("lat","0.0");
            restaurantsModel.lng=RestaurantLocation.optString("long","0.0");


        }catch (JSONException e){

        }

        return restaurantsModel;

    }


    public static RestaurantsModel Pasrse_favourite_Restaurent(JSONObject jsonObject){

        RestaurantsModel restaurantsModel=new RestaurantsModel();

        try {

            JSONObject jsonObjRestaurant = jsonObject.getJSONObject("Restaurant");
            JSONObject jsonRestaurantFavorite = jsonObject.getJSONObject("RestaurantFavourite");

            JSONObject jsonObjCurrency = jsonObjRestaurant.getJSONObject("Currency");
            String symbol = jsonObjCurrency.optString("symbol");
            JSONObject jsonObjTax = jsonObjRestaurant.getJSONObject("Tax");
            JSONObject jsonObjRating = null;
            try {
                jsonObjRating = jsonObject.getJSONObject("TotalRatings");
            }
            catch (JSONException ignored){
                ignored.getCause();
            }

            restaurantsModel.restaurant_name=jsonObjRestaurant.optString("name");
            restaurantsModel.restaurant_salgon=jsonObjRestaurant.optString("slogan");
            restaurantsModel.restaurant_about=jsonObjRestaurant.optString("about");
            restaurantsModel.restaurant_fee=symbol+jsonObjRestaurant.optString("delivery_fee");
            restaurantsModel.restaurant_image=jsonObjRestaurant.optString("image");
            restaurantsModel.restaurant_id=jsonObjRestaurant.optString("id");
            restaurantsModel.restaurant_phone=jsonObjRestaurant.optString("phone");
            restaurantsModel.restaurant_cover=jsonObjRestaurant.optString("cover_image");
            restaurantsModel.min_order_price=jsonObjRestaurant.optString("min_order_price");
            restaurantsModel.restaurant_isFav=jsonRestaurantFavorite.optString("favourite");
            restaurantsModel.promoted=jsonObjRestaurant.optString("promoted");
            restaurantsModel.preparation_time=jsonObjRestaurant.optString("preparation_time");

            if(jsonObjRating==null) {

                restaurantsModel.restaurant_avgRating="0.00";
                restaurantsModel.restaurant_totalRating="0.00";
            }
            else {
                restaurantsModel.restaurant_avgRating=jsonObjRating.optString("avg");
            }
            restaurantsModel.restaurant_currency=jsonObjCurrency.optString("symbol");
            restaurantsModel.restaurant_tax=jsonObjTax.optString("tax");
            restaurantsModel.delivery_fee_per_km=jsonObjTax.optString("delivery_fee_per_km");

            restaurantsModel.restaurant_menu_style=jsonObjRestaurant.optString("menu_style");
            restaurantsModel.deliveryFee_Range=jsonObjRestaurant.optString("delivery_free_range");


        }catch (JSONException e){

        }

        return restaurantsModel;

    }


}
