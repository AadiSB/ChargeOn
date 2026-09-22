package com.core2web.controller;

import java.util.ArrayList;
import java.util.List;

import com.core2web.dao.NotificationDao;
import com.core2web.model.AuthSession;
import com.core2web.model.Notification;

public class NotificationController {

    private final NotificationDao notificationDAO =
            new NotificationDao();

    public List<Notification> getMyNotifications() {

        AuthSession session =
                AuthSession.getCurrent();

        if (session == null) {
            return new ArrayList<>();
        }

        return newestFirst(
                notificationDAO.getNotificationsForUser(
                        session.getUid(),
                        session.getIdToken()
                )
        );
    }

    public List<Notification> getAdminFeed() {

        AuthSession session =
                AuthSession.getCurrent();

        if (session == null) {
            return new ArrayList<>();
        }

        return newestFirst(
                notificationDAO.getAdminBroadcastNotifications(
                        session.getIdToken()
                )
        );
    }

    public List<Notification> getOwnerDriverNotifications() {

        AuthSession session =
                AuthSession.getCurrent();

        if (session == null) {
            return new ArrayList<>();
        }

        if (!"owner".equals(session.getRole())) {

            return new ArrayList<>();
        }

        return newestFirst(
                notificationDAO.getOwnerDriverNotifications(
                        session.getUid(),
                        session.getIdToken()
                )
        );
    }

    private static List<Notification> newestFirst(
            List<Notification> notifications) {

        if (notifications == null) {
            return new ArrayList<>();
        }

        notifications.sort(
                (a, b) ->
                        safeCreatedAt(b)
                                .compareTo(
                                        safeCreatedAt(a)
                                )
        );

        return notifications;
    }

    private static String safeCreatedAt(
            Notification notification) {

        if (notification == null
                || notification.getCreatedAt() == null) {

            return "";
        }

        return notification.getCreatedAt();
    }

    public void notifyUser(
            String userId,
            String type,
            String message,
            String linkedEntityId) {

        AuthSession session =
                AuthSession.getCurrent();

        if (session == null
                || session.getIdToken() == null
                || session.getIdToken().isBlank()) {

            System.out.println(
                    "Notification failed: AuthSession/token is null."
            );

            return;
        }

        notificationDAO.notifyUser(
                userId,
                type,
                message,
                linkedEntityId,
                session.getIdToken()
        );
    }

    public void notifyAdmins(
            String type,
            String message,
            String linkedEntityId) {

        AuthSession session =
                AuthSession.getCurrent();

        if (session == null
                || session.getIdToken() == null
                || session.getIdToken().isBlank()) {

            System.out.println(
                    "Admin notification failed: AuthSession/token is null."
            );

            return;
        }

        notificationDAO.notifyAdmins(
                type,
                message,
                linkedEntityId,
                session.getIdToken()
        );
    }

    public void notifyOwnerOfDriverUpdate(
            String ownerId,
            String type,
            String message,
            String linkedEntityId) {

        AuthSession session =
                AuthSession.getCurrent();

        if (session == null) {

            System.out.println(
                    "Notification failed: AuthSession is null."
            );

            return;
        }

        if (ownerId == null
                || ownerId.isBlank()) {

            System.out.println(
                    "Notification failed: ownerId is empty."
            );

            return;
        }

        if (session.getIdToken() == null
                || session.getIdToken().isBlank()) {

            System.out.println(
                    "Notification failed: ID token is empty."
            );

            return;
        }

        boolean success =
                notificationDAO.notifyOwnerOfDriverUpdate(
                        ownerId,
                        type,
                        message,
                        linkedEntityId,
                        session.getIdToken()
                );

        if (success) {

            System.out.println(
                    "Owner notification created successfully."
                    + " ownerId="
                    + ownerId
                    + ", type="
                    + type
                    + ", bookingId="
                    + linkedEntityId
            );

        } else {

            System.out.println(
                    "Owner notification FAILED."
                    + " ownerId="
                    + ownerId
                    + ", type="
                    + type
                    + ", bookingId="
                    + linkedEntityId
            );
        }
    }
}