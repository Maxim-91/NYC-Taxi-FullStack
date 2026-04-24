from repositories.zones_abc_repository import ZonesRepository
from services.zones_abc_service import ZonesService


class ZonesLocalDBService(ZonesService):

    def __init__(self, repo: ZonesRepository) -> None:
        self.repo = repo

    def get_all(self):
        return self.repo.get_all()
