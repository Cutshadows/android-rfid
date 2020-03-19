package com.appc72_uhf.app.interfaceApi;

import com.appc72_uhf.app.models.LectorRespuesta;

import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface LecturaServicio {
    @Headers({
            "Accept: application/json",
            "Access-Allow-Content-Type: application/json",
            "Authorization : Bearer nrORFNTuWX9XkUh5ikcbcVTrIhIgqhXh0pBRp2ui-omH_h8iAz-inx0WycgV2tBigL3QjRP8_GIOjzQypYiJPNMqnYc2a6zn_ZjMgGCz8bHjjWPfSrQeajBMBVjLQW7wAJsiupV_D-9P_3F4VCJt9AcfBkwDctozoat4Gx7jTUfRg65U7utdnnqOurMxR5Fv0FqHnSHmXmBt4cG_9Ep1knHMsoAdYxiRmbqtao6siyXHSLhY4UQ-V0Y9sOQB-AqftwMd-GZdXVJJVgAtr6Gx7w9AkRgkqMH0iD8-2gUIVM2nHxHWcwS-OXG470K1shJ9pOzbgJSMVLAWW_KN5LrXyTtHcRbRx3KhBEJItAmdEB1zop3LHIHXBOrVJ1CN60zAWtn7_c3itN4ly2L7d08BTBPQHddmKoI5w7YhMfwqlphwoaYNPqvjYGausr9-zdzjG8r9swDmQbnwwrSbZtTnrhCFfP8OftDgUNFNnMZUQ86S40A6UPDNifcpwhfxHN45"
    })
    @POST("listLector")
    Call<LectorRespuesta> obtenerListaEtiqueta();

}
