
package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class Configuration {

    @Expose
    private String name;
    @Expose
    private Integer zigbeechannel;
    @Expose
    private String mac;
    @Expose
    private Boolean dhcp;
    @Expose
    private String ipaddress;
    @Expose
    private String netmask;
    @Expose
    private String gateway;
    @Expose
    private String proxyaddress;
    @Expose
    private Integer proxyport;
    @Expose
    private String UTC;
    @Expose
    private String localtime;
    @Expose
    private String timezone;
    @Expose
    private HashMap<String,Whitelist> whitelist;
    @Expose
    private String swversion;
    @Expose
    private String apiversion;
    @Expose
    private Swupdate swupdate;
    @Expose
    private Boolean linkbutton;
    @Expose
    private Boolean portalservices;
    @Expose
    private String portalconnection;
    @Expose
    private Portalstate portalstate;

    public String getName() {
        return name;
    }

    public Integer getZigbeechannel() {
        return zigbeechannel;
    }

    public String getMac() {
        return mac;
    }

    public Boolean getDhcp() {
        return dhcp;
    }

    public String getIpaddress() {
        return ipaddress;
    }

    public String getNetmask() {
        return netmask;
    }

    public String getGateway() {
        return gateway;
    }

    public String getProxyaddress() {
        return proxyaddress;
    }

    public Integer getProxyport() {
        return proxyport;
    }

    public String getUTC() {
        return UTC;
    }

    public String getLocaltime() {
        return localtime;
    }

    public String getTimezone() {
        return timezone;
    }

    public HashMap<String, Whitelist> getWhitelist() {
        return whitelist;
    }

    public String getSwversion() {
        return swversion;
    }

    public String getApiversion() {
        return apiversion;
    }

    public Swupdate getSwupdate() {
        return swupdate;
    }

    public Boolean getLinkbutton() {
        return linkbutton;
    }

    public Boolean getPortalservices() {
        return portalservices;
    }

    public String getPortalconnection() {
        return portalconnection;
    }

    public Portalstate getPortalstate() {
        return portalstate;
    }
}

