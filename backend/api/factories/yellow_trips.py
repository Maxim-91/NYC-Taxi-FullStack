from repositories.trips_abc_repository import TripsRepository
from repositories.trips_pg_repository import TripsPgRepo
from services.yellow_trips_abc_service import YellowTripsService
from services.yellow_trips_local_db_service import YellowTripsLocalDBService


def create_yellow_trips_repo(conn) -> TripsRepository:
    return TripsPgRepo(conn)

def create_yellow_trips_service(repo: TripsRepository) -> YellowTripsService:
    return YellowTripsLocalDBService(repo)