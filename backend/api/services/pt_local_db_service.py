from repositories.pt_abc_repository import PtRepository
from services.pt_abc_service import PtService


class PtLocalDBService(PtService):

    def __init__(self, repo: PtRepository) -> None:
        self.repo = repo

    def get_all(self):
        return self.repo.get_all()

    def get_by_id(self, payment_type_id):
        return self.repo.get_by_id(payment_type_id)

    def remove_by_id(self, payment_type_id):
        self.repo.get_by_id(payment_type_id)
        return self.repo.remove_by_id(payment_type_id)

    def add(self, req_data):
        new_id = self.repo.add(req_data)
        return {'payment_type': req_data['payment_type'], 'id': new_id}

    def edit(self, payment_type_id, req_data):
        self.repo.get_by_id(payment_type_id)
        updated_data = self.repo.edit(payment_type_id, req_data)
        updated_data['id'] = payment_type_id
        return updated_data
