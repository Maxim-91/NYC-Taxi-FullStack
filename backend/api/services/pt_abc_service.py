import abc


class PtService(abc.ABC):
    @abc.abstractmethod
    def get_all(self):
        raise NotImplementedError()

    @abc.abstractmethod
    def get_by_id(self, payment_type_id):
        raise NotImplementedError()

    @abc.abstractmethod
    def remove_by_id(self, payment_type_id):
        raise NotImplementedError()

    @abc.abstractmethod
    def add(self, req_data):
        raise NotImplementedError()

    @abc.abstractmethod
    def edit(self, payment_type_id,  req_data):
        raise NotImplementedError()