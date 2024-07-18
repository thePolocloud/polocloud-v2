package dev.httpmarco.polocloud.rest.endpoints.impl.v1.group;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.httpmarco.polocloud.api.CloudAPI;
import dev.httpmarco.polocloud.rest.RestAPI;
import dev.httpmarco.polocloud.rest.endpoints.Endpoint;
import io.javalin.http.Context;

public class GroupEndpoint extends Endpoint {

    public GroupEndpoint(RestAPI restAPI) {
        super("groups/", restAPI);
    }

    @Override
    public void get(Context context) {
        if (!tokenProvided(context)) return;
        var user = handelAuth(context, getToken(context));
        if (user == null) return;
        if (!hasPermission(user, "polocloud.rest.endpoint.groups", context)) return;

        var response = new JsonObject();
        var groups = new JsonArray();

        CloudAPI.instance().groupProvider().groups().forEach(group -> groups.add(group.toJsonObject()));
        response.add("groups", groups);

        context.status(200);
        context.json(response.toString());
    }
}
