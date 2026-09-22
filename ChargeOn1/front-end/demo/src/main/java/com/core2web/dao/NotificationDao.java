package com.core2web.dao;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.core2web.model.Notification;

public class NotificationDao {

    private static final String COLLECTION = "notifications";

    private static final String OWNER_DRIVER_COLLECTION = "notifications";

    public List<Notification> getNotificationsForUser(
            String uid,
            String idToken) {

        if (uid == null
                || uid.isBlank()
                || idToken == null
                || idToken.isBlank()) {

            return new ArrayList<>();
        }

        List<JSONObject> docs =
                FirestoreHelper.queryWithFilters(
                        COLLECTION,
                        Arrays.asList(
                                new FirestoreHelper.Filter(
                                        "userId",
                                        "EQUAL",
                                        uid
                                )
                        ),
                        idToken
                );

        return toList(docs);
    }

    public List<Notification> getAdminBroadcastNotifications(
            String adminIdToken) {

        if (adminIdToken == null
                || adminIdToken.isBlank()) {

            return new ArrayList<>();
        }

        List<JSONObject> docs =
                FirestoreHelper.queryWithFilters(
                        COLLECTION,
                        Arrays.asList(
                                new FirestoreHelper.Filter(
                                        "audience",
                                        "EQUAL",
                                        "admin_broadcast"
                                )
                        ),
                        adminIdToken
                );

        return toList(docs);
    }

    public List<Notification> getOwnerDriverNotifications(
            String ownerId,
            String idToken) {

        if (ownerId == null
                || ownerId.isBlank()
                || idToken == null
                || idToken.isBlank()) {

            return new ArrayList<>();
        }

        List<JSONObject> docs =
                FirestoreHelper.queryWithFilters(
                        OWNER_DRIVER_COLLECTION,
                        Arrays.asList(
                                new FirestoreHelper.Filter(
                                        "userId",
                                        "EQUAL",
                                        ownerId
                                )
                        ),
                        idToken
                );

        return toList(docs);
    }

    public boolean notifyUser(
            String userId,
            String type,
            String message,
            String linkedEntityId,
            String idToken) {

        if (userId == null
                || userId.isBlank()
                || idToken == null
                || idToken.isBlank()) {

            return false;
        }

        Map<String, Object> fields =
                baseFields(
                        type,
                        message,
                        linkedEntityId
                );

        fields.put(
                "audience",
                "user"
        );

        fields.put(
                "userId",
                userId
        );

        return FirestoreHelper.writeDocument(
                COLLECTION,
                autoId(),
                fields,
                idToken
        );
    }

    public boolean notifyAdmins(
            String type,
            String message,
            String linkedEntityId,
            String idToken) {

        if (idToken == null
                || idToken.isBlank()) {

            return false;
        }

        Map<String, Object> fields =
                baseFields(
                        type,
                        message,
                        linkedEntityId
                );

        fields.put(
                "audience",
                "admin_broadcast"
        );

        fields.put(
                "userId",
                ""
        );

        return FirestoreHelper.writeDocument(
                COLLECTION,
                autoId(),
                fields,
                idToken
        );
    }

    public boolean notifyOwnerOfDriverUpdate(
            String ownerId,
            String type,
            String message,
            String linkedEntityId,
            String idToken) {

        if (ownerId == null
                || ownerId.isBlank()
                || idToken == null
                || idToken.isBlank()) {

            return false;
        }

        Map<String, Object> fields =
                baseFields(
                        type,
                        message,
                        linkedEntityId
                );

        fields.put(
                "audience",
                "owner_driver_update"
        );

        fields.put(
                "userId",
                ownerId
        );

        return FirestoreHelper.writeDocument(
                COLLECTION,
                autoId(),
                fields,
                idToken
        );
    }

    private Map<String, Object> baseFields(
            String type,
            String message,
            String linkedEntityId) {

        Map<String, Object> fields =
                new LinkedHashMap<>();

        fields.put(
                "type",
                type == null
                        ? ""
                        : type
        );

        fields.put(
                "message",
                message == null
                        ? ""
                        : message
        );

        fields.put(
                "linkedEntityId",
                linkedEntityId == null
                        ? ""
                        : linkedEntityId
        );

        fields.put(
                "createdAt",
                Instant.now().toString()
        );

        return fields;
    }

    private String autoId() {
        return UUID.randomUUID().toString();
    }

    private List<Notification> toList(
            List<JSONObject> docs) {

        List<Notification> notifications =
                new ArrayList<>();

        if (docs == null) {
            return notifications;
        }

        for (JSONObject doc : docs) {

            if (doc == null) {
                continue;
            }

            notifications.add(
                    new Notification(
                            FirestoreHelper.getDocumentId(doc),

                            FirestoreHelper.getString(
                                    doc,
                                    "audience"
                            ),

                            FirestoreHelper.getString(
                                    doc,
                                    "userId"
                            ),

                            FirestoreHelper.getString(
                                    doc,
                                    "type"
                            ),

                            FirestoreHelper.getString(
                                    doc,
                                    "message"
                            ),

                            FirestoreHelper.getString(
                                    doc,
                                    "linkedEntityId"
                            ),

                            FirestoreHelper.getString(
                                    doc,
                                    "createdAt"
                            )
                    )
            );
        }

        return notifications;
    }
}