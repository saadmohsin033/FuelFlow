# FuelFlow - Petrol Bunk Management System

A Java 17 + Swing + Maven + MySQL JDBC desktop application. All modules are displayed inside one main JFrame using CardLayout.

## Included modules
- Dashboard with live database statistics
- Fuel management: add, edit, delete and view
- Fuel sales and payment recording
- Customer management
- Employee management
- Supplier management
- Fuel purchases
- Reports for stock, sales and supplier purchases

## Requirements
- JDK 17 or later
- Apache Maven
- VS Code with Extension Pack for Java
- Your Aiven MySQL service and its CA certificate

## Project structure
```text
FuelFlow/
├── pom.xml
├── README.md
├── .gitignore
├── certs/                         # create this; keep certificates out of Git
└── src/main/
    ├── java/com/fuelflow/
    │   ├── Main.java
    │   ├── config/Database.java
    │   ├── ui/MainFrame.java
    │   ├── ui/Theme.java
    │   ├── ui/panels/
    │   └── util/UiUtils.java
    └── resources/db.properties.example
```

## Aiven database connection
The app reads these environment variables:
`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `DB_SSL_MODE`, `DB_TRUSTSTORE`, and `DB_TRUSTSTORE_PASSWORD`.

Use the host, port, username and password shown in your Aiven service. The database name configured for this project is `java_pbl_db`. Do not hard-code your password or commit it to GitHub. Since a password was previously shared in chat, rotate it in Aiven before using this project.

### 1. Download the Aiven CA certificate
Download the CA certificate from your Aiven MySQL service and save it as `certs/ca.pem` in the project root.

### 2. Create a Java truststore
Run these commands in the VS Code PowerShell terminal from the project root:
```powershell
New-Item -ItemType Directory -Force certs
keytool -importcert -alias aiven-ca -file certs/ca.pem -keystore certs/aiven-truststore.jks -storepass changeit -noprompt
```

### 3. Set connection variables
Replace `YOUR_NEW_PASSWORD` with the new password you created in Aiven:
```powershell
$env:DB_HOST = "mysql-java-3096c3e6-petrol-pump-db-01.j.aivencloud.com"
$env:DB_PORT = "22393"
$env:DB_NAME = "java_pbl_db"
$env:DB_USER = "avnadmin"
$env:DB_PASSWORD = "YOUR_NEW_PASSWORD"
$env:DB_SSL_MODE = "VERIFY_CA"
$env:DB_TRUSTSTORE = "certs/aiven-truststore.jks"
$env:DB_TRUSTSTORE_PASSWORD = "changeit"
```
These variables remain available only in the current terminal session. Set them again in a new terminal.

## Open and run in VS Code
1. Extract the ZIP file.
2. In VS Code choose **File > Open Folder** and select the extracted `FuelFlow` folder (the folder containing `pom.xml`).
3. Open **Terminal > New Terminal**.
4. Set the Aiven environment variables as described above.
5. Compile:
```powershell
mvn clean compile
```
6. Run:
```powershell
mvn exec:java
```

## Database notes
- The Java connection uses MySQL Connector/J through JDBC and verifies the Aiven server certificate.
- The supplied database triggers adjust fuel stock when fuel purchase/sale rows are inserted. Java should not adjust the same stock a second time.
- The supplied `Fuel_Tank.current_level` is separate from `Fuel.current_stock`; synchronizing these values is not implemented in this starter.
- The application expects your imported schema and views to exist in `java_pbl_db`. It does not import or recreate the database automatically.

## Scope and limitations
This is a functional starter application, not a production-certified system. It includes the main CRUD, sales, purchase and report screens. Authentication, role permissions, PDF invoice printing, CSV export, chart visualizations, shift management, stronger validation and audit logging are not included.
