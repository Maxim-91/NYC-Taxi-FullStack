import abc


class RtService(abc.ABC):
    @abc.abstractmethod
    def get_all(self):
        raise NotImplementedError()

    @abc.abstractmethod
    def get_by_id(self, rate_code_id):
        raise NotImplementedError()

    @abc.abstractmethod
    def remove_by_id(self, rate_code_id):
        raise NotImplementedError()

    @abc.abstractmethod
    def add(self, req_data):
        raise NotImplementedError()

    @abc.abstractmethod
    def edit(self, rate_code_id, req_data):
        raise NotImplementedError()