from repositories.rt_abc_repository import RtRepository
from services.rt_abc_service import RtService


class RtLocalDBService(RtService):

    def __init__(self, repo: RtRepository) -> None:
        self.repo = repo

    def get_all(self):
        return self.repo.get_all()

    def get_by_id(self, rate_code_id):
        return self.repo.get_by_id(rate_code_id)

    def remove_by_id(self, rate_code_id):
        self.repo.get_by_id(rate_code_id)
        return self.repo.remove_by_id(rate_code_id)

    def add(self, req_data):
        new_id = self.repo.add(req_data)
        return {'code': req_data['code'], 'RatecodeID': new_id}

    def edit(self, rate_code_id, req_data):
        self.repo.get_by_id(rate_code_id)
        updated_data = self.repo.edit(rate_code_id, req_data)
        updated_data['RatecodeID'] = rate_code_id
        return updated_data