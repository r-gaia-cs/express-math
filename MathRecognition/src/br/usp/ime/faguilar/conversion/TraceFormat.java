/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.conversion;

import java.util.ArrayList;

/**
 *
 * @author frank
 */
public class TraceFormat {
    public final static String NAME = "name";
    public final static String TYPE = "type";

    ArrayList<Channel> channels;

    public ArrayList<Channel> getChannels() {
        return channels;
    }

    public void setChannels(ArrayList<Channel> channels) {
        this.channels = channels;
    }

    @Override
    public String toString(){
        String string = "traceformat : "+ "\n";
        for (Channel channel : channels) {
            string += channel;
        }
        return string;
    }
}
