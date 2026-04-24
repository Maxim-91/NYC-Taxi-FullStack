import contextlib
import os

import psycopg2

from factories.boroughs import create_boroughs_repo, create_boroughs_service
from factories.pt import create_pt_repo, create_pt_service
from factories.rate_codes import create_rt_repo, create_rt_service
from factories.service_zones import create_sz_repo, create_sz_service
from factories.vendors import create_vendors_repo, create_vendors_service
from factories.yellow_trips import create_yellow_trips_repo, create_yellow_trips_service
from factories.zones import create_zones_repo, create_zones_service


@contextlib.contextmanager
def get_db_conn():
    conn = None
    try:
        conn = psycopg2.connect(database=os.getenv("DB_NAME"), user=os.getenv("DB_USER"), password=os.getenv("DB_PWD"))
        yield conn
    finally:
        if conn is not None:
            conn.close()


def get_pt_service(func):
    def wrapper(*args, **kwargs):
        with get_db_conn() as conn:
            repo = create_pt_repo(conn)
            service = create_pt_service(repo)
            return func(service, *args, **kwargs)

    return wrapper


def get_boroughs_service(func):
    def wrapper(*args, **kwargs):
        with get_db_conn() as conn:
            repo = create_boroughs_repo(conn)
            service = create_boroughs_service(repo)
            return func(service, *args, **kwargs)

    return wrapper


def get_rt_service(func):
    def wrapper(*args, **kwargs):
        with get_db_conn() as conn:
            repo = create_rt_repo(conn)
            service = create_rt_service(repo)
            return func(service, *args, **kwargs)

    return wrapper


def get_sz_service(func):
    def wrapper(*args, **kwargs):
        with get_db_conn() as conn:
            repo = create_sz_repo(conn)
            service = create_sz_service(repo)
            return func(service, *args, **kwargs)

    return wrapper

def get_vendors_service(func):
    def wrapper(*args, **kwargs):
        with get_db_conn() as conn:
            repo = create_vendors_repo(conn)
            service = create_vendors_service(repo)
            return func(service, *args, **kwargs)

    return wrapper

def get_zones_service(func):
    def wrapper(*args, **kwargs):
        with get_db_conn() as conn:
            repo = create_zones_repo(conn)
            service = create_zones_service(repo)
            return func(service, *args, **kwargs)

    return wrapper


def get_yt_service(func):
    def wrapper(*args, **kwargs):
        with get_db_conn() as conn:
            repo = create_yellow_trips_repo(conn)
            service = create_yellow_trips_service(repo)
            return func(service, *args, **kwargs)

    return wrapper

