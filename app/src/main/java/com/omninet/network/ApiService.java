package com.omninet.network;

import com.omninet.data.models.Call;
import com.omninet.data.models.Chat;
import com.omninet.data.models.Contact;
import com.omninet.data.models.Group;
import com.omninet.data.models.Message;
import com.omninet.data.models.Status;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("contacts")
    Call<List<Contact>> getContacts();

    @POST("contacts")
    Call<Contact> addContact(@Body Contact contact);

    @PUT("contacts/{id}")
    Call<Contact> updateContact(@Path("id") int id, @Body Contact contact);

    @DELETE("contacts/{id}")
    Call<Void> deleteContact(@Path("id") int id);

    @GET("messages/chat/{chatId}")
    Call<List<Message>> getMessages(@Path("chatId") int chatId);

    @POST("messages")
    Call<Message> sendMessage(@Body Message message);

    @PUT("messages/{id}")
    Call<Message> updateMessage(@Path("id") int id, @Body Message message);

    @DELETE("messages/{id}")
    Call<Void> deleteMessage(@Path("id") int id);

    @GET("chats")
    Call<List<Chat>> getChats();

    @POST("chats")
    Call<Chat> createChat(@Body Chat chat);

    @PUT("chats/{id}")
    Call<Chat> updateChat(@Path("id") int id, @Body Chat chat);

    @DELETE("chats/{id}")
    Call<Void> deleteChat(@Path("id") int id);

    @GET("groups")
    Call<List<Group>> getGroups();

    @POST("groups")
    Call<Group> createGroup(@Body Group group);

    @PUT("groups/{id}")
    Call<Group> updateGroup(@Path("id") int id, @Body Group group);

    @DELETE("groups/{id}")
    Call<Void> deleteGroup(@Path("id") int id);

    @GET("status")
    Call<List<Status>> getStatuses();

    @POST("status")
    Call<Status> uploadStatus(@Body Status status);

    @GET("calls")
    Call<List<Call>> getCalls();

    @POST("calls")
    Call<Call> logCall(@Body Call call);
}
