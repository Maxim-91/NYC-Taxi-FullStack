from repositories.boroughs_abc_repository import BoroughsRepository
from services.boroughs_abc_service import BoroughsService


class BoroughsLocalDBService(BoroughsService):

    def __init__(self, repo: BoroughsRepository) -> None:
        self.repo = repo


    def get_all(self):
        return self.repo.get_all()