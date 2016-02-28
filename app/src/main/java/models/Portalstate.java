package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Portalstate {

    @SerializedName("signedon")
    @Expose
    private Boolean signedon;
    @SerializedName("incoming")
    @Expose
    private Boolean incoming;
    @SerializedName("outgoing")
    @Expose
    private Boolean outgoing;
    @SerializedName("communication")
    @Expose
    private String communication;

    /**
     *
     * @return
     * The signedon
     */
    public Boolean getSignedon() {
        return signedon;
    }

    /**
     *
     * @param signedon
     * The signedon
     */
    public void setSignedon(Boolean signedon) {
        this.signedon = signedon;
    }

    /**
     *
     * @return
     * The incoming
     */
    public Boolean getIncoming() {
        return incoming;
    }

    /**
     *
     * @param incoming
     * The incoming
     */
    public void setIncoming(Boolean incoming) {
        this.incoming = incoming;
    }

    /**
     *
     * @return
     * The outgoing
     */
    public Boolean getOutgoing() {
        return outgoing;
    }

    /**
     *
     * @param outgoing
     * The outgoing
     */
    public void setOutgoing(Boolean outgoing) {
        this.outgoing = outgoing;
    }

    /**
     *
     * @return
     * The communication
     */
    public String getCommunication() {
        return communication;
    }

    /**
     *
     * @param communication
     * The communication
     */
    public void setCommunication(String communication) {
        this.communication = communication;
    }

}
