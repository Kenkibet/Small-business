package com.example.smallbusiness.api;

import com.example.foodordering.model.food.FoodModel;
import com.example.foodordering.model.menu.MenuModel;
import com.example.foodordering.model.order.OrderCreateModel;
import com.example.foodordering.model.order.OrderModel;
import com.example.foodordering.model.restaurant.RestaurantModel;

import io.reactivex.Observable;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IRestaurantAPI {

    // USER
    @GET("user")
    Observable<UserModel> getUser(@Query("email") String email);

    @POST("user")
    Observable<UpdateUserModel> updateUserInfo(@Field("key") String apiKey,
                                               @Field("userPhone") String userPhone,
                                               @Field("userName") String userName,
                                               @Field("email") String email);

    @POST("user/login")
    @FormUrlEncoded
    Observable<UserModel> login(@Field("email") String email, @Field("password") String password);

    @POST("user/register")
    @FormUrlEncoded
    Observable<UserModel> register(@Field("userPhone") String userPhone,
                                   @Field("name") String name,
                                   @Field("address") String address,
                                   @Field("email")  String email,
                                   @Field("password") String password);

    // RESTAURANT
    @GET("restaurant")
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

    //SIZE
    @GET("size")
    Observable<SizeModel> getSizeOfFood(@Header("Authorization") String authToken, @Query("key") String apiKey, @Query("foodId") int foodId);

    //ADD ON
    @GET("addon")
    Observable<AddOnModel> getAddOnOfFood(@Header("Authorization") String authToken, @Query("key") String apiKey, @Query("foodId") int foodId);

    //FAVORITE
    @GET("favorite")
    Observable<FavoriteModel> getFavoriteByUser(@Header("Authorization") String authToken, @Query("key") String apiKey, @Query("email") String email);

    @GET("favoriteByRestaurant")
    Observable<FavoriteIdModel> getFavoriteByRestaurant(@Header("Authorization") String authToken,
                                                        @Query("key") String apiKey,
                                                        @Query("email") String email,
                                                        @Query("restaurantId") int restaurantId);

    @DELETE("favorite")
    Observable<FavoriteModel> removeFavorite(
            @Header("Authorization") String authToken,
            @Query("key") String apiKey,
            @Query("email") String email,
            @Query("foodId") int foodId,
            @Query("restaurantId") int restaurantId);

    @POST("favorite")
    @FormUrlEncoded
    Observable<FavoriteModel> insertFavorite(
            @Header("Authorization") String authToken,
            @Field("key") String apiKey,
            @Field("email") String email,
            @Field("foodId") int foodId,
            @Field("restaurantId") int restaurantId,
            @Field("restaurantName") String restaurantName,
            @Field("foodName") String foodName,
            @Field("foodImage") String foodImage,
            @Field("foodPrice") double foodPrice
    );

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
