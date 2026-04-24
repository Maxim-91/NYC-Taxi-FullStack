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

The script provides a text-based menu. **Crucial:** You must execute the options sequentially from 1 to 9 to ensure data integrity.

| Option | Action (Finnish) | Description |
| :--- | :--- | :--- |
| **1** | `luo tietokanta` | **Schema Creation:** The script creates the `nyc_taxi` database and all required tables (yellow_trips, zones, etc.). |
| **2** | `lataa valmis datapaketti` | **Data Download:** Downloads compressed archives from Google Drive into the `/data` folder. (Duration depends on your internet speed). |
| **3 - 8** | `vendorit, payment_typet...` | **Lookup Tables:** Populates small reference tables (boroughs, payment types, rate codes, etc.). This is nearly instantaneous. |
| **9** | `yellow_trips` | **Main Import:** The final step. Performs a high-performance import of the primary trip data. This is the longest process. |

> **Note:** Make sure your PostgreSQL service is running and your `.env` credentials are correct before starting with option 1.


---
