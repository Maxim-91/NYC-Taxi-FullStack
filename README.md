# NYC Taxi FullStack Project Report

This project is a full-stack (Backend + Frontend) application designed to analyze and visualize massive datasets of New York City taxi trips. It leverages a Python-based backend for data processing and API delivery, a PostgreSQL database for robust data storage, and an Android mobile application for interactive data visualization and management.

---

## Backend

The backend layer serves as the foundation of the system. Its primary responsibilities include efficient ETL (Extract, Transform, Load) operations for handling millions of trip records and providing a secure, scalable REST API that the mobile frontend consumes to display analytics and manage lookup tables.

### Сomponents

This backend consists of two integrated projects originally developed by [juhaniguru](https://github.com/juhaniguru):
* **API Service:** based on [emo_2026_api](https://github.com/juhaniguru/emo_2026_api/tree/master)
* **Data Importer:** based on [nyc_taxi_importer](https://github.com/juhaniguru/nyc_taxi_importer/tree/master)

### Setup and Environment

Both components are managed as separate projects within **PyCharm Community Edition**. 
For each project, a dedicated local interpreter has been configured using a virtual environment (`.venv`).


#### 1. Data Importer Setup

The importer is responsible for fetching raw taxi data from Google Drive and populating the PostgreSQL database.

**Location:** `/NYC-Taxi-FullStack/backend/importer`

**Core Dependencies:**
* `pandas`: For data manipulation.
* `gdown`: To download large datasets from Google Drive.
* `sqlalchemy` & `sqlalchemy-utils`: For database schema management and creation.
* `psycopg2-binary`: PostgreSQL adapter for Python.
* `python-dotenv`: To manage environment variables securely.

**Installation command used:**
```bash
pip install -r requirements.txt
```
or
```bash
pip install python-dotenv pandas gdown sqlalchemy sqlalchemy-utils psycopg2-binary
```

#### 2. API Service Setup

The API serves as the bridge between the PostgreSQL database and the Android mobile application.

**Location:** `/NYC-Taxi-FullStack/backend/api`

**Core Dependencies:**
* `flask`: Web framework for building the REST API.
* `psycopg2-binary`: To connect with the taxi database.
* `python-dotenv`: For configuration management.

**Installation command used:**

```bash
pip install -r requirements.txt
```
or
```bash
pip install flask python-dotenv psycopg2-binary
```

#### 3. Database Population Instructions

To correctly populate the database, run the importer script located at `/NYC-Taxi-FullStack/backend/importer/main.py`. 

The script provides a text-based menu. **Crucial:** You must execute the options sequentially from 1 to **9** to ensure data integrity and proper database configuration (see Table 1).

**Table 1.** Text menu of action options

| Option | Action (Finnish) | Description |
| :--- | :--- | :--- |
| **0** | `lopeta` | Stop this running program. |
| **1** | `luo tietokanta` | Creates the `nyc_taxi` database and all table structures (Yellow Trips, Zones, etc.). |
| **2** | `lataa valmis datapaketti Google Drivesta` | Download compressed `.dump` files from Google Drive into the `./data` folder. |
| **3** | `vendorit` | Populates the list of taxi vendors (CMT, VeriFone, etc.). |
| **4** | `payment_typet` | Populates payment methods (Credit Card, Cash, etc.). |
| **5** | `borought` | Populates the list of NYC boroughs (Manhattan, Brooklyn, etc.). |
| **6** | `service_zonet` | Populates service zone categories. |
| **7** | `rate_codet` | Populates rate codes (Standard, JFK, Newark, etc.). |
| **8** | `zonet` | Maps specific Location IDs to names and boroughs (requires `taxi_zone_lookup.csv`). |
| **9** | `yellow_trips` | **Main Data Import:** Restores the massive dataset of taxi trips into the database using `pg_restore`. And synchronizes database IDs so new entries won't cause unique constraint errors. |
| **10** | `aseta oikeat auto incrementtien arvot` | Synchronizes database IDs so new entries won't cause unique constraint errors. |

> **Note 1:** Make sure your PostgreSQL service is running and your `.env` credentials are correct before starting with option 1.

> **Note 2. On option 2:** A file 5016.dat.gz - 5.24 GB will be downloaded, make sure you have enough free disk space.

> **Note 3. On option 9:** This process generates massive temporary files. The 5.24 GB compressed file will expand to **over 50 GB of disk space** to complete successfully, make sure you have enough free disk space. Additionally, the process may take 30 min. - more than 3 hours (depending on the computer's performance).

#### 4. Database Verification

You may verify the data import using any PostgreSQL client (such as pgAdmin or DBeaver). Ensure that all 7 tables (`boroughs`, `payment_types`, `rate_codes`, `service_zones`, `vendors`, `yellow_trips`, and `zones`) are correctly created under the **public** schema as shown in Figure 1.


<img width="219" height="849" alt="Figure1" src="https://github.com/user-attachments/assets/30aa2760-96b9-42d2-b5e5-c0d24024fba4" />

**Figure 1.** NYC Taxi database schema and table hierarchy in PostgreSQL


#### 5. Running the API Service

Once the database is fully populated (via the importer), you are ready to start the API service. Follow these steps:

1. Open the project folder `/NYC-Taxi-FullStack/backend/api` in **PyCharm** (or your preferred IDE).
2. Run the `main.py` file.
3. The server should start on your local machine, displaying: `Running on http://127.0.0.1:5000`.

**To verify** that the API is working, open your browser and visit: **[http://127.0.0.1:5000/api/v1/boroughs](http://127.0.0.1:5000/api/v1/boroughs)** (see Figure 2).


<img width="366" height="534" alt="Figure2" src="https://github.com/user-attachments/assets/6e4abe70-2a47-4e4b-9cf3-eb812c434760" />

**Figure 2.** Running API on localhost


#### 6. API Overview (Backend Routes)

The API is built using **RESTful** principles. Each entity (boroughs, payment types, trips) has a specific endpoint, and actions are performed using standard HTTP methods: `GET` (read), `POST` (create), `DELETE` (remove), and `PATCH` (update). All programming capabilities of the code in the `main.py` file are presented in Table 2.

**Table 2.** The API's endpoints

| Endpoint | Method | Function / Description |
| :--- | :--- | :--- |
| `/api/v1/boroughs` | **GET** | Retrieves a list of all NYC boroughs. |
| `/api/v1/payment_types` | **GET** | Retrieves a list of all payment methods. |
| `/api/v1/payment_types` | **POST** | Creates a new payment method. |
| `/api/v1/payment_types/<id>` | **GET** | Retrieves details of a specific payment type by ID. |
| `/api/v1/payment_types/<id>` | **DELETE** | Removes a specific payment type. |
| `/api/v1/payment_types/<id>` | **PATCH** | Updates/Edits an existing payment type. |
| `/api/v1/rate_codes` | **GET** | Retrieves a list of all taxi rate codes. |
| `/api/v1/rate_codes/<id>` | **GET** | Retrieves details of a specific rate code by ID. |
| `/api/v1/rate_codes/<id>` | **DELETE** | Removes a specific rate code. |
| `/api/v1/rate_codes` | **POST** | Creates a new taxi rate code. |
| `/api/v1/rate_codes/<id>` | **PATCH** | Updates/Edits an existing rate code. |
| `/api/v1/service_zones` | **GET** | Retrieves a list of all service zones. |
| `/api/v1/zones` | **GET** | Retrieves a list of all taxi zones (pickup/drop-off locations). |
| `/api/v1/vendors` | **GET** | Retrieves a list of all taxi vendors operating in NYC. |
| `/api/v1/yellow_trips/<dt>/<step>/avg_amount` | **GET** | **Analytics:** Returns the average total amount of taxi trips grouped by `step` (year, month, or day) starting from the provided `dt` (epoch timestamp). |

---

### Backend Conclusion & Status

At this stage, the server-side infrastructure is fully operational.
* **Database:** Successfully migrated and optimized (indexes created).
* **API:** All endpoints are verified and responding with correct JSON payloads.

> **Current Status:** Backend development and database population are complete. The API is verified and ready for frontend integration in Android Studio.

---

## Frontend (Android Application)

The frontend part of this project is a native Android application built using **Kotlin** and **Jetpack Compose**. It serves as a tool for visualizing and editing previously downloaded data about New York city taxis for 2020-2025 years.

> **Note:** To ensure the application functions correctly, you must first have running the **API Server (Backend)**: `/NYC-Taxi-FullStack/backend/api/main.py`. The app connects to the local server via `http://10.0.2.2:5000/` (default address for the Android Emulator to access the host's localhost).

The entry point of the application is `MainActivity.kt`. It utilizes a `NavigationSuiteScaffold` to provide an adaptive UI that switches between a bottom navigation bar and a navigation rail based on the device's screen size. It manages the global state of navigation between the Analytics, Management, and Locations screens.

### Analytics Window

This window is the core of the data visualization logic. The following components work together to provide a seamless user experience:

* **`AnalyticsScreen.kt`**: The UI layer built with Jetpack Compose. It contains the date picker, granularity selection buttons (Year, Month, Day), and a custom-drawn **Canvas-based line chart** that dynamically scales to fit the data.
* **`AnalyticsViewModel`**: The logic layer that manages the UI state using `StateFlow`. It handles user interactions, triggers network requests through Coroutines, and processes the raw data for the chart.
* **`ApiService.kt`**: Defines the Retrofit interface for network communication. It uses an `OkHttpClient` with extended timeouts (5 minutes) to ensure stable data fetching from the backend.
* **`AnalyticsViewModelTest`**: Contains Unit Tests to verify the business logic, ensuring that state transitions and data updates within the ViewModel work correctly in isolation.

#### Testing

The application was thoroughly tested using the **Android Emulator** within **Android Studio**. 

**How it works:**

1.  The user selects a date via the Calendar dialog - button "Select a date (2020-2025 years)".
2.  Once a date is selected, the app automatically triggers a `GET` request to the following endpoint:
    `/api/v1/yellow_trips/<dt>/<step>/avg_amount`
where **parameters** is:
    * `<dt>`: this is the selected date converted into a **Unix Timestamp** (milliseconds). For example, if you select **01.01.2023**, it is converted to `1672531200000`.
    * `<step>`: defines the data granularity. By default, it is set to **"year"**.
3.  Every time the user changes the date (`<dt>`) or the granularity (`<step>`), a new API request is sent automatically.

#### Data from API

The API returns a list, depending on the chosen step, this list contains:

* **Year**: average trip amounts (`avg_amount`) for each month  (`pu_month`) (12 values ​​in the list) for the selected year in the date.
* **Month**: average trip amounts (`avg_amount`) for each day (`pu_day`) (may be 28, 29, 30, or 31 values ​​in the list) for the selected month in the date.
* **Day**: average trip amounts (`avg_amount`) for each hour (`pu_hour`) (24 values ​​in the list) for the selected day in the date.

#### Visualization

The received data is mapped to the line chart (in practice, I have 1-3 minutes of the process), providing a visual representation of taxi trip statistics in New York city. The results of the Analytics window's operation are demonstrated in **Figures 3-7**.


<img width="416" height="922" alt="Figure3" src="https://github.com/user-attachments/assets/60a36530-bed6-48c8-a566-df9d7f7b8ce0" />

**Figure 3.** First launch of the Android emulator


<img width="414" height="920" alt="Figure4" src="https://github.com/user-attachments/assets/c066a732-e866-4c8a-b906-db39a456b928" />

**Figure 4.** Selecting a date from the calendar


<img width="413" height="919" alt="Figure5" src="https://github.com/user-attachments/assets/130335e0-8128-4ddb-aa2c-fb9ebae2869b" />

**Figure 5.** A chart of statistics for average amount taxi trips for selected year


<img width="415" height="921" alt="Figure6" src="https://github.com/user-attachments/assets/d2c14afe-4ba5-47b1-a5cd-f9ddd252cc33" />

**Figure 6.** A chart of statistics for average amount taxi trips for selected month


<img width="415" height="920" alt="Figure7" src="https://github.com/user-attachments/assets/5bbc9994-81c2-4971-a8c3-1bae715d323b" />

**Figure 7.** A chart of statistics for average amount taxi trips for selected day


### Locations Window

The Locations window provides a comprehensive interface for exploring and filtering New York City taxi zones. It is designed to handle large datasets efficiently, offering users both granular search capabilities and multi-layered filtering options.

* **`LocationsScreen.kt`**: The UI layer built with Jetpack Compose. It features a top navigation bar, a browser-standard search bar, collapsible filter groups (Boroughs and Service Zones), and a structured data table for results.
* **`LocationsViewModel.kt`**: The business logic layer that manages the UI state using `StateFlow`. It reactively combines search queries and filter selections to provide an instantly updated list of locations.
* **`ApiService.kt`**: Defines the Retrofit interface for network communication. It handles requests to `/api/v1/boroughs`, `/api/v1/service_zones`, and `/api/v1/zones` to populate the screen with live data.
* **`UIComponents.kt`**: A shared utility file containing reusable components like `NavigationArrow`, ensuring a consistent design and navigation experience across all application screens. **This file was created later, after Analytics Window, during work on the Locations Window and implemented in `LocationsScreen.kt` and `AnalyticsScreen.kt` and is planned to be used in `ManagementScreen.kt`**.

#### Search Logic

The search functionality is engineered to mirror modern browser standards for a seamless user experience:
1.  **Flexible Matching**: The search is case-insensitive and automatically ignores leading or trailing whitespaces.
2.  **Multi-term Querying**: Users can enter multiple words separated by spaces, commas, or slashes. The app processes each term individually and displays a unified list of results without duplicates.
3.  **Keyboard Integration**: For better accessibility, pressing the "Enter" key on the virtual keyboard triggers the search action, identical to clicking the physical "Search" button.
4.  **Input Validation**: The search field is restricted to Latin letters, spaces, slashes, and commas. If a user enters a period, it is automatically converted to a comma to maintain query consistency.

#### Advanced Filtering System

The window employs a sophisticated, reactive filtering logic based on the data fetched from the API:

* **Dynamic Checkboxes**: Filter groups for Boroughs and Service Zones are generated dynamically according to the records retrieved from the backend.
* **"All" Selector Logic**:
    * By default, the "All" checkbox is active and locked in the 'ON' position.
    * If any specific sub-item is deselected, the "All" checkbox is automatically deactivated and unlocked.
    * Re-selecting "All" resets the entire group to the active state and re-locks the switch.
* **Collapsible UI**: To maximize screen real estate, the filter groups are nested within an animated collapsible element that users can toggle via an arrow icon.

#### Result Presentation

The final list of zones is presented in a table under the section **"List of searched zone names"**. The table provides the following columns (see **Figure 8**):
* **ID**: The unique `LocationID`.
* **Zone Name**: The specific name of the taxi zone (including unique markers like "N/A").
* **Service Zone**: The category of service (e.g., Yellow Zone, Boro Zone, EWR).
* **Borough**: The specific NYC borough the zone belongs to.


<img width="414" height="920" alt="Figure8" src="https://github.com/user-attachments/assets/4125e1a4-e127-46f7-b933-00564b6ebd8e" />

**Figure 8.** Locations window view


Every interaction-whether typing a single letter or toggling a checkbox—automatically triggers a re-filtering of the data, ensuring the table always displays the most relevant information.





