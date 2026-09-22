
package com.core2web.dao;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.core2web.model.AuthSession;
import com.core2web.model.Driver;

public class DriverDao {

    private static final String PROJECT_ID = "chargeon-1793c";

    private static final String COLLECTION = "Driver";

    private static final String FIREBASE_API_KEY = "AIzaSyBWHcZ1eZiWn49k_RFCQSAb0G9kYDM6yos";

    private final HttpClient client;

    public DriverDao() {
        client = HttpClient.newHttpClient();
    }



    public boolean addDriver(Driver driver, String password) {
        return createDriverAccount(driver, password) != null;
    }


    /**
     * Creates the driver (auth account + Firestore doc) and, when {@code busId} is
     * given, links driver and bus in both directions. Returns the new driver uid, or
     * null if either the account creation or the bidirectional link failed.
     *
     * <p>If the link fails the driver account is left in place but reported as a
     * failure, because a half-written link is worse than an unassigned driver: the
     * caller surfaces the error and the admin can re-assign from Users & Drivers.
     */
    public String addDriverAndAssignBus(Driver driver, String password, String busId) {

        String uid = createDriverAccount(driver, password);

        if (uid == null) {
            return null;
        }

        if (busId == null || busId.isEmpty()) {
            return uid;
        }

        AuthSession session = AuthSession.getCurrent();

        if (session == null) {
            System.out.println("No admin session available to link bus " + busId + ".");
            return null;
        }

        if (!linkDriverAndBus(uid, busId, session.getIdToken())) {
            return null;
        }

        return uid;
    }


    /**
     * Writes Driver.assignedBusId and Bus.assignedDriverId together, then clears
     * both kinds of stale pointer that a re-assignment can leave behind. Firestore
     * REST gives us no multi-document transaction, so the bus side is written first
     * and rolled back if the driver side fails — the invariant we protect is "never
     * one side only".
     */
    public boolean linkDriverAndBus(String driverUid, String busId, String idToken) {

        if (driverUid == null || driverUid.isEmpty() || busId == null || busId.isEmpty()) {
            System.out.println("Cannot link driver and bus: missing driver uid or bus id.");
            return false;
        }

        BusDao busDao = new BusDao();

        // Whoever held this bus before us, read BEFORE we overwrite the pointer.
        com.core2web.model.Bus target = busDao.getBus(busId, idToken);
        String previousDriverUid = target == null ? null : target.getAssignedDriverId();

        if (!busDao.updateAssignedDriver(busId, driverUid, idToken)) {
            System.out.println("Failed to set assignedDriverId on bus " + busId + ".");
            return false;
        }

        if (!updateAssignedBusId(driverUid, busId, idToken)) {
            System.out.println("Failed to set assignedBusId on driver " + driverUid
                    + "; rolling back bus " + busId + ".");
            busDao.updateAssignedDriver(busId, previousDriverUid == null ? "" : previousDriverUid,
                    idToken);
            return false;
        }

        // Release any OTHER bus this driver used to hold, so no bus keeps a stale
        // pointer back to them.
        for (com.core2web.model.Bus other : busDao.getBusesForDriver(driverUid, idToken)) {
            if (!busId.equals(other.getId())) {
                busDao.updateAssignedDriver(other.getId(), "", idToken);
            }
        }

        // And release the PREVIOUS holder of this bus. Without this the old driver
        // keeps claiming a bus that now belongs to someone else — exactly the drift
        // where buses/{id}.assignedDriverId and Driver/{uid}.assignedBusId disagree.
        if (previousDriverUid != null
                && !previousDriverUid.isEmpty()
                && !"null".equals(previousDriverUid)
                && !previousDriverUid.equals(driverUid)) {
            System.out.println("Bus " + busId + " taken from driver " + previousDriverUid
                    + "; clearing their assignedBusId.");
            updateAssignedBusId(previousDriverUid, "", idToken);
        }

        return true;
    }


    /** Driver -> bus side of the link. Prefer {@link #linkDriverAndBus} so both sides move together. */
    public boolean updateAssignedBusId(String driverUid, String busId, String idToken) {
        return FirestoreHelper.updateFields(
                COLLECTION, driverUid,
                java.util.Map.of("assignedBusId", busId == null ? "" : busId),
                idToken);
    }


    private String createDriverAccount(Driver driver, String password) {

        if (driver == null) {
            System.out.println("Driver data is null.");
            return null;
        }

        if (password == null || password.isEmpty()) {
            System.out.println("Password is required.");
            return null;
        }

        try {


            String authUrl =
                    "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key="
                            + FIREBASE_API_KEY;

            JSONObject authBody = new JSONObject();

            authBody.put(
                    "email",
                    driver.getEmail()
            );

            authBody.put(
                    "password",
                    password
            );

            authBody.put(
                    "returnSecureToken",
                    true
            );

            HttpRequest authRequest =
                    HttpRequest.newBuilder()
                            .uri(URI.create(authUrl))
                            .header(
                                    "Content-Type",
                                    "application/json"
                            )
                            .POST(
                                    HttpRequest.BodyPublishers.ofString(
                                            authBody.toString()
                                    )
                            )
                            .build();

            HttpResponse<String> authResponse =
                    client.send(
                            authRequest,
                            HttpResponse.BodyHandlers.ofString()
                    );



            if (authResponse.statusCode() != 200) {

                System.out.println(
                        "Failed to create driver Authentication account: "
                                + authResponse.statusCode()
                                + " "
                                + authResponse.body()
                );

                return null;
            }



            JSONObject authResult =
                    new JSONObject(authResponse.body());

            String uid =
                    authResult.optString(
                            "localId",
                            ""
                    );

            if (uid.isEmpty()) {

                System.out.println(
                        "Firebase UID was not returned."
                );

                return null;
            }



            driver.setUid(uid);



            String idToken =
                    authResult.optString(
                            "idToken",
                            ""
                    );

            if (idToken.isEmpty()) {

                System.out.println(
                        "Firebase ID token was not returned."
                );

                return null;
            }



            JSONObject fields =
                    new JSONObject();



            fields.put(
                    "assignedBusId",
                    new JSONObject().put(
                            "stringValue",
                            driver.getAssignedBusId()
                    )
            );



            fields.put(
                    "depot",
                    new JSONObject().put(
                            "stringValue",
                            driver.getDepot()
                    )
            );



            fields.put(
                    "email",
                    new JSONObject().put(
                            "stringValue",
                            driver.getEmail()
                    )
            );



            fields.put(
                    "name",
                    new JSONObject().put(
                            "stringValue",
                            driver.getName()
                    )
            );



            try {

                long phoneNumber =
                        Long.parseLong(
                                driver.getPhone()
                        );

                fields.put(
                        "phone",
                        new JSONObject().put(
                                "integerValue",
                                String.valueOf(phoneNumber)
                        )
                );

            } catch (NumberFormatException e) {

                System.out.println(
                        "Invalid phone number: "
                                + driver.getPhone()
                );

                return null;
            }



            fields.put(
                    "profileImageUrl",
                    new JSONObject().put(
                            "stringValue",
                            driver.getProfileImageUrl()
                    )
            );



            fields.put(
                    "shift",
                    new JSONObject().put(
                            "stringValue",
                            driver.getShift()
                    )
            );



            fields.put(
                    "status",
                    new JSONObject().put(
                            "stringValue",
                            driver.getStatus()
                    )
            );



            String firestoreUrl =
                    "https://firestore.googleapis.com/v1/projects/"
                            + PROJECT_ID
                            + "/databases/(default)/documents/"
                            + COLLECTION
                            + "/"
                            + uid;


            JSONObject firestoreBody =
                    new JSONObject();

            firestoreBody.put(
                    "fields",
                    fields
            );


            HttpRequest firestoreRequest =
                    HttpRequest.newBuilder()
                            .uri(URI.create(firestoreUrl))
                            .header(
                                    "Content-Type",
                                    "application/json"
                            )
                            .header(
                                    "Authorization",
                                    "Bearer " + idToken
                            )
                            .method(
                                    "PATCH",
                                    HttpRequest.BodyPublishers.ofString(
                                            firestoreBody.toString()
                                    )
                            )
                            .build();


            HttpResponse<String> firestoreResponse =
                    client.send(
                            firestoreRequest,
                            HttpResponse.BodyHandlers.ofString()
                    );



            if (firestoreResponse.statusCode() == 200) {

                System.out.println(
                        "Driver added successfully."
                );

                System.out.println(
                        "Firebase UID: " + uid
                );

                System.out.println(
                        "Driver Firestore document created."
                );

                return uid;
            }



            System.out.println(
                    "Failed to create Driver Firestore document: "
                            + firestoreResponse.statusCode()
                            + " "
                            + firestoreResponse.body()
            );

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }



    public Driver getCurrentDriver() {

        AuthSession session = AuthSession.getCurrent();

        if (session == null) {
            System.out.println("No logged-in user found.");
            return null;
        }

        String uid = session.getUid();
        String idToken = session.getIdToken();

        String url = "https://firestore.googleapis.com/v1/projects/"
                + PROJECT_ID
                + "/databases/(default)/documents/"
                + COLLECTION
                + "/"
                + uid;

        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + idToken)
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (response.statusCode() != 200) {

                System.out.println(
                        "Failed to load driver: "
                                + response.statusCode()
                                + " "
                                + response.body()
                );

                return null;
            }

            JSONObject document =
                    new JSONObject(response.body());

            JSONObject fields =
                    document.optJSONObject("fields");

            if (fields == null) {
                return null;
            }

            Driver driver = new Driver();

            driver.setUid(uid);

            driver.setName(
                    getStringField(fields, "name")
            );

            driver.setEmail(
                    getStringField(fields, "email")
            );

            driver.setPhone(
                    getStringField(fields, "phone")
            );

            driver.setAssignedBusId(
                    getStringField(fields, "assignedBusId")
            );

            driver.setDepot(
                    getStringField(fields, "depot")
            );

            driver.setShift(
                    getStringField(fields, "shift")
            );

            driver.setStatus(
                    getStringField(fields, "status")
            );

            driver.setProfileImageUrl(
                    getStringField(fields, "profileImageUrl")
            );

            return driver;

        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }
    }



    public List<Driver> getAllDrivers(String idToken) {
        List<JSONObject> docs = FirestoreHelper.listCollection(COLLECTION, idToken);
        List<Driver> drivers = new ArrayList<>();

        for (JSONObject doc : docs) {
            Driver driver = fromDocument(FirestoreHelper.getDocumentId(doc), doc);
            if (driver != null) {
                drivers.add(driver);
            }
        }

        return drivers;
    }


    /**
     * One driver by uid, as a single document read.
     *
     * <p>Deliberately not "scan {@link #getAllDrivers} and filter": that needs
     * collection-wide {@code list} permission, which would force the security rules
     * to let any signed-in user enumerate the whole roster just to resolve one name.
     */
    public Driver getDriver(String uid, String idToken) {
        if (uid == null || uid.isEmpty() || "null".equals(uid)) {
            return null;
        }
        JSONObject doc = FirestoreHelper.getDocument(COLLECTION, uid, idToken);
        return doc == null ? null : fromDocument(uid, doc);
    }


    private Driver fromDocument(String uid, JSONObject doc) {
        JSONObject fields = doc.optJSONObject("fields");
        if (fields == null) {
            return null;
        }

        Driver driver = new Driver();
        driver.setUid(uid);
        driver.setName(getStringField(fields, "name"));
        driver.setEmail(getStringField(fields, "email"));
        driver.setPhone(getStringField(fields, "phone"));
        driver.setAssignedBusId(getStringField(fields, "assignedBusId"));
        driver.setDepot(getStringField(fields, "depot"));
        driver.setShift(getStringField(fields, "shift"));
        driver.setStatus(getStringField(fields, "status"));
        driver.setProfileImageUrl(getStringField(fields, "profileImageUrl"));
        return driver;
    }


    public boolean updatePhone(String phone) {

        AuthSession session = AuthSession.getCurrent();

        if (session == null) {
            System.out.println("No logged-in user found.");
            return false;
        }

        String uid = session.getUid();
        String idToken = session.getIdToken();

        String url = "https://firestore.googleapis.com/v1/projects/"
                + PROJECT_ID
                + "/databases/(default)/documents/"
                + COLLECTION
                + "/"
                + uid
                + "?updateMask.fieldPaths=phone";

        JSONObject fields = new JSONObject();

        fields.put(
                "phone",
                new JSONObject().put(
                        "stringValue",
                        phone
                )
        );

        JSONObject body = new JSONObject();

        body.put("fields", fields);

        try {

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .header(
                                    "Content-Type",
                                    "application/json"
                            )
                            .header(
                                    "Authorization",
                                    "Bearer " + idToken
                            )
                            .method(
                                    "PATCH",
                                    HttpRequest.BodyPublishers.ofString(
                                            body.toString()
                                    )
                            )
                            .build();

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (response.statusCode() == 200) {

                System.out.println(
                        "Driver phone updated successfully."
                );

                return true;
            }

            System.out.println(
                    "Failed to update driver phone: "
                            + response.statusCode()
                            + " "
                            + response.body()
            );

            return false;

        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }



    public boolean updateProfileImageUrl(String imageUrl) {

        AuthSession session = AuthSession.getCurrent();

        if (session == null) {
            System.out.println("No logged-in user found.");
            return false;
        }

        String uid = session.getUid();
        String idToken = session.getIdToken();

        String url = "https://firestore.googleapis.com/v1/projects/"
                + PROJECT_ID
                + "/databases/(default)/documents/"
                + COLLECTION
                + "/"
                + uid
                + "?updateMask.fieldPaths=profileImageUrl";

        JSONObject fields = new JSONObject();

        fields.put(
                "profileImageUrl",
                new JSONObject().put(
                        "stringValue",
                        imageUrl
                )
        );

        JSONObject body = new JSONObject();

        body.put("fields", fields);

        try {

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .header(
                                    "Content-Type",
                                    "application/json"
                            )
                            .header(
                                    "Authorization",
                                    "Bearer " + idToken
                            )
                            .method(
                                    "PATCH",
                                    HttpRequest.BodyPublishers.ofString(
                                            body.toString()
                                    )
                            )
                            .build();

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (response.statusCode() == 200) {

                System.out.println(
                        "Driver profile image URL updated successfully."
                );

                return true;
            }

            System.out.println(
                    "Failed to update driver profile image: "
                            + response.statusCode()
                            + " "
                            + response.body()
            );

            return false;

        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }



    private String getStringField(
            JSONObject fields,
            String fieldName) {

        JSONObject field =
                fields.optJSONObject(fieldName);

        if (field == null) {
            return "";
        }

        if (field.has("integerValue")) {

            return field.optString(
                    "integerValue",
                    ""
            );
        }

        return field.optString(
                "stringValue",
                ""
        );
    }
}

