/*
 *  Copyright 2015 the original author or authors.
 *  @https://github.com/scouter-project/scouter
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package scouterx.webapp.api.controller;

import scouter.util.Hexa32;
import scouterx.client.server.Server;
import scouterx.client.server.ServerManager;
import scouterx.model.ActiveThread;
import scouterx.model.scouter.SActiveService;
import scouterx.model.scouter.SActiveServiceStepCount;
import scouterx.webapp.api.view.CommonResultView;
import scouterx.webapp.service.ActiveServiceService;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author Gun Lee (gunlee01@gmail.com) on 2017. 8. 27.
 */
@Path("/v1/activeService")
@Singleton
@Produces(MediaType.APPLICATION_JSON)
public class ActiveServiceController {
    @Context
    HttpServletRequest servletRequest;

    private final ActiveServiceService activeServiceService;

    public ActiveServiceController() {
        this.activeServiceService = new ActiveServiceService();
    }

    /**
     * current active service count 3-stepped by response time.
     *
     * @param objType
     * @param serverId optional if web instance just connected one collector server.
     * @return
     */
    @GET
    @Path("/stepCount/ofType/{objType}")
    @Consumes(MediaType.APPLICATION_JSON)
    public CommonResultView<List<SActiveServiceStepCount>> retrieveRealTimeActiveServiceByObjType(
            @PathParam("objType") @Valid @NotNull final String objType,
            @QueryParam("serverId") final int serverId) {

        List<SActiveServiceStepCount> activeServiceList = activeServiceService.retrieveRealTimeActiveServiceByObjType(
                objType, ServerManager.getInstance().getServerIfNullDefault(serverId));

        return CommonResultView.success(activeServiceList);
    }

    /**
     * get active service list of given objType
     *
     * @param objType
     * @param serverId optional if web instance just connected one collector server.
     * @return
     */
    @GET
    @Path("/ofType/{objType}")
    @Consumes(MediaType.APPLICATION_JSON)
    public CommonResultView<List<SActiveService>> retrieveRealTimeActiveServiceListByObjType(
            @PathParam("objType") @Valid @NotNull final String objType,
            @QueryParam("serverId") final int serverId) {

        List<SActiveService> activeServiceList = activeServiceService.retrieveActiveServiceListByType(
                objType, ServerManager.getInstance().getServerIfNullDefault(serverId));

        return CommonResultView.success(activeServiceList);
    }

    /**
     * get active service list of given objHash
     *
     * @param objHash
     * @param serverId optional if web instance just connected one collector server.
     * @return
     */
    @GET
    @Path("/ofObject/{objHash}")
    @Consumes(MediaType.APPLICATION_JSON)
    public CommonResultView<List<SActiveService>> retrieveRealTimeActiveServiceListByObjHashes(
            @PathParam("objHash") @Valid @NotNull final int objHash,
            @QueryParam("serverId") final int serverId) {

        List<SActiveService> activeServiceList = activeServiceService.retrieveActiveServiceListByObjHashes(
                objHash, ServerManager.getInstance().getServerIfNullDefault(serverId));

        return CommonResultView.success(activeServiceList);
    }

    /**
     * get thread detail of the object's threadId
     *
     * @param objHash
     * @param threadId
     * @param txidName This value is for valuable service related information. (like service name & a sql that currently running)
     * @param serverId optional if web instance just connected one collector server.
     * @return
     */
    @GET
    @Path("/thread/{threadId}/ofObject/{objHash}")
    @Consumes(MediaType.APPLICATION_JSON)
    public CommonResultView<ActiveThread> retrieveActiveThread(
            @PathParam("objHash") @Valid @NotNull final int objHash,
            @PathParam("threadId") @Valid @NotNull final long threadId,
            @QueryParam("txidName") final String txidName,
            @QueryParam("serverId") final int serverId) {

        long txid = Hexa32.toLong32(txidName);
        Server server = ServerManager.getInstance().getServerIfNullDefault(serverId);
        ActiveThread activeThread = activeServiceService.retrieveActiveThread(objHash, threadId, txid, server);

        return CommonResultView.success(activeThread);
    }
}