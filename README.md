# NYC Taxi FullStack Project Report

---

## Backend

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

Table 1. Text menu of action options

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

> **Note 2:** On option 2: A file 5016.dat.gz - 5.24 GB will be downloaded, make sure you have enough free disk space.

> **Note 3:** On option 9: This process generates massive temporary files. The 5.24 GB compressed file can theoretically expand to 15-20 GB once imported into the database, and the index creation process requires an additional 10-15 GB of free space. Theoretically, a total of 35 GB is enough, but **in my specific practical case, option 9 required over 50 GB of free disk space** to complete successfully.

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

Figure 2. Running API on localhost

---

#### 6. API Overview (Backend Routes)
The API is built using **RESTful** principles. Each entity (boroughs, payment types, trips) has a specific endpoint, and actions are performed using standard HTTP methods: `GET` (read), `POST` (create), `DELETE` (remove), and `PATCH` (update).

Table 2. The API's endpoints

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


---
