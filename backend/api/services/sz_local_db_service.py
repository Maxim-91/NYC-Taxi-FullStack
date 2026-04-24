from repositories.sz_abc_repository import SzRepository
from services.sz_abc_service import SzService


class SzLocalDBService(SzService):

    def __init__(self, repo: SzRepository) -> None:
        self.repo = repo


    def get_all(self):
        return self.repo.get_all()