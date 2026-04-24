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

The script provides a text-based menu. **Crucial:** You must execute the options sequentially from 1 to 10 to ensure data integrity and proper database configuration.

| Option | Action (Finnish) | Description |
| :--- | :--- | :--- |
| **1** | `luo tietokanta` | **Schema Creation:** Creates the `nyc_taxi` database and all table structures (Yellow Trips, Zones, etc.). |
| **2** | `lataa valmis datapaketti Google Drivesta` | **Data Download:** Fetches compressed `.dump` files from Google Drive into the `./data` folder. |
| **3** | `vendorit` | **Lookup Table:** Populates the list of taxi vendors (CMT, VeriFone, etc.). |
| **4** | `payment_typet` | **Lookup Table:** Populates payment methods (Credit Card, Cash, etc.). |
| **5** | `borought` | **Lookup Table:** Populates the list of NYC boroughs (Manhattan, Brooklyn, etc.). |
| **6** | `service_zonet` | **Lookup Table:** Populates service zone categories. |
| **7** | `rate_codet` | **Lookup Table:** Populates rate codes (Standard, JFK, Newark, etc.). |
| **8** | `zonet` | **Lookup Table:** Maps specific Location IDs to names and boroughs (requires `taxi_zone_lookup.csv`). |
| **9** | `yellow_trips` | **Main Data Import:** Restores the massive dataset of taxi trips into the database using `pg_restore`. |
| **10** | `aseta oikeat auto incrementtien arvot` | **Sequence Fix:** Synchronizes database IDs so new entries won't cause unique constraint errors. |

> **Note 1:** Make sure your PostgreSQL service is running and your `.env` credentials are correct before starting with option 1.

> **Note 2:** On option 2, a file 5016.dat.gz - 5.24GB will be downloaded, make sure you have enough free disk space.


---
