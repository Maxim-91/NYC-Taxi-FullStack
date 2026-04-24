from repositories.vendors_abc_repository import VendorsRepository
from services.vendors_abc_service import VendorsService


class VendorsLocalDBService(VendorsService):

    def __init__(self, repo: VendorsRepository) -> None:
        self.repo = repo


    def get_all(self):
        return self.repo.get_all()