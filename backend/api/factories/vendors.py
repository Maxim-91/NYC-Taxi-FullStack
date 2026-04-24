from repositories.vendors_abc_repository import VendorsRepository
from repositories.vendors_pg_repository import VendorsPgRepository
from services.vendors_abc_service import VendorsService
from services.vendors_local_db_service import VendorsLocalDBService


def create_vendors_repo(conn) -> VendorsRepository:
    return VendorsPgRepository(conn)

def create_vendors_service(repo: VendorsRepository) -> VendorsService:
    return VendorsLocalDBService(repo)