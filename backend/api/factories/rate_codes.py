from repositories.rt_abc_repository import RtRepository
from repositories.rt_pg_repo import RtPgRepository
from services.rt_abc_service import RtService
from services.rt_local_db_service import RtLocalDBService


def create_rt_repo(conn) -> RtRepository:
    return RtPgRepository(conn)

def create_rt_service(repo: RtRepository) -> RtService:
    return RtLocalDBService(repo)