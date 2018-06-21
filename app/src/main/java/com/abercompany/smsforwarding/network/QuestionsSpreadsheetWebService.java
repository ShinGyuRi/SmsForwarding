package com.abercompany.smsforwarding.network;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface QuestionsSpreadsheetWebService {

    @POST("1FAIpQLScmTiZM7UBWJSNt99KBqQ-osqpIFzB9b5ntXJo15tGDwWqVjw/formResponse")
    @FormUrlEncoded
    Call<Void> completeQuestionnaire(@Field("entry.803530203") String name,
                                     @Field("entry.2095286950") String account,
                                     @Field("entry.1597176839") String amount,
                                     @Field("entry.179284988") String date);
}
