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

    /*
     * Kept as a separate constant for compatibility with the existing
     * owner-driver notification flow.
     *
     * It intentionally points to the same Firestore collection.
     */
    private static final String OWNER_DRIVER_COLLECTION = "notifications";


    // ============================================================
    // GET NOTIFICATIONS FOR CURRENT USER
    // ============================================================

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


    // ============================================================
    // ADMIN BROADCAST NOTIFICATIONS
    // ============================================================

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


    // ============================================================
    // OWNER DRIVER NOTIFICATIONS
    // ============================================================

    /**
     * Returns notifications belonging to the logged-in owner.
     *
     * IMPORTANT:
     *
     * The owner is identified using the Firebase Auth UID stored
     * in the notification's "userId" field.
     *
     * We intentionally do NOT filter notification types here.
     *
     * This allows the owner to receive:
     *
     *     bus_assigned
     *     driver_booking_accepted
     *     driver_response
     *     charging_started
     *     charging_completed
     *     and any future owner notification types.
     *
     * The actual notification type is still displayed by the
     * existing OwnerNotifications UI.
     */
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

        /*
         * Do NOT filter by notification type here.
         *
         * The userId is the ownership boundary.
         */
        return toList(docs);
    }


    // ============================================================
    // CREATE NORMAL USER NOTIFICATION
    // ============================================================

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


    // ============================================================
    // CREATE ADMIN BROADCAST NOTIFICATION
    // ============================================================

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

        /*
         * Admin broadcasts intentionally have no individual userId.
         */
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


    // ============================================================
    // CREATE OWNER DRIVER UPDATE NOTIFICATION
    // ============================================================

    /**
     * Creates a notification specifically for an owner when a
     * driver/bus/booking event occurs.
     *
     * The owner's Firebase Auth UID is stored in "userId".
     */
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

        /*
         * CRITICAL:
         *
         * This MUST be the Firebase Auth UID of the owner.
         */
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


    // ============================================================
    // COMMON NOTIFICATION FIELDS
    // ============================================================

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

        /*
         * FirestoreHelper already converts createdAt into a
         * Firestore timestamp because the field ends with "At".
         */
        fields.put(
                "createdAt",
                Instant.now().toString()
        );

        return fields;
    }


    // ============================================================
    // AUTO ID
    // ============================================================

    private String autoId() {
        return UUID.randomUUID().toString();
    }


    // ============================================================
    // FIRESTORE -> NOTIFICATION MODEL
    // ============================================================

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