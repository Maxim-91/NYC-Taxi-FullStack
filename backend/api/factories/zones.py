from repositories.zones_abc_repository import ZonesRepository
from repositories.zones_pg_repository import ZonesPgRepository
from services.zones_abc_service import ZonesService
from services.zones_local_db_service import ZonesLocalDBService


def create_zones_repo(conn) -> ZonesRepository:
    return ZonesPgRepository(conn)

def create_zones_service(repo: ZonesRepository) -> ZonesService:
    return ZonesLocalDBService(repo)