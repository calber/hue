
package models;

import com.google.gson.annotations.Expose;

import java.util.HashMap;

public class Configuration {

    @Expose
    public String name;
    @Expose
    public Integer zigbeechannel;
    @Expose
    public String mac;
    @Expose
    public Boolean dhcp;
    @Expose
    public String ipaddress;
    @Expose
    public String netmask;
    @Expose
    public String gateway;
    @Expose
    public String proxyaddress;
    @Expose
    public Integer proxyport;
    @Expose
    public String UTC;
    @Expose
    public String localtime;
    @Expose
    public String timezone;
    @Expose
    public HashMap<String,Whitelist> whitelist;
    @Expose
    public String swversion;
    @Expose
    public String apiversion;
    @Expose
    public Swupdate swupdate;
    @Expose
    public Boolean linkbutton;
    @Expose
    public Boolean portalservices;
    @Expose
    public String portalconnection;
    @Expose
    public Portalstate portalstate;

}

