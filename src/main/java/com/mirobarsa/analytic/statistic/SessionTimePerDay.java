/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mirobarsa.analytic.statistic;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mirobarsa.analytic.data.AdditionalMetadata;
import com.mirobarsa.analytic.data.SessionMetadata;

/**
 *
 * @author mbarsocchi
 */
public class SessionTimePerDay {

    private final List<AdditionalMetadata> accessDataArray;
    Map<String, Map<String, SessionMetadata>> uniqueSessionListner = new HashMap<>();

    public SessionTimePerDay(List<AdditionalMetadata> accessDataArray) {
        this.accessDataArray = accessDataArray;
    }

    public void calculateSessionTimePerDay() {

        for (AdditionalMetadata el : accessDataArray) {
            String stringDate = new SimpleDateFormat("dd/MM/yyyy").format(el.getDate());
            if (uniqueSessionListner.containsKey(stringDate)) {
                Map<String, SessionMetadata> userForThisDay = uniqueSessionListner.get(stringDate);
                SessionMetadata sessionMetadataDay = null;
                if (userForThisDay.containsKey(el.getDayHash())) {
                    sessionMetadataDay = userForThisDay.get(el.getDayHash());
                    sessionMetadataDay.setListnerTotalTime(sessionMetadataDay.getListnerTotalTime() + el.getSecondOfListening());
                } else {
                    sessionMetadataDay = new SessionMetadata(el.getSecondOfListening(), el.getUseragent(), el.getDayHash(), el.getIp());
                }
                userForThisDay.put(el.getDayHash(), sessionMetadataDay);
            } else {
                SessionMetadata sessionMetadataDay = new SessionMetadata(el.getSecondOfListening(), el.getUseragent(), el.getDayHash(), el.getIp());
                Map<String, SessionMetadata> t = new HashMap();
                t.put(el.getDayHash(), sessionMetadataDay);
                uniqueSessionListner.put(stringDate, t);
            }
        }
    }

    @Override
    public String toString() {
        Iterator it = uniqueSessionListner.entrySet().iterator();
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            Map<Integer, SessionMetadata> userPerDay = (Map<Integer, SessionMetadata>) pair.getValue();
            sb.append(pair.getKey())
                    .append(" Unique: ")
                    .append(userPerDay.size())
                    .append("\n");

            Iterator<Map.Entry<Integer, SessionMetadata>> userIterator = userPerDay.entrySet().iterator();
            while (userIterator.hasNext()) {
                Map.Entry dataForUser = (Map.Entry) userIterator.next();
                SessionMetadata ssMtd = (SessionMetadata) dataForUser.getValue();
                sb.append("\t")
                        .append(ssMtd.getIp())
                        .append("\t")
                        .append(ssMtd.getUserAgent())
                        .append("\t")
                        .append(ssMtd.getListnerTotalTime())
                        .append("\n");
                userIterator.remove();
            }
            it.remove();
        }
        return sb.toString();
    }

    public int getUniquePerDay(String key) {
        return uniqueSessionListner.get(key).size();
    }
}
