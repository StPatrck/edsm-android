/*
 * Copyright (c) 2017.
 *
 * Copyright 2017 Patrick Honkonen
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.phapps.elitedangerous.edsm.tasks;

import android.net.Uri;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.phapps.elitedangerous.edsm.EdsmClient;
import com.phapps.elitedangerous.edsm.callbacks.GetCommanderCreditsCallbacks;
import com.phapps.elitedangerous.edsm.dto.CreditSummaryDto;
import com.phapps.elitedangerous.edsm.network.GsonRequest;
import com.phapps.elitedangerous.edsm.requests.GetCommanderCreditsRequest;

public class GetCommanderCreditsTask extends CommanderTask {

    private static final String COMMAND = "get-credits";
    private static final String PARAM_PERIOD = "period";

    private final GetCommanderCreditsRequest mRequest;
    private final GetCommanderCreditsCallbacks mCallbacks;

    public GetCommanderCreditsTask(RequestQueue requestQueue, EdsmClient.Server server,
            GetCommanderCreditsRequest request,
            GetCommanderCreditsCallbacks callbacks) {
        super(requestQueue, server, request.getCommanderName(), request.getApiKey());

        mRequest = request;
        mCallbacks = callbacks;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Uri.Builder builder = getUriBuilder(VERSION_1, COMMAND);
        builder.appendQueryParameter(PARAM_COMMANDER_NAME, mRequest.getCommanderName())
                .appendQueryParameter(PARAM_API_KEY, mRequest.getApiKey());

        if (mRequest.getPeriod() != null) {
            builder.appendQueryParameter(PARAM_PERIOD, mRequest.getPeriod());
        }

        GsonRequest<CreditSummaryDto> gsonRequest = new GsonRequest<>(
                builder.build().toString(),
                CreditSummaryDto.class,
                getHeaders(),
                new Response.Listener<CreditSummaryDto>() {
                    @Override
                    public void onResponse(CreditSummaryDto response) {
                        if (response.getMessageNumber() != null &&
                                response.getMessageNumber() == 100) {
                            mCallbacks.onSuccess(response.getCredits());
                        } else {
                            mCallbacks.onFail(response.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mCallbacks.onError();
                    }
                });

        mRequestQueue.add(gsonRequest);
        return null;
    }
}
