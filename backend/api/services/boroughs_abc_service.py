import abc


class BoroughsService(abc.ABC):
    @abc.abstractmethod
    def get_all(self):
        raise NotImplementedError()