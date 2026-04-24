from repositories.pt_abc_repository import PtRepository
from repositories.pt_pg_repo import PtPgRepository
from services.pt_abc_service import PtService
from services.pt_local_db_service import PtLocalDBService


def create_pt_repo(conn) -> PtRepository:
    return PtPgRepository(conn)

def create_pt_service(repo: PtRepository) -> PtService:
    return PtLocalDBService(repo)