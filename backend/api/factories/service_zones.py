from repositories.sz_abc_repository import SzRepository
from repositories.sz_pg_repository import SzPgRepository
from services.sz_abc_service import SzService
from services.sz_local_db_service import SzLocalDBService


def create_sz_repo(conn) -> SzRepository:
    return SzPgRepository(conn)

def create_sz_service(repo: SzRepository) -> SzService:
    return SzLocalDBService(repo)