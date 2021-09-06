package com.example.smallbusiness.api;

import com.example.smallbusiness.models.food.FoodModel;
import com.example.smallbusiness.models.menu.MenuModel;
import com.example.smallbusiness.models.order.OrderCreateModel;
import com.example.smallbusiness.models.order.OrderModel;
import com.example.smallbusiness.models.restaurant.RestaurantModel;

import io.reactivex.Observable;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IRestaurantAPI {

    Observable<RestaurantModel> getAllRestaurant(@Query("key") String apiKey);

    // MENU
    @GET("menu")
    Observable<MenuModel> getCategory(@Header("Authorization") String authToken, @Query("key") String apiKey, @Query("restaurantId") int restaurantId);

    //FOOD
    @GET("food")
    Observable<FoodModel> getFoodByMenuId(@Query("key") String apiKey, @Query("menuId") int menuId, @Header("Authorization") String authToken);

    @GET("foodById")
    Observable<FoodModel> getFoodById(@Query("key") String apiKey, @Query("id") int id, @Header("Authorization") String authToken);

    @GET("food/search/name")
    Observable<FoodModel> getFoodByName(@Query("key") String apiKey, @Query("search") String search, @Header("Authorization") String authToken);

    //Order
    @POST("createOrder")
    @FormUrlEncoded
    Observable<OrderCreateModel> createOrder(
            @Header("Authorization") String authToken,
            @Field("orderEmail") String orderEmail,
            @Field("orderPhone") String orderPhone,
            @Field("ordername") String ordername,
            @Field("orderAddress") String orderAddress,
            @Field("orderDate") String orderDate,
            @Field("restaurantId") int restaurantId,
            @Field("transactionId") String transactionId,
            @Field("COD") boolean COD,
            @Field("totalPrice") double totalPrice,
            @Field("numOfItem") int numOfItem
            );

    @GET("order")
    Observable<OrderModel> getAllOrder(@Header("Authorization") String authToken, @Query("orderEmail") String orderEmail);
}
