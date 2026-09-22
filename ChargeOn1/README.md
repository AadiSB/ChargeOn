# ChargeOn

ChargeOn is a JavaFX desktop application for managing an EV (electric vehicle) bus fleet — charging, dispatch, live tracking, and driver operations — backed by Firebase/Firestore.

## Features

**Admin**
- Dashboard with fleet-wide stats and notifications
- Booking dispatch: assign drivers to pending charging/route bookings
- Alerts & issue tracking
- User and driver account management
- Subscription plan management

**Owner**
- Fleet management: register and monitor vehicles
- Route creation and bus status overview
- Revenue and history/impact reporting
- Wallet and payments

**Driver**
- Assigned bus and shift/earnings tracking
- Live navigation and live location reporting
- Notifications and issue reporting

**Shared**
- Firebase Authentication–based login/account creation
- Live map tracking via Gluon Maps (OpenStreetMap tiles)
- In-app AI copilot chat (per role)
- Image uploads via Cloudinary

## Tech Stack

- **UI:** JavaFX 21 (controls, fxml, graphics)
- **Language / Build:** Java 17, Maven
- **Backend:** Firebase Admin SDK (Firestore, Authentication)
- **Maps:** Gluon Maps
- **Media:** Cloudinary
- **JSON:** org.json

## Project Structure

```
front-end/
  demo/
    pom.xml
    src/main/java/com/core2web/
      config/       # Firebase / Cloudinary initialization
      controller/   # Business logic between views and DAOs
      dao/          # Firestore data access
      model/        # Data models
      service/      # External service integrations (geocoding, Cloudinary)
      util/         # Shared utilities (e.g. date/time formatting)
      view/         # JavaFX screens (Admin/Owner/Driver dashboards, etc.)
    src/main/resources/
      styles/       # CSS per screen
      assets/       # Images, logos
  firestore.rules    # Firestore security rules
```

## Getting Started

### Prerequisites

- JDK 17
- Maven
- [JavaFX SDK 21.0.12](https://gluonhq.com/products/javafx/) downloaded locally
- A Firebase project with a service account key

### Setup

1. Clone the repo and open `front-end/demo` as a Maven project.
2. Place your own Firebase service account JSON in `src/main/resources/` and update the filename referenced in `FirebaseInitialize.java` if needed. **Do not commit this file** — it grants full admin access to your Firebase project.
3. Update the JavaFX module path in `.vscode/launch.json` (`vmArgs`) to point to your local JavaFX SDK `lib` folder.
4. Run the `Main` launch configuration (or `com.core2web.Main`) with:
   ```
   --module-path <path-to-javafx-sdk>/lib --add-modules javafx.controls,javafx.fxml
   ```

### Firestore Rules

Security rules live in [`firestore.rules`](front-end/firestore.rules) and should be deployed via the Firebase CLI:

```
firebase deploy --only firestore:rules
```
