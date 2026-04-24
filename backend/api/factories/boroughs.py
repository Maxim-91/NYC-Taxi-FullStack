from repositories.boroughs_abc_repository import BoroughsRepository
from repositories.boroughs_pg_repo import BoroughsPgRepository
from services.boroughs_abc_service import BoroughsService
from services.boroughs_local_db_service import BoroughsLocalDBService


def create_boroughs_repo(conn) -> BoroughsRepository:
    return BoroughsPgRepository(conn)

def create_boroughs_service(repo: BoroughsRepository) -> BoroughsService:
    return BoroughsLocalDBService(repo)