package com.core2web.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.core2web.model.Alert;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;

public class AlertDao {

    private static final String COLLECTION = "alerts";


    public List<Alert> getAlertsForDriver(String driverId, String idToken) {
        List<JSONObject> docs = FirestoreHelper.queryWithFilters(
                COLLECTION,
                Arrays.asList(new FirestoreHelper.Filter("driverId", "EQUAL", driverId)),
                idToken
        );
        return toDomainList(docs);
    }


    public String createAlert(Alert alert, String idToken) {
        Map<String, Object> fields = Map.of(
                "driverId", alert.getDriverId(),
                "busId", alert.getBusId(),
                "issueType", alert.getIssueType(),
                "severity", alert.getSeverity(),
                "description", alert.getDescription(),
                "status", alert.getStatus(),
                "createdAt", alert.getCreatedAt()
        );

        return FirestoreHelper.createDocument(
                COLLECTION,
                fields,
                idToken
        );
    }


    public boolean cancelAlert(
            String alertId,
            String idToken) {

        return FirestoreHelper.updateFields(
                COLLECTION,
                alertId,
                Map.of(
                        "status",
                        "CANCELLED"
                ),
                idToken
        );
    }


    private List<Alert> toDomainList(List<JSONObject> docs) {
        List<Alert> list = new ArrayList<>();

        for (JSONObject doc : docs) {
            list.add(fromDocument(doc));
        }

        return list;
    }


    private Alert fromDocument(JSONObject doc) {
        Alert a = new Alert();

        a.setId(
                FirestoreHelper.getDocumentId(doc)
        );

        a.setDriverId(
                FirestoreHelper.getString(
                        doc,
                        "driverId"
                )
        );

        a.setBusId(
                FirestoreHelper.getString(
                        doc,
                        "busId"
                )
        );

        a.setIssueType(
                FirestoreHelper.getString(
                        doc,
                        "issueType"
                )
        );

        a.setSeverity(
                FirestoreHelper.getString(
                        doc,
                        "severity"
                )
        );

        a.setDescription(
                FirestoreHelper.getString(
                        doc,
                        "description"
                )
        );

        a.setStatus(
                FirestoreHelper.getString(
                        doc,
                        "status"
                )
        );

        a.setCreatedAt(
                FirestoreHelper.getString(
                        doc,
                        "createdAt"
                )
        );

        return a;


    }
    public void deleteAlert(String alertId, String idToken) {
        Firestore db=FirestoreClient.getFirestore();
        try{
        db.collection(COLLECTION).document(alertId).delete();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}